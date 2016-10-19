package controllers;

import handler.Utility;

/**
 * Created by Paul K Szean on 24/9/2016.
 */

public class EstateConfig {

    // URLS
    // 10.0.2.2 = localhost (default)
    // look for estatephpfiles folder in our dropbox
    // test the link by localhost/estatelogin
    public static String URL_LOGIN = "http://10.0.2.2/estatelogin.php";
    public static String URL_REGISTER = "http://10.0.2.2/estateregister.php";
    public static String URL_ALLLISTINGS = "http://10.0.2.2/estatealllistings.php";
    public static String URL_PROPERTYDETAILS = "http://10.0.2.2/estatepropertydetails.php";
    public static String URL_GETUSERLISTINGS = "http://10.0.2.2/estateuserlistings.php";
    public static String URL_UPDATEUSERPROPERTY = "http://10.0.2.2/estateupdateuserproperty.php";
    public static String URL_SEARCHLISTINGS = "http://10.0.2.2/estatesearchlistings.php";
    public static String URL_NEWPROPERTY = "http://10.0.2.2/estatenewproperty.php";

    // GOV DATA
    public static String URL_GOVDATA_RESALEFLATPRICES = "https://data.gov.sg/api/action/datastore_search?resource_id=83b2fc37-ce8c-4df4-968b-370fd818138b";


}
