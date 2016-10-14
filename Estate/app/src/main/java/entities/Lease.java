package entities;

/**
 * Created by Paul K Szean on 28/9/2016.
 */

public class Lease extends Property {


    private String wholeApartment;

    public Lease(String wholeApartment) {
        this.wholeApartment = wholeApartment;
    }

    public Lease(String propertyID, User owner, String flatType, String dealType, String title, String description, String furnishLevel, String price, String postalcode, String unit, String addressName, String photo, String status, String noOfbedrooms, String noOfbathrooms, String createdate, String wholeApartment) {
        super(propertyID, owner, flatType, dealType, title, description, furnishLevel, price, postalcode, unit, addressName, photo, status, noOfbedrooms, noOfbathrooms, createdate);
        this.wholeApartment = wholeApartment;
    }

    public String getWholeApartment() {
        return wholeApartment;
    }

    public void setWholeApartment(String wholeApartment) {
        this.wholeApartment = wholeApartment;
    }
}
