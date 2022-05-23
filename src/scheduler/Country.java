package scheduler;

public class Country {
    private String countryName;
    private int countryID;
    public  Country(String countryName, int countryID){
        this.countryName = countryName;
        this.countryID = countryID;
    }

    public String getCountryName() {
        return countryName;
    }

    public int getCountryID() {
        return countryID;
    }
}
