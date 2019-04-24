package singareddy.productionapps.capturethemoment;

import com.google.firebase.auth.FirebaseUser;


// HELLO
public class AppUtilities {

    public static boolean UPDATE_PROFILE_DIALOG_SHOWN = false;

    public static class FailureCodes {
        public static final String EMPTY_EMAIL = "EMPTY_EMAIL";
        public static final String EMPTY_PASSWORD = "EMPTY_PASSWORD";
        public static final String PASSWORD_MISMATCH = "PASSWORD_MISMATCH";
        public static final String PROFILE_NAME_EMPTY = "PROFILE_NAME_EMPTY";
        public static final String PROFILE_AGE_INVALID = "PROFILE_AGE_INVALID";
        public static final String PROFILE_EMAIL_EMPTY = "PROFILE_EMAIL_EMPTY";
        public static final String PROFILE_MOBILE_EMPTY = "PROFILE_MOBILE_EMPTY";
        public static final String PROFILE_EMAIL_INVALID = "PROFILE_EMAIL_INVALID";
        public static final String PROFILE_MOBILE_INVALID = "PROFILE_MOBILE_INVALID";
        public static final String EMAIL_EXISTS = "The email address is already in use by another account.";
    }

    public static class Firebase {
        public static final String ALL_USERS_NODE = "users";
        public static final String ALL_BOOKS_NODE = "books";
        public static final String ALL_REGISTERED_USERS_NODE = "regUsers";
        public static final String ALL_SHARE_REQUESTS_NODE = "";
        public static final String EMAIL_PROVIDER = "password";
        public static final String PHONE_PROVIDER = "phone";
    }

    public static class Book {
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
        public static String CURRENT_USER_MOBILE;
        public static String CURRENT_USER_EMAIL;
    }

    public static class FileNames {
        public static final String UIDS_CACHE = "UIDS_CACHE";
    }
}
