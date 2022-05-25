package scheduler;

public class Country {
    private int countryID;
    private String countryName;
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
    @Override
    public String toString(){
        return getCountryName();
    }
}
