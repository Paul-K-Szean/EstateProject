package handler;

import android.util.Log;

import java.text.DecimalFormat;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Paul K Szean on 22/9/2016.
 */

public class Utility {

    public Utility() {

    }

    private static Pattern pattern;
    private static Matcher matcher;
    //Email Pattern
    private static final String EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";


    public static boolean isEmailValid(String email) {
        Log.i("isEmailValid", "Value is " + email);
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        Log.i("isEmailValid", "Result is " + matcher.matches());
        return matcher.matches();
    }

    public static String generateTitle() {
        String titles[] = {"House for sale", " Unit for sale"};
        Random random = new Random();
        return titles[random.nextInt(titles.length)];
    }

    public static String generateDesc() {
        Random random = new Random();
        return "Description number: " + random.nextInt(100) + " !";
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

        return String.valueOf(number);
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
}
