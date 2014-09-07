package allen.gc;

import java.util.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import allen.gc.util.DateUtil;
import allen.gc.util.FileUtil;

/**
 * DataHolder.
 * 
 * @author xinzhi.zhang
 * */
public class DataHolder {

    private static Map<String, List<ParseResult>>                parseResult = new HashMap<String, List<ParseResult>>();
    /**
     * <pre>
     * GCDataType ->  map ( event-> map (time->gcData) )
     * </pre>
     * */
    public Map<GCDataType, Map<String, TreeMap<String, GCData>>> datas       = new HashMap<GCDataType, Map<String, TreeMap<String, GCData>>>();

    public DataHolder() {

        for (GCDataType gcDataType : GCDataType.values()) {
            datas.put(gcDataType,
                    new HashMap<String, TreeMap<String, GCData>>());
        }
        if (ControlData.logFile == null) {
            return;
        }

        String filePath = ControlData.logFile.getAbsolutePath();

        if (parseResult.get(filePath) == null) {
            List<ParseResult> cache = new LinkedList<ParseResult>();
            List<String> lines = FileUtil.readLineFromFile(ControlData.logFile);
            parseLinesAndAddToCache(cache, lines);
            parseResult.put(filePath, cache);
        }

        parseCache(parseResult.get(filePath));
    }

    private void parseCache(List<ParseResult> cache) {
        for (ParseResult parseResult : cache) {
            if (parseResult.gcType.getClass() == GCEventType.class) {
                add(parseResult.time, (GCEventType) parseResult.gcType);
                continue;
            }
            if (parseResult.gcType.getClass() == GCMemoryType.class) {
                add(parseResult.time, (GCMemoryType) parseResult.gcType,
                        parseResult.result);
                continue;
            }
            if (parseResult.gcType.getClass() == GCConsumeTimeType.class) {
                add(parseResult.time, (GCConsumeTimeType) parseResult.gcType,
                        parseResult.result);
                continue;
            }
            throw new RuntimeException(parseResult.gcType.toString());
        }
    }

    public List<String> getSortedTimeLine() {
        TreeSet<String> times = new TreeSet<String>();

        for (GCDataType gcDataType : GCDataType.values()) {

            Map<String, TreeMap<String, GCData>> temData = datas
                    .get(gcDataType);
            for (String event : temData.keySet()) {
                times.addAll(temData.get(event).keySet());
            }
        }

        List<String> timeLines = new ArrayList<String>();

        for (String time : times) {
            if (isTimeInRange(time)) {
                timeLines.add(time);
            }
        }

        return timeLines;
    }

    private boolean isTimeInRange(String time) {
        Date startDate = ControlData.startDate;
        Date endDate = ControlData.endDate;

        if (startDate == null && endDate == null) {
            return true;
        }

        Date tem = DateUtil.parseDate(time);

        if (startDate == null) {
            return !tem.after(endDate);
        }
        if (endDate == null) {
            return !tem.before(startDate);
        }
        return !tem.before(startDate) && !tem.after(endDate);
    }

    private long parseValue(final String line, String end,
            String... startStrings) {
        return Long.valueOf(parseString(line, end, startStrings));
    }

    private String parseString(final String line, String end,
            String... startStrings) {
        String tem = line;
        try {
            for (int i = 0; i < startStrings.length; i++) {
                int index = tem.indexOf(startStrings[i]);
                tem = tem.substring(index + startStrings[i].length());
            }

            int index = tem.indexOf(end);
            tem = tem.substring(0, index);

            return tem;
        } catch (Exception e) {
            System.out.println("parse line=" + line);
            throw new RuntimeException("parse line=" + line, e);
        }
    }

    private static class ParseResult {
        String time;
        Object gcType;
        String result;
    }

