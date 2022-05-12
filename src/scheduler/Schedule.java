package scheduler;


import java.time.LocalDateTime;

/**
 * This class just helps facilitate easier adding of data to a TableView
 */
public class Schedule {
    private int apptID;
    private String apptTitle;
    private String apptDescription;
    private String apptLocation;
    private String apptContact;
    private String apptType;
    private LocalDateTime apptStart;
    private LocalDateTime apptEnd;
    private int customerID;
    private int userID;
    public Schedule(int apptID, String apptTitle, String apptDescription, String apptLocation, String apptContact, String apptType, LocalDateTime apptStart, LocalDateTime apptEnd, int customerID, int userID){
        this.apptID = apptID;
        this.apptTitle = apptTitle;
        this.apptDescription = apptDescription;
        this.apptLocation = apptLocation;
        this.apptContact = apptContact;
        this.apptType = apptType;
        this.apptStart = apptStart;
        this.apptEnd = apptEnd;
        this.customerID = customerID;
        this.userID = userID;
    }
}
