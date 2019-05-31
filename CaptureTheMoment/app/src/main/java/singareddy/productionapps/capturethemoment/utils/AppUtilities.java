package singareddy.productionapps.capturethemoment.utils;

import com.google.firebase.auth.FirebaseUser;


// HELLO
public class AppUtilities {

    public static boolean UPDATE_PROFILE_DIALOG_SHOWN = false;
    public static final int IMAGE_CROP_RATIO_X = 1;
    public static final int IMAGE_CROP_RATIO_Y = 1;
    public static final int PHOTO_HAS_FRAME = 1;
    public static final int PHOTO_HAS_NO_FRAME = 0;

    public static class FailureCodes {
        public static final String EMPTY_EMAIL = "EMPTY_EMAIL";
        public static final String EMPTY_PASSWORD = "EMPTY_PASSWORD";
        public static final String PASSWORD_MISMATCH = "PASSWORD_MISMATCH";
        public static final String EMPTY_MOBILE = "EMPTY_MOBILE";
        public static final String NO_OTP = "NO_OTP";
        public static final String PROFILE_NAME_EMPTY = "PROFILE_NAME_EMPTY";
        public static final String PROFILE_AGE_INVALID = "PROFILE_AGE_INVALID";
        public static final String PROFILE_EMAIL_EMPTY = "PROFILE_EMAIL_EMPTY";
        public static final String PROFILE_MOBILE_EMPTY = "PROFILE_MOBILE_EMPTY";
        public static final String PROFILE_EMAIL_INVALID = "PROFILE_EMAIL_INVALID";
        public static final String PROFILE_MOBILE_INVALID = "PROFILE_MOBILE_INVALID";
        public static final String EMAIL_EXISTS = "The email address is already in use by another account.";
    }

    public static class Firebase {
        public static final String KEY_USER_NAME = "name";
        public static final String KEY_USER_OWNER = "owner";
        public static final String KEY_USER_PROFILE = "profile";
        public static final String KEY_USER_OWNED_BOOKS = "ownedBooks";
        public static final String KEY_USER_SHARED_BOOKS = "sharedBooks";
        public static final String KEY_BOOK_NAME = "name";
        public static final String KEY_BOOK_LAST_UPDATED = "lastUpdatedDate";
        public static final String KEY_BOOK_SEC_OWNERS = "secOwners";
        public static final String KEY_BOOK_CARDS = "cards";

        public static final String ALL_USERS_NODE = "users";
        public static final String ALL_BOOKS_NODE = "books";
        public static final String ALL_CARDS_NODE = "cards";
        public static final String ALL_REGISTERED_USERS_NODE = "regUsers";
        public static final String ALL_SHARE_REQUESTS_NODE = "";

        public static final String EMAIL_PROVIDER = "password";
        public static final String PHONE_PROVIDER = "phone";
    }

    public static class Book {
        public static final int SEC_OWNER_NOT_VALIDATED = 0;
        public static final int SEC_OWNER_INVALID = -1;
        public static final int SEC_OWNER_VALID = 1;
        public static final int SEC_OWNER_DUPLICATE = 2;

        public static final String BOOK_NAME_EMPTY = "BOOK_NAME_EMPTY";
        public static final String BOOK_NAME_INVALID = "BOOK_NAME_INVALID";
        public static final String BOOK_NAME_VALID = "BOOK_NAME_VALID";
        public static final String BOOK_EXISTS = "BOOK_EXISTS";
        public static final String BOOK_DB_ERROR = "BOOK_DB_ERROR";
        public static final String BOOK_NAME_SAME_AS_OLD_NAME = "BOOK_NAME_SAME_AS_OLD_NAME";
    }

    public static class User {
        public static String LOGIN_PROVIDER;
        public static FirebaseUser CURRENT_USER;
        public static String CURRENT_USER_ID;
        public static String CURRENT_USER_MOBILE;
        public static String CURRENT_USER_EMAIL;
    }

    public static class FileNames {
        public static final String UIDS_CACHE = "UIDS_CACHE";
        public static final String USER_PROFILE_CACHE = "USER_PROFILE_CACHE";
        public static final String USER_PROFILE_PICTURE = "profile_pic.jpg";
    }

    public static class SharedPrefKeys {
        public static final String PROFILE_PIC_AVAILABLE = "profilePicAvailable";
    }

    public static class FBUser {
        public static final String NAME = "name";
        public static final String AGE = "age";
        public static final String ABOUT = "about";
        public static final String GENDER = "gender";
        public static final String EMAIL = "email";
        public static final String MOBILE = "mobile";
        public static final String LOCATION = "location";
        public static final String PROFILE = "profile";
    }

    public static class Defaults {
        public static final String DEFAULT_STRING = "";
        public static final Integer DEFAULT_INT = 0;
        public static final Long DEFAULT_LONG = 0l;
    }

    public static class ScreenTitles {
        public static final String SCREEN_TITLE_EDIT_PROFILE = "Edit Profile";
        public static final String SCREEN_TITLE_ADD_BOOK = "New Book";
        public static final String SCREEN_TITLE_ADD_CARD = "Add Memory";
        public static final String SCREEN_TITLE_EDIT_CARD = "Edit Memory";
    }
}
