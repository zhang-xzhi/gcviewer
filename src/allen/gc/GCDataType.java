package allen.gc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * GCDataType.
 * 
 * @author xinzhi.zhang
 * */
public enum GCDataType {

    Counter(GCEventType.values()),

    Memory(GCMemoryType.values()),

    ConsumeTime(GCConsumeTimeType.values()), ;

    private Object[] subTypes;

    public Object[] getSubTypes() {
        return subTypes;
    }

    private GCDataType(Object[] subTypes) {
        this.subTypes = subTypes;
    }

    public static List<Object> getGCDataSubTypeList() {
        List<Object> gcDataSubTypeList = new ArrayList<Object>();

        GCDataType[] gcDataTypes = GCDataType.values();
        for (GCDataType gcDataType : gcDataTypes) {
            gcDataSubTypeList.addAll(Arrays.asList(gcDataType.getSubTypes()));
        }
        return gcDataSubTypeList;
    }
}
