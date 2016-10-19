package enums;

/**
 * Created by Paul K Szean on 27/9/2016.
 */

public enum FloorLevel {
    Select("Select Floor Level"),
    Level01To03("01 TO 03"),
    Level04To06("04 TO 06"),
    Level07To09("07 TO 09"),
    Level10To12("10 TO 12"),
    Level13To15("13 TO 15"),
    Level16To18("16 TO 18"),
    Level19T021("19 TO 21"),
    Level22T024("22 TO 24"),
    Level25T027("25 TO 27"),
    Level28T030("28 TO 30"),
    Level01T005("01 TO 05"),
    Level06T010("06 TO 10"),
    Level11T015("11 TO 15"),
    Level16T020("16 TO 20"),
    Level21T025("21 TO 25"),
    Level26T030("26 TO 30"),
    Level31T035("31 TO 35"),
    Level36T040("36 TO 40");


    private String FloorLevel;

    FloorLevel(String FloorLevel) {
        this.FloorLevel = FloorLevel;
    }

    @Override
    public String toString() {
        return FloorLevel;
    }
}
