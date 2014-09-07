package allen.gc;

import java.io.File;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * ControlData.
 * 
 * @author xinzhi.zhang
 * */
public class ControlData {

    public static TimePrecision timePrecision = TimePrecision.Hour;

    public static File          logFile       = null;

    public static boolean       Debug         = false;

    public static Date          startDate;
    public static Date          endDate;

    public static Set<Object>   filterObjects = new HashSet<Object>(
                                                      GCDataType
                                                              .getGCDataSubTypeList());
}