    private void parseLinesAndAddToCache(List<ParseResult> cache,
            List<String> lines) {
        for (int i = 0; i < lines.size(); i++) {
            String s = lines.get(i);

            if (s == null) {
                continue;
            }

            if (s.contains("JAVA_OPTS")) {
                continue;
            }

            if (s.startsWith("stdout")) {
                int index = s.indexOf(":");
                if (index != -1) {
                    s = s.substring(index + 1);
                }
            }

            if (s.length() < Config.TimeStrLength) {
                continue;
            }

            String time = s.substring(0, Config.TimeStrLength);

            if (s.contains("Full GC")) {

                if (i < lines.size() - 1
                        && lines.get(i + 1).contains("concurrent mode failure")) {
                    s = s + lines.get(i + 1);
                    i++;

                    addCache(cache, time, GCEventType.Event_FullGC_CMF);

                    String consumeTime = parseString(s, "secs]",
                            "(concurrent mode failure): ", ",");
                    addCache(cache, time, GCConsumeTimeType.Time_FullGC_CMF,
                            consumeTime);

                    //2014-08-21T12:59:21.014+0800: [Full GC [CMS2014-08-21T12:59:22.872+0800: [CMS-concurrent-mark: 3.046/3.061 secs] [Times: user=6.03 sys=0.31, real=3.06 secs] 
                    // (concurrent mode failure): 1310719K->1310719K(1310720K), 8.8504440 secs] 2018559K->1750445K(2018560K), [CMS Perm : 73237K->73237K(196608K)], 8.8509720 secs] [Times: user=8.83 sys=0.00, real=8.85 secs] 

                    long static_o = parseValue(s, "K", "(", "(");
                    long static_eso = parseValue(s, "K", "(", "(", "(");
                    long static_es = static_eso - static_o;

                    long o = parseValue(s, "K", "(concurrent mode failure): ");
                    long aftero = parseValue(s, "K",
                            "(concurrent mode failure): ", "->");

                    long eso = parseValue(s, "K",
                            "(concurrent mode failure): ", "secs] ");
                    long aftereso = parseValue(s, "K",
                            "(concurrent mode failure): ", "secs] ", "->");

                    addCache(cache, time, GCMemoryType.MM_FixES_And_Old,
                            (static_es + o) + "");
                    addCache(cache, time,
                            GCMemoryType.MM_AfterGC_FixES_And_Old,
                            (static_es + aftero) + "");

                    //
                    addCache(cache, time, GCMemoryType.MM_ESO, (eso) + "");
                    addCache(cache, time, GCMemoryType.MM_AfterGC_ESO,
                            (aftereso) + "");

                } else {

                    addCache(cache, time, GCEventType.Event_FullGC);

                    String consumeTime = parseString(s, "secs]", ",");
                    addCache(cache, time, GCConsumeTimeType.Time_FullGC,
                            consumeTime);

                    //2014-08-21T12:59:11.698+0800: [Full GC [CMS: 1310720K->1310719K(1310720K), 7.5913440 secs] 2018559K->1726579K(2018560K), [CMS Perm : 73237K->73234K(196608K)], 7.5918010 secs] [Times: user=7.62 sys=0.00, real=7.59 secs] 

                    long static_eso = parseValue(s, "K", "(", "(");
                    long static_o = parseValue(s, "K", "(");

                    long o = parseValue(s, "K", "CMS: ");
                    long aftero = parseValue(s, "K", "CMS: ", "->");

                    long eso = parseValue(s, "K", "secs] ");
                    long aftereso = parseValue(s, "K", "secs] ", "->");

                    long static_es = static_eso - static_o;
                    long es = eso - o;
                    long afteres = aftereso - aftero;

                    addCache(cache, time, GCMemoryType.MM_FixES_And_Old,
                            (static_es + o) + "");
                    addCache(cache, time,
                            GCMemoryType.MM_AfterGC_FixES_And_Old,
                            (static_es + aftero) + "");

                    addCache(cache, time, GCMemoryType.MM_ESO, (eso) + "");
                    addCache(cache, time, GCMemoryType.MM_AfterGC_ESO,
                            (aftereso) + "");

                    addCache(cache, time, GCMemoryType.MM_ES, (es) + "");
                    addCache(cache, time, GCMemoryType.MM_AfterGC_ES, (afteres)
                            + "");
                }

                continue;
            }

            if (s.contains("ParNew")) {

                if (i < lines.size() - 1
                        && lines.get(i + 1).contains("concurrent mode failure")) {
                    s = s + lines.get(i + 1);
                    i++;

                    addCache(cache, time, GCEventType.Event_ParNew_CMF);

                    String consumeTime = parseString(s, "secs]",
                            "(concurrent mode failure): ", ",");
                    addCache(cache, time, GCConsumeTimeType.Time_ParNew_CMF,
                            consumeTime);

                    //2014-08-21T12:57:31.039+0800: [GC [ParNew: 707840K->707840K(707840K), 0.0000600 secs][CMS2014-08-21T12:57:32.419+0800: [CMS-concurrent-sweep: 1.851/1.858 secs] [Times: user=2.66 sys=0.23, real=1.86 secs] 
                    // (concurrent mode failure): 1043351K->1061176K(1310720K), 6.7085780 secs] 1751191K->1061176K(2018560K), [CMS Perm : 73247K->73199K(196608K)], 6.7091580 secs] [Times: user=6.72 sys=0.00, real=6.70 secs] 

                    long static_o = parseValue(s, "K", "(", "(", "(");
                    long static_eso = parseValue(s, "K", "(", "(", "(", "(");
                    long static_es = static_eso - static_o;

                    long o = parseValue(s, "K", "(concurrent mode failure): ");
                    long aftero = parseValue(s, "K",
                            "(concurrent mode failure): ", "->");

                    long eso = parseValue(s, "K",
                            "(concurrent mode failure): ", "secs] ");
                    long aftereso = parseValue(s, "K",
                            "(concurrent mode failure): ", "secs] ", "->");

                    addCache(cache, time, GCMemoryType.MM_FixES_And_Old,
                            (static_es + o) + "");
                    addCache(cache, time,
                            GCMemoryType.MM_AfterGC_FixES_And_Old,
                            (static_es + aftero) + "");

                    //
                    addCache(cache, time, GCMemoryType.MM_ESO, (eso) + "");
                    addCache(cache, time, GCMemoryType.MM_AfterGC_ESO,
                            (aftereso) + "");

                } else {
                    addCache(cache, time, GCEventType.Event_ParNew);

                    String consumeTime = parseString(s, "secs]", ",");
                    addCache(cache, time, GCConsumeTimeType.Time_ParNew,
                            consumeTime);

                    //2014-08-20T17:48:32.872+0800: [GC [ParNew: 671811K->56895K(707840K), 0.0501820 secs] 1195100K->584461K(2018560K), 0.0506010 secs] [Times: user=0.19 sys=0.00, real=0.05 secs] 

                    long static_es = parseValue(s, "K", "(");
                    long static_eso = parseValue(s, "K", "(", "(");
                    long static_o = static_eso - static_es;

                    long es = parseValue(s, "K", "ParNew: ");
                    long afteres = parseValue(s, "K", "ParNew: ", "->");

                    long eso = parseValue(s, "K", "secs] ");
                    long aftereso = parseValue(s, "K", "secs] ", "->");

                    long o = eso - es;
                    long aftero = aftereso - afteres;

                    addCache(cache, time, GCMemoryType.MM_FixES_And_Old,
                            (static_es + o) + "");
                    //小GC不影响old，所以不显示。MM_AfterGC_FixES_And_Old
                    //                    add(time, MM_AfterGC_FixES_And_Old, (static_es + aftero)
                    //                            + "");

                    //小GC.
                    addCache(cache, time, GCMemoryType.MM_ES, (es) + "");
                    addCache(cache, time, GCMemoryType.MM_AfterGC_ES, (afteres)
                            + "");

                    //通用ESO。
                    addCache(cache, time, GCMemoryType.MM_ESO, (eso) + "");
                    addCache(cache, time, GCMemoryType.MM_AfterGC_ESO,
                            (aftereso) + "");

                    //显示一个参考值。
                    addCache(cache, time, GCMemoryType.MM_FixES, (static_es)
                            + "");
                    addCache(cache, time, GCMemoryType.MM_FixESO, (static_eso)
                            + "");

                }

                continue;
            }

            if (s.contains("CMS-initial-mark")) {

                addCache(cache, time, GCEventType.Event_CMS_initial_mark);

                String consumeTime = parseString(s, "secs]", ",");
                addCache(cache, time, GCConsumeTimeType.Time_CMS_initial_mark,
                        consumeTime);

                //2014-08-20T17:48:21.076+0800: [GC [1 CMS-initial-mark: 894430K(1310720K)] 937193K(2018560K), 0.0282240 secs] [Times: user=0.03 sys=0.00, real=0.03 secs] 

                long static_eso = parseValue(s, "K", "(", "(");
                long static_o = parseValue(s, "K", "(");
                long o = parseValue(s, "K", "CMS-initial-mark: ");
                addCache(cache, time, GCMemoryType.MM_FixES_And_Old,
                        (static_eso - static_o + o) + "");
                continue;
            }

            if (s.contains("CMS-remark")) {

                addCache(cache, time, GCEventType.Event_CMS_remark);

                String consumeTime = parseString(s, "secs]", ",");
                addCache(cache, time, GCConsumeTimeType.Time_CMS_remark,
                        consumeTime);

                //2014-08-20T17:48:26.776+0800: [GC[YG occupancy: 376368 K (707840 K)][Rescan (parallel) , 0.1614480 secs][weak refs processing, 0.0009130 secs][class unloading, 0.0093330 secs][scrub symbol & string tables, 0.0073960 secs] [1 CMS-remark: 894430K(1310720K)] 1270799K(2018560K), 0.1836520 secs] [Times: user=0.66 sys=0.00, real=0.19 secs]

                long static_eso = parseValue(s, "K", "CMS-remark: ", "(", "(");
                long static_o = parseValue(s, "K", "CMS-remark: ", "(");
                long o = parseValue(s, "K", "CMS-remark: ");
                addCache(cache, time, GCMemoryType.MM_FixES_And_Old,
                        (static_eso - static_o + o) + "");

                continue;
            }

            if (ControlData.Debug) {
                System.out.println("unhandle log = " + s);
            }
        }
    }

