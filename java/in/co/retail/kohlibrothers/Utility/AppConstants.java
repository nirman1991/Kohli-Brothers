package in.co.retail.kohlibrothers.Utility;

/**
 * Created by Niha on 1/8/2016.
 */
public class AppConstants
{
    public enum ApplicationLayoutModeAction {
        NONE,
        SINGLE_PRODUCT,
        PRODUCT,
        CART, //PREF_LIST_ITEM, PREF_LIST_SEARCH,ORDER_OVERVIEW
        SEARCH,
        NEW_PRODUCT,
        SUB_CATEGORY,
        MONTHLY_BASKET,
        WISHLIST,
        REVIEW_ORDER,
        ORDER_DETAILS,
        SCHOOL,
        BRAND,
        CATEGORY,
        TRACK_ORDER,
        PROMOTION
    };

    public enum UserInfoSaveMode {
        NONE,
        PRIMARY,
        SECONDARY,
        CHECKOUT,
        PRI_SAVE_CHECKOUT,
        SEC_SAVE_CHECKOUT
    };

    public enum DeliveryAddressType {
        NONE,
        PRIMARY,
        SECONDARY
    };

    public class UserInfoBtn {
        public static final String SAVE = "Save";
        public static final String PROCEED = "Proceed";
        public static final String SP = "Save & Proceed";
    }
}
