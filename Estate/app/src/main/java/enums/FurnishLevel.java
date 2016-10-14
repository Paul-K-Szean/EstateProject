package enums;

/**
 * Created by Paul K Szean on 27/9/2016.
 */

public enum FurnishLevel {
    Select("Select Furnish Level"),
    FullyFurnished("Fully Furnished"),
    PartlyFurnished("Partly Furnished");


    private String FurnishLevels;

    FurnishLevel(String FurnishLevels) {
        this.FurnishLevels = FurnishLevels;
    }

    @Override
    public String toString() {
        return FurnishLevels;
    }
}