    private static void addCache(List<ParseResult> cache, String time,
            Object gcType, String result) {
        ParseResult parseResult = new ParseResult();
        parseResult.time = time;
        parseResult.gcType = gcType;
        parseResult.result = result;
        cache.add(parseResult);
    }

    private static void addCache(List<ParseResult> cache, String time,
            Object gcType) {
        ParseResult parseResult = new ParseResult();
        parseResult.time = time;
        parseResult.gcType = gcType;
        cache.add(parseResult);
    }

    private void add(String time, GCEventType gcEventType) {
        if (!ControlData.filterObjects.contains(gcEventType)) {
            return;
        }

        time = convertTime(time);
        String event = gcEventType.getDesc();

        Map<String, TreeMap<String, GCData>> temData = datas
                .get(GCDataType.Counter);

        TreeMap<String, GCData> map = temData.get(event);
        if (map == null) {
            map = new TreeMap<String, GCData>();
            temData.put(event, map);
        }

        GCData gcEventCounter = map.get(time);
        if (gcEventCounter == null) {
            gcEventCounter = new GCEventCounter();
            map.put(time, gcEventCounter);
        }
        gcEventCounter.add(time, event);
    }

    private void add(String time, GCMemoryType gcMemoryType, String memoryInK) {
        if (!ControlData.filterObjects.contains(gcMemoryType)) {
            return;
        }

        time = convertTime(time);

        String event = gcMemoryType.getDesc();

        Map<String, TreeMap<String, GCData>> temData = datas
                .get(GCDataType.Memory);

        TreeMap<String, GCData> map = temData.get(event);
        if (map == null) {
            map = new TreeMap<String, GCData>();
            temData.put(event, map);
        }

        GCData gcMMCounter = map.get(time);
        if (gcMMCounter == null) {
            gcMMCounter = new GCMMCounter();
            map.put(time, gcMMCounter);
        }
        gcMMCounter.add(time, event, memoryInK);
    }

