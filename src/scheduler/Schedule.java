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
    private String apptStart;
    private String apptEnd;
    private int customerID;
    private int userID;

    public Schedule(int apptID, String apptTitle, String apptDescription, String apptLocation, String apptContact, String apptType, String apptStart, String apptEnd, int customerID, int userID){
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

    public int getApptID(){ return apptID; }
    public String getApptTitle() { return apptTitle; }
    public String getApptDescription() { return apptDescription; }
    public String getApptLocation() { return apptLocation; }
    public String getApptContact() { return apptContact; }
    public String getApptType() { return apptType; }
    public String getApptStart() { return apptStart; }
    public String getApptEnd() { return apptEnd; }
    public int getCustomerID(){ return customerID; }
    public int getUserID(){ return userID; }



}
