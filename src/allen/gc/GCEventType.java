package allen.gc;

/**
 * GCEventType.
 * 
 * @author xinzhi.zhang
 * */
public enum GCEventType {

    Event_FullGC("Event_Full GC"),

    Event_FullGC_CMF("Event_Full GC(CMF)"),

    Event_ParNew("Event_ParNew"),

    Event_ParNew_CMF("Event_ParNew(CMF)"),

    Event_CMS_initial_mark("Event_CMS-initial-mark"),

    Event_CMS_remark("Event_CMS-remark");

    private String desc;

    public String getDesc() {
        return desc;
    }

    private GCEventType(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return desc;
    }
}