    private void add(String time, GCConsumeTimeType gcConsumeTimeType,
            String consumeTimeInSecondStr) {
        if (!ControlData.filterObjects.contains(gcConsumeTimeType)) {
            return;
        }

        double consumeTimeInMilliSecond = Double
                .parseDouble(consumeTimeInSecondStr) * 1000D;

        time = convertTime(time);

        String event = gcConsumeTimeType.getDesc();

        Map<String, TreeMap<String, GCData>> temData = datas
                .get(GCDataType.ConsumeTime);

        TreeMap<String, GCData> map = temData.get(event);
        if (map == null) {
            map = new TreeMap<String, GCData>();
            temData.put(event, map);
        }

        GCData gcConsumeTimeCounter = map.get(time);
        if (gcConsumeTimeCounter == null) {
            gcConsumeTimeCounter = new GCConsumeTimeCounter();
            map.put(time, gcConsumeTimeCounter);
        }
        gcConsumeTimeCounter.add(time, event, consumeTimeInMilliSecond);

    }

    private String convertTime(String time) {

        String t = time.replace("T", " ");
        Date date = DateUtil.parseDate(t, DateUtil.SecondFormat);

        long ms = date.getTime();

        TimePrecision timePrecision = ControlData.timePrecision;

        Date newDate = new Date(ms / timePrecision.getMilliSeconds()
                * timePrecision.getMilliSeconds());

        return DateUtil.formatDate(newDate, timePrecision.getFormat());
    }

}
