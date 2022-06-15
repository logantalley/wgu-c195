package scheduler;

public class apptReport {
    private String timeFrame;
    private String apptType;
    private int apptCount;

    public apptReport(String timeFrame, String apptType, int apptCount){
        this.timeFrame = timeFrame;
        this.apptType = apptType;
        this.apptCount = apptCount;
    }

    public int getApptCount() {
        return apptCount;
    }

    public String getApptType() {
        return apptType;
    }

    public String getTimeFrame() {
        return timeFrame;
    }
}
