package scheduler;

public class customerReport {
    private String countryName;
    private int customerCount;

    /**
     * Constructor to build object for Customer Report
     * @param countryName String name of Country
     * @param customerCount Integer count of Customers
     */
    public customerReport(String countryName, int customerCount){
        this.countryName = countryName;
        this.customerCount = customerCount;
    }


    public String getCountryName() {
        return countryName;
    }

    public int getCustomerCount() {
        return customerCount;
    }
}
