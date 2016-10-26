package handler;

import android.app.Activity;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

import java.text.DecimalFormat;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Paul K Szean on 22/9/2016.
 */

public class Utility {
    private static final String TAG = Utility.class.getSimpleName();

    public Utility() {

    }

    private static Pattern pattern;
    private static Matcher matcher;
    //Email Pattern
    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";


    public static boolean isEmailValid(String email) {
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        Log.i(TAG, "isEmailValid: " + matcher.matches());
        return matcher.matches();
    }

    public static String generateTitle() {
        Random random = new Random();
        return "Some title number: " + random.nextInt(10000) + " !";
    }

    public static String generateDesc() {
        Random random = new Random();
        return "Some description number: " + random.nextInt(10000) + " !";
    }

    public static String generateString(int length) {
        char[] chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        return sb.toString();
    }

    public static String generateNumberAsString(int length) {
        char[] chars = "0123456789".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        return sb.toString();
    }

    public static String generateNumberAsString(int min, int max) {
        Random random = new Random();
        int number = random.nextInt(max - min + 1) + 1;
        if (number >= 10)
            return String.valueOf(number);
        else
            return new DecimalFormat("00").format(number);

    }

    public static int generateNumber(int min, int max) {
        Random random = new Random();
        int number = random.nextInt(max - min + 1) + min;
        return number;
    }

    public static String formatStringNumber(String number) {
        double amount = Double.parseDouble(number);
        DecimalFormat formatter = new DecimalFormat("#,###.00");
        return formatter.format(amount);
    }

    public static String generateUnit() {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();

        int floor = random.nextInt(18 - 1 + 1) + 1;
        int unit = random.nextInt(9999 - 99 + 1) + 1;
        sb.append(floor + "-" + unit);

        return sb.toString();
    }

    public static boolean generateBool() {
        Random random = new Random();
        return random.nextBoolean();
    }

    public String generateLocation() {
        String[] location = {"Raffles Place", "Cecil", "Marina",
                "Anson", "Tanjong Pagar",
                "Queenstown", "Tiong Bahru",
                "Telok Blangah", "Harbourfront",
                "Pasir Panjang", "Hong Leong Garden", "Clementi",
                "High Street", "Beach Road ",
                "Middle Road", "Golden Mile ",
                "Little India ",
                "Orchard", "Cairnhill", "River Valley ",
                "Ardmore", "Bukit Timah", "Holland Road", "Tanglin",
                "Watten Estate", "Novena", "Thomson",
                "Balestier", "Toa Payoh", "Serangoon",
                "Macpherson", "Braddell",
                "Geylang", "Eunos",
                "Katong", "Joo Chiat", "Amber Road",
                "Bedok", "Upper East Coast", "Eastwood", "Kew Drive",
                "Loyang", "Changi",
                "Simei", "Tampines", "Pasir Ris",
                "Serangoon Garden", "Hougang", "Punggol",
                "Bishan", "Ang Mo Kio",
                "Upper Bukit Timah", "Clementi Park", "Ulu Pandan",
                "Jurong",
                "Hillview", "Dairy Farm", "Bukit Panjang", "Choa Chu Kang",
                "Lim Chu Kang", "Tengah",
                "Kranji", "Woodgrove", "Woodlands",
                "Upper Thomson", "Springleaf",
                "Yishun", "Sembawang",
                "Seletar"
        };

        Random random = new Random();
        String locationValue = location[random.nextInt(location.length) + 1];
        Log.i(TAG, locationValue);
        return locationValue;

    }

    public static void showSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(activity.getCurrentFocus(), InputMethodManager.SHOW_IMPLICIT);
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (activity.getCurrentFocus() != null)
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

}
