package enums;

/**
 * Created by Paul K Szean on 27/9/2016.
 */

public enum FlatType {
    Select("Select Flat Type"),
    OneRoomFlat("1 ROOM"),
    TwoRoomFlat("2 ROOM"),
    ThreeRoomFlat("3 ROOM"),
    FourRoomFlat("4 ROOM"),
    FiveRoomFlat("5 ROOM"),
    ExecutiveFlat("EXECUTIVE"),
    StudioApartment("STUDIO APARTMENT"),
    BungalowDetached("BUNGALOW/DETACHED HOUSE"),
    SemiDetached("SEMI/DETACHED HOUSE"),
    Terrace("TERRACE HOUSE"),
    Condominium("CONDOMINIUM"),
    ExecutiveCondominium("EXECUTIVE CONDOMINIUM"),
    ShopHouse("SHOP HOUSE");


    private String FlatTypeNames;

    FlatType(String FlatTypeNames) {
        this.FlatTypeNames = FlatTypeNames;
    }

    @Override
    public String toString() {
        return FlatTypeNames;
    }
}
