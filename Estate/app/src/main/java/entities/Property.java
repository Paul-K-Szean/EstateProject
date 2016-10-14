package entities;

/**
 * Created by Paul K Szean on 26/9/2016.
 */

public class Property {
    private String propertyID;
    private User owner;
    private String flatType;
    private String dealType;
    private String title;
    private String description;
    private String furnishLevel;
    private String price;
    private String postalcode;
    private String unit;
    private String addressName;
    private String photo;
    private String status;
    private String noofbedrooms;
    private String noofbathrooms;
    private String createdate;

    public Property() {

    }

    public Property(String propertyID, User owner, String flatType, String dealType, String title, String description, String furnishLevel, String price, String postalcode, String unit, String addressName, String photo, String status, String noOfbedrooms, String noOfbathrooms, String createdate) {
        this.propertyID = propertyID;
        this.owner = owner;
        this.flatType = flatType;
        this.dealType = dealType;
        this.title = title;
        this.description = description;
        this.furnishLevel = furnishLevel;
        this.price = price;
        this.postalcode = postalcode;
        this.unit = unit;
        this.addressName = addressName;
        this.photo = photo;
        this.status = status;
        this.noofbedrooms = noOfbedrooms;
        this.noofbathrooms = noOfbathrooms;
        this.createdate = createdate;
    }

    public String getPropertyID() {
        return propertyID;
    }

    public void setPropertyID(String propertyID) {
        this.propertyID = propertyID;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public String getFlatType() {
        return flatType;
    }

    public void setFlatType(String flatType) {
        this.flatType = flatType;
    }

    public String getDealType() {
        return dealType;
    }

    public void setDealType(String dealType) {
        this.dealType = dealType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFurnishLevel() {
        return furnishLevel;
    }

    public void setFurnishLevel(String furnishLevel) {
        this.furnishLevel = furnishLevel;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPostalcode() {
        return postalcode;
    }

    public void setPostalcode(String postalcode) {
        this.postalcode = postalcode;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getAddressName() {
        return addressName;
    }

    public void setAddressName(String addressName) {
        this.addressName = addressName;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNoOfbedrooms() {
        return noofbedrooms;
    }

    public void setNoOfbedrooms(String noOfbedrooms) {
        this.noofbedrooms = noOfbedrooms;
    }

    public String getNoOfbathrooms() {
        return noofbathrooms;
    }

    public void setNoOfbathrooms(String noOfbathrooms) {
        this.noofbathrooms = noOfbathrooms;
    }

    public String getCreatedate() {
        return createdate;
    }

    public void setCreatedate(String createdate) {
        this.createdate = createdate;
    }
}
