package allen.gc;

public interface GCData {

    String getEvent();

    String getTime();

    double getValue();

    void add(String time, String event);

    void add(String time, String event, String memoryInK);

    void add(String time, String event, double consumeTimeInMilliSecond);

}

class GCEventCounter implements GCData {

    private String event;
    private String time;
    private int    count;

    @Override
    public void add(String time, String event) {
        this.time = time;
        this.event = event;
        count++;
    }

    @Override
    public String getEvent() {
        return event;
    }

    @Override
    public String getTime() {
        return time;
    }

    @Override
    public double getValue() {
        return count;
    }

    @Override
    public void add(String time, String event, String memoryInK) {
        throw new RuntimeException();
    }

    @Override
    public void add(String time, String event, double consumeTimeInMilliSecond) {
        throw new RuntimeException();
    }

}

class GCMMCounter implements GCData {

    private String event;
    private String time;
    private int    count;
    private double memoryInM;

    @Override
    public void add(String time, String event, String memoryInK) {
        this.time = time;
        this.event = event;
        this.count++;
        double value = Long.parseLong(memoryInK) / 1024;
        this.memoryInM += value;
    }

    @Override
    public String getEvent() {
        return event;
    }

    @Override
    public String getTime() {
        return time;
    }

    @Override
    public double getValue() {
        return memoryInM / count;
    }

    @Override
    public void add(String time, String event) {
        throw new RuntimeException();
    }

    @Override
    public void add(String time, String event, double consumeTimeInMilliSecond) {
        throw new RuntimeException();
    }

}

class GCConsumeTimeCounter implements GCData {

    private String event;
    private String time;
    private double consumeTimeInMilliSecond;

    @Override
    public String getEvent() {
        return event;
    }

    @Override
    public String getTime() {
        return time;
    }

    @Override
    public double getValue() {
        return consumeTimeInMilliSecond;
    }

    @Override
    public void add(String time, String event) {
        throw new RuntimeException();
    }

    @Override
    public void add(String time, String event, String memoryInK) {
        throw new RuntimeException();
    }

    @Override
    public void add(String time, String event, double consumeTimeInMilliSecond) {
        this.time = time;
        this.event = event;
        this.consumeTimeInMilliSecond += consumeTimeInMilliSecond;
    }
}