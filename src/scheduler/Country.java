package scheduler;

public class Country {
    private static int countryID;
    private static String countryName;
    public  Country(String countryName, int countryID){
        this.countryName = countryName;
        this.countryID = countryID;
    }

    public static String getCountryName() {
        return countryName;
    }

    public static int getCountryID() {
        return countryID;
    }
}
