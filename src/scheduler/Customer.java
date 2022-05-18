package scheduler;

public class Customer {
    private int customerID;
    private String customerName;
    private String customerPhone;
    private String customerAddress;
    private String customerDivision;
    private String customerCountry;
    private int customerPostal;
    public Customer(
            int customerID,
            String customerName,
            String customerPhone,
            String customerAddress,
            String customerDivision,
            String customerCountry,
            int customerPostal
    ){
        this.customerID = customerID;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.customerAddress = customerAddress;
        this.customerDivision = customerDivision;
        this.customerCountry = customerCountry;
        this.customerPostal = customerPostal;

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

    public int getCustomerPostal() {
        return customerPostal;
    }

}
