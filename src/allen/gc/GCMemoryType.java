package allen.gc;

/**
 * GCMemoryDataType.
 * 
 * @author xinzhi.zhang
 * */
public enum GCMemoryType {

    MM_FixES("MM_Fix(ES)"),

    MM_FixESO("MM_Fix(ESO)"),

    MM_FixES_And_Old("MM_Fix(ES)+O"),

    MM_AfterGC_FixES_And_Old("MM_AfterGC_Fix(ES)+O"),

    MM_ES("MM_ES"),

    MM_AfterGC_ES("MM_AfterGC_ES"),

    MM_ESO("MM_ESO"),

    MM_AfterGC_ESO("MM_AfterGC_ESO"), ;

    private String desc;

    public String getDesc() {
        return desc;
    }

    private GCMemoryType(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return desc;
    }
}
