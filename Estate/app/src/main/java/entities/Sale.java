package entities;

/**
 * Created by Paul K Szean on 28/9/2016.
 */

public class Sale extends Property {


    private String floorArea;

    public Sale(String floorArea) {
        this.floorArea = floorArea;
    }

    public Sale(String propertyID, User owner, String flatType, String dealType, String title, String description, String furnishLevel, String price, String postalcode, String unit, String addressName, String photo, String status, String noOfbedrooms, String noOfbathrooms, String createdate, String floorArea) {
        super(propertyID, owner, flatType, dealType, title, description, furnishLevel, price, postalcode, unit, addressName, photo, status, noOfbedrooms, noOfbathrooms, createdate);
        this.floorArea = floorArea;
    }

    public String getFloorArea() {
        return floorArea;
    }

    public void setFloorArea(String floorArea) {
        this.floorArea = floorArea;
    }
}
