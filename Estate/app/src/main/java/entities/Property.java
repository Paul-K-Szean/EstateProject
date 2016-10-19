package entities;

/**
 * Created by Paul K Szean on 26/9/2016.
 */

public class Property {

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

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public String getStreetname() {
        return streetname;
    }

    public void setStreetname(String streetname) {
        this.streetname = streetname;
    }

    public String getFloorlevel() {
        return floorlevel;
    }

    public void setFloorlevel(String floorlevel) {
        this.floorlevel = floorlevel;
    }

    public String getFloorarea() {
        return floorarea;
    }

    public void setFloorarea(String floorarea) {
        this.floorarea = floorarea;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getBedroomcount() {
        return bedroomcount;
    }

    public void setBedroomcount(String bedroomcount) {
        this.bedroomcount = bedroomcount;
    }

    public String getBathroomcount() {
        return bathroomcount;
    }

    public void setBathroomcount(String bathroomcount) {
        this.bathroomcount = bathroomcount;
    }

    public String getWholeapartment() {
        return wholeapartment;
    }

    public void setWholeapartment(String wholeapartment) {
        this.wholeapartment = wholeapartment;
    }

    public String getCreatedate() {
        return createdate;
    }

    public void setCreatedate(String createdate) {
        this.createdate = createdate;
    }

    public Property(User owner, String flatType, String block, String streetname, String floorlevel, String floorarea, String price,
                    String image, String status, String dealType, String title, String description, String furnishLevel,
                    String bedroomcount, String bathroomcount, String wholeapartment) {
        this.owner = owner;
        this.flatType = flatType;
        this.block = block;
        this.streetname = streetname;
        this.floorlevel = floorlevel;
        this.floorarea = floorarea;
        this.price = price;
        this.image = image;
        this.status = status;
        this.dealType = dealType;
        this.title = title;
        this.description = description;
        this.furnishLevel = furnishLevel;
        this.bedroomcount = bedroomcount;
        this.bathroomcount = bathroomcount;
        this.wholeapartment = wholeapartment;

    }

    public Property(String propertyID, User owner, String flatType, String block, String streetname, String floorlevel,
                    String floorarea, String price, String image, String status, String dealType, String title, String description,
                    String furnishLevel, String bedroomcount, String bathroomcount, String wholeapartment, String createdate) {

        this.propertyID = propertyID;
        this.owner = owner;
        this.flatType = flatType;
        this.block = block;
        this.streetname = streetname;
        this.floorlevel = floorlevel;
        this.floorarea = floorarea;
        this.price = price;
        this.image = image;
        this.status = status;
        this.dealType = dealType;
        this.title = title;
        this.description = description;
        this.furnishLevel = furnishLevel;
        this.bedroomcount = bedroomcount;
        this.bathroomcount = bathroomcount;
        this.wholeapartment = wholeapartment;
        this.createdate = createdate;
    }

    private String propertyID;
    private User owner;
    private String flatType;
    private String block;
    private String streetname;
    private String floorlevel;
    private String floorarea;
    private String price;
    private String image;
    private String status;
    private String dealType;
    private String title;
    private String description;
    private String furnishLevel;
    private String bedroomcount;
    private String bathroomcount;
    private String wholeapartment;
    private String createdate;

}
