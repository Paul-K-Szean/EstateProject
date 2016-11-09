package controllers;

/**
 * Created by Paul K Szean on 24/9/2016.
 */

public class EstateConfig {


    public static String URL = "192.168.1.122";
    // public static String URL = "10.27.251.110";


    // 10.0.2.2 = localhost (default)
    // look for estatephpfiles folder in our dropbox
    // test the link by localhost/estatelogin
    // ACCOUNT
    public static String URL_LOGIN = "http://" + URL + "/estatelogin.php";
    public static String URL_REGISTER = "http://" + URL + "/estateregister.php";
    // PROPERTY
    public static String URL_GETPROPERTYDETAILS = "http://" + URL + "/estategetpropertydetails.php";
    public static String URL_GETUSERLISTINGS = "http://" + URL + "/estategetuserlistings.php";
    public static String URL_UPDATEUSERPROPERTY = "http://" + URL + "/estateupdateuserproperty.php";
    public static String URL_SEARCHLISTINGS = "http://" + URL + "/estatesearchlistings.php";
    public static String URL_LEASELISTINGS = "http://" + URL + "/estatesearchleaselistings.php";
    public static String URL_SALELISTINGS = "http://" + URL + "/estatesearchsalelistings.php";
    public static String URL_NEWPROPERTY = "http://" + URL + "/estatenewproperty.php";
    public static String URL_UPDATEFAVOURITEYCOUNT = "http://" + URL + "/estateupdatefavouritecount.php";
    public static String URL_UPDATEVIEWCOUNT = "http://" + URL + "/estateupdateviewcount.php";
    // FAVOURITE
    public static String URL_NEWFAVOURITEPROPERTY = "http://" + URL + "/estatenewfavouriteproperty.php";
    public static String URL_DELETEFAVOURITEPROPERTY = "http://" + URL + "/estatedeletefavouriteproperty.php";
    public static String URL_GETUSERFAVOURITELISTINGS = "http://" + URL + "/estategetuserfavouritedlistings.php";
    // COMMENT
    public static String URL_NEWCOMMENT = "http://" + URL + "/estatenewcomment.php"; // new comment
    public static String URL_GETCOMMENT = "http://" + URL + "/estategetcomment.php"; // get comments
    // NOTIFICATION
    public static String URL_NEWNOTIFICATION = "http://" + URL + "/estatenewnotification.php"; // register current device
    public static String URL_PUSHNOTIFICATION = "http://" + URL + "/estatepushnotification.php"; // push to device

    // GOV DATA
    public static String URL_GOVDATA_RESALEFLATPRICES = "https://data.gov.sg/api/action/datastore_search?resource_id=83b2fc37-ce8c-4df4-968b-370fd818138b";


}
