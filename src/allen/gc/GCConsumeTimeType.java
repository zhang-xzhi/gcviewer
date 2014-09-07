package allen.gc;

/**
 * GCConsumeTimeType.
 * 
 * @author xinzhi.zhang
 * */
public enum GCConsumeTimeType {

    Time_FullGC("Time_Full GC"),

    Time_FullGC_CMF("Time_Full GC(CMF)"),

    Time_ParNew("Time_ParNew"),

    Time_ParNew_CMF("Time_ParNew(CMF)"),

    Time_CMS_initial_mark("Time_CMS-initial-mark"),

    Time_CMS_remark("Time_CMS-remark");

    private String desc;

    public String getDesc() {
        return desc;
    }

    private GCConsumeTimeType(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return desc;
    }
}
