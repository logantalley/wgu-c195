package scheduler;

public class Contact {
    private int contactID;
    private String contactName;
    private String contactEmail;

    public Contact(int contactID, String contactName, String contactEmail){
        this.contactID = contactID;
        this.contactName = contactName;
        this.contactEmail = contactEmail;
    }

    public int getContactID() {
        return contactID;
    }

    public String getContactName() {
        return contactName;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    @Override
    public String toString(){
        return getContactName();
    }
}
