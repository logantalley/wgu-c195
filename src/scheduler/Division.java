package scheduler;

public class Division {
    private String divisionName;
    private int divisionID;
    public Division(String divisionName, Integer divisionID) {
        this.divisionName = divisionName;
        this.divisionID = divisionID;
    }

    public String getDivisionName() {
        return divisionName;
    }

    public int getDivisionID() {
        return divisionID;
    }
}

