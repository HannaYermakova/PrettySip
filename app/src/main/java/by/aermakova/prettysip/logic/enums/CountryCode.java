package by.aermakova.prettysip.logic.enums;

/**
 * Utilitarian enumerator for country code picker in {@link by.aermakova.prettysip.ui.activity.auth.EnterPhoneNumberFragment}
 */
public enum CountryCode {

    BY("BY", "+375"),
    RU("RU", "+7"),
    UA("UA","+380");

    private String countryName;
    private String countryPrefix;

    CountryCode(String countryName, String countryPrefix) {
        this.countryName = countryName;
        this.countryPrefix = countryPrefix;
    }

    public String getCountryView(){
        return countryName + " " + countryPrefix;
    }

    public String getCountryPrefix() {
        return countryPrefix;
    }
}
