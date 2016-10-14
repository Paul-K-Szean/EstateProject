package enums;

/**
 * Created by Paul K Szean on 27/9/2016.
 */

public enum FlatType {
    Select("Select Residential Type"),
    OneRoomFlat("1-Room Flat"),
    TwoRoomFlat("2-Room Flat"),
    ThreeRoomFlat("3-Room Flat"),
    FourRoomFlat("4-Room Flat"),
    FiveRoomFlat("5-Room Flat"),
    ExecutiveFlat("Executive Flat"),
    StudioApartment("Studio Apartment"),
    BungalowDetached("Bungalow/Detached House"),
    SemiDetached("Semi/Detached House"),
    Terrace("Terrace House"),
    Condominium("Condominium"),
    ExecutiveCondominium("Executive Condominium"),
    ShopHouse("Shop House");


    private String FlatTypeNames;

    FlatType(String FlatTypeNames) {
        this.FlatTypeNames = FlatTypeNames;
    }

    @Override
    public String toString() {
        return FlatTypeNames;
    }
}
