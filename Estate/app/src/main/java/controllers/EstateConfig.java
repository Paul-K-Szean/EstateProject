package controllers;

/**
 * Created by Paul K Szean on 24/9/2016.
 */

public class EstateConfig {


    public static String URL = "192.168.1.122";
    // URLS
    // 10.0.2.2 = localhost (default)
    // look for estatephpfiles folder in our dropbox
    // test the link by localhost/estatelogin
    // ACCOUNT
    public static String URL_LOGIN = "http://" + URL + "/estatelogin.php";
    public static String URL_REGISTER = "http://" + URL + "/estateregister.php";
    // PROPERTY
    public static String URL_ALLLISTINGS = "http://" + URL + "/estatealllistings.php";
    public static String URL_PROPERTYDETAILS = "http://" + URL + "/estatepropertydetails.php";
    public static String URL_GETUSERLISTINGS = "http://" + URL + "/estateuserlistings.php";
    public static String URL_UPDATEUSERPROPERTY = "http://" + URL + "/estateupdateuserproperty.php";
    public static String URL_SEARCHLISTINGS = "http://" + URL + "/estatesearchlistings.php";
    public static String URL_LEASELISTINGS = "http://" + URL + "/estatesearchleaselistings.php";
    public static String URL_SALELISTINGS = "http://" + URL + "/estatesearchsalelistings.php";
    public static String URL_NEWPROPERTY = "http://" + URL + "/estatenewproperty.php";
    // FAVOURITE
    public static String URL_NEWFAVOURITEPROPERTY = "http://" + URL + "/estatenewfavouriteproperty.php";
    public static String URL_DELETEFAVOURITEPROPERTY = "http://" + URL + "/estatedeletefavouriteproperty.php";
    public static String URL_USERFAVOURITELISTINGS = "http://" + URL + "/estateuserfavouritedlistings.php";
    public static String URL_UPDATEPROPERTYCOUNT = "http://" + URL + "/estateupdatepropertycount.php";
    // INBOX
    public static String URL_NEWINBOX = "http://" + URL + "/estatenewinbox.php";
    public static String URL_GETINBOX = "http://" + URL + "/estategetinbox.php";

    // GOV DATA
    public static String URL_GOVDATA_RESALEFLATPRICES = "https://data.gov.sg/api/action/datastore_search?resource_id=83b2fc37-ce8c-4df4-968b-370fd818138b";


}
