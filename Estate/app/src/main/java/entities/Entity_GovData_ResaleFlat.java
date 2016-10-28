package entities;

/**
 * Created by Paul K Szean on 18/10/2016.
 */

public class Entity_GovData_ResaleFlat {
    private String resaleflatpriceID;
    private String town;
    private String flat_type;
    private String block;
    private String month;
    private String street_name;
    private String storey_range;
    private String floor_area_sqm;
    private String flat_model;
    private String lease_commence_date;
    private String resale_price;

    public Entity_GovData_ResaleFlat(String resaleflatpriceID, String town, String flat_type, String block, String month, String street_name, String storey_range, String floor_area_sqm, String flat_model, String lease_commence_date, String resale_price) {
        this.resaleflatpriceID = resaleflatpriceID;
        this.town = town;
        this.flat_type = flat_type;
        this.block = block;
        this.month = month;
        this.street_name = street_name;
        this.storey_range = storey_range;
        this.floor_area_sqm = floor_area_sqm;
        this.flat_model = flat_model;
        this.lease_commence_date = lease_commence_date;
        this.resale_price = resale_price;
    }

    public String getResaleflatpriceID() {
        return resaleflatpriceID;
    }

    public void setResaleflatpriceID(String resaleflatpriceID) {
        this.resaleflatpriceID = resaleflatpriceID;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getFlat_type() {
        return flat_type;
    }

    public void setFlat_type(String flat_type) {
        this.flat_type = flat_type;
    }

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getStreet_name() {
        return street_name;
    }

    public void setStreet_name(String street_name) {
        this.street_name = street_name;
    }

    public String getStorey_range() {
        return storey_range;
    }

    public void setStorey_range(String storey_range) {
        this.storey_range = storey_range;
    }

    public String getFloor_area_sqm() {
        return floor_area_sqm;
    }

    public void setFloor_area_sqm(String floor_area_sqm) {
        this.floor_area_sqm = floor_area_sqm;
    }

    public String getFlat_model() {
        return flat_model;
    }

    public void setFlat_model(String flat_model) {
        this.flat_model = flat_model;
    }

    public String getLease_commence_date() {
        return lease_commence_date;
    }

    public void setLease_commence_date(String lease_commence_date) {
        this.lease_commence_date = lease_commence_date;
    }

    public String getResale_price() {
        return resale_price;
    }

    public void setResale_price(String resale_price) {
        this.resale_price = resale_price;
    }
}