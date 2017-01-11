package in.co.retail.kohlibrothers.Model;


import in.co.retail.kohlibrothers.Utility.Constants;

/**
 * Created by Niha on 12/2/2015.
 */
public class Apis
{
    public final static String URL = Constants.DATA_URL;

    // url links
    public final static String OTP_URL = URL + Constants.OTP_NAME; // for sending sms
    public final static String AUTHORIZE_URL = URL + Constants.AUTHORIZE_NAME;
    public final static String BANNER_URL = URL + Constants.BANNER_NAME;
    public final static String NEW_PRODUCT_URL = URL + Constants.NEW_PRODUCT_NAME;
    public final static String CAT_TYPE_CATEGORY_LIST_URL = URL + Constants.CAT_TYPE_CATEGORY_LIST_NAME;
    public final static String SUB_CAT_LIST_URL = URL + Constants.SUB_CAT_LIST_NAME;
    public final static String PRODUCT_LIST_URL = URL + Constants.PRODUCT_LIST_NAME;
    public final static String DELIVERY_SPECS_URL = URL + Constants.DELIVERY_SPECS_NAME;
    public final static String ORDER_DETAILS_URL = URL + Constants.ORDER_DETAILS_NAME;
    public final static String AREA_URL = URL + Constants.AREA_NAME;
    public final static String USER_INFO_URL = URL + Constants.USER_INFO_NAME;
    public final static String SEARCH_LIST_URL = URL + Constants.SEARCH_LIST_NAME;
    public final static String SINGLE_PRODUCT_URL = URL + Constants.SINGLE_PRODUCT;
    public final static String ADD_REMOVE_PREF_LIST_URL = URL + Constants.ADD_REMOVE_PREF_LIST;
    public final static String GET_PREF_LIST_URL = URL + Constants.GET_PREF_LIST;
    public final static String SEND_MAIL_URL = URL + Constants.SEND_MAIL;
    public final static String TRACK_ORDER_URL = URL + Constants.TRACK_ORDER;
    public final static String GET_PROMOTION_URL = URL + Constants.GET_PROMOTION;
    public final static String GET_PMT_TYPE_URL = URL + Constants.GET_PMT_TYPE;

    // url codes
    public final static int OTP_CODE = 0; // for sending sms with OTP
    public final static int AUTHORIZE_CODE = 1;
    public final static int NEW_PRODUCT_CODE = 2;
    public final static int BANNER_CODE = 3;
    public final static int CAT_TYPE_CATEGORY_LIST_CODE = 4;
    public final static int SUB_CAT_LIST_CODE = 5;
    public final static int PRODUCT_LIST_CODE = 6;
    public final static int DELIVERY_SPECS_CODE = 7;
    public final static int ORDER_DETAILS_CODE = 8;
    public final static int AREA_CODE = 9;
    public final static int USER_INFO_CODE = 10;
    public final static int SEARCH_LIST_CODE = 11;
    public final static int SINGLE_PRODUCT_CODE = 12;
    public final static int ADD_REMOVE_PREF_LIST_CODE = 13;
    public final static int GET_PREF_LIST_CODE = 14;
    public final static int SEND_MAIL_CODE = 15;
    public final static int TRACK_ORDER_CODE = 16;
    public final static int GET_PROMOTION_CODE = 17;
    public final static int GET_PMT_TYPE_CODE = 18;
}
