package scheduler;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Customer {
    private int customerID;
    private String customerName;
    private String customerPhone;
    private String customerAddress;
    private String customerDivision;
    private String customerCountry;
    private String customerPostal;
    private ObservableList<Schedule> customerAppts = FXCollections.observableArrayList();
    public Customer(
            int customerID,
            String customerName,
            String customerPhone,
            String customerAddress,
            String customerDivision,
            String customerCountry,
            String customerPostal,
            ObservableList<Schedule> customerAppts
    ){
        this.customerID = customerID;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.customerAddress = customerAddress;
        this.customerDivision = customerDivision;
        this.customerCountry = customerCountry;
        this.customerPostal = customerPostal;
        this.customerAppts = customerAppts;

    }

    public int getCustomerID() {
        return customerID;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public String getCustomerDivision() {
        return customerDivision;
    }

    public String getCustomerCountry() {
        return customerCountry;
    }

    public String getCustomerPostal() {
        return customerPostal;
    }

    public ObservableList<Schedule> getCustomerAppts() {
        return customerAppts;
    }
}

