package singareddy.productionapps.capturethemoment.user;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.telephony.PhoneNumberUtils;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

import static singareddy.productionapps.capturethemoment.AppUtilities.*;
import static singareddy.productionapps.capturethemoment.AppUtilities.Firebase.*;

import singareddy.productionapps.capturethemoment.AppUtilities;
import singareddy.productionapps.capturethemoment.auth.AuthListener;
import singareddy.productionapps.capturethemoment.models.User;

import static singareddy.productionapps.capturethemoment.AppUtilities.User.*;


public class AuthenticationViewModel extends AndroidViewModel {
    static String TAG = "AuthenticationViewModel";
    private AuthListener.EmailLogin emailLoginListener;
    private AuthListener.EmailSignup emailSignupListener;
    private AuthListener.Mobile mobileLoginListener;
    private AuthListener.Logout logoutListener;
    private ProfileListener.InitialProfile initialProfileListener;
    private ProfileListener profileListener;
    private FirebaseAuth.AuthStateListener authStateListener;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private User user;

    public AuthenticationViewModel(Application application) {
        super(application);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                Log.i(TAG, "onAuthStateChanged: State: "+firebaseAuth.getCurrentUser());
                if (firebaseAuth.getCurrentUser() == null) {
                    // The user has been logged out
                    // So, clean up all the user related data and take them to the login activity
                    AuthenticationViewModel.this.eraseUserDataFromCache();
                    logoutListener.onUserLoggedOut();
                }
                else {
                    // Initialise important global variables
                    CURRENT_USER = firebaseAuth.getCurrentUser();
                    LOGIN_PROVIDER = CURRENT_USER.getProviders().get(0);
                    if (LOGIN_PROVIDER.equals(EMAIL_PROVIDER)) {
                        CURRENT_USER_EMAIL = firebaseAuth.getCurrentUser().getEmail();
                    }
                    else if (LOGIN_PROVIDER.equals(PHONE_PROVIDER)) {
                        CURRENT_USER_MOBILE = convertE164toNormalMobile(CURRENT_USER.getPhoneNumber());
                    }
                }
            }
        };
    }

    /**
     * This method takes user object and registers the user in Firebase Auth
     * @param user : the details of the newly registering user
     * @param confirmPassword
     */
    public void registerUserWithEmailCredentials(final User user, String password, String confirmPassword) {
        Log.i(TAG, "registerUserWithEmailCredentials: Email - "+user.getEmailId()+" Password - "+confirmPassword);
        // Check if the passwords match
        if (user.getEmailId() == null || user.getEmailId().equals("")) {
            // Empty email id
            emailSignupListener.onEmailUserRegisterFailure(user.getEmailId(), AppUtilities.FailureCodes.EMPTY_EMAIL);
            return;
        }
        else if (password == null || password.equals("") || confirmPassword == null || confirmPassword.equals("")) {
            // Empty passwords
            emailSignupListener.onEmailUserRegisterFailure(user.getEmailId(), AppUtilities.FailureCodes.EMPTY_PASSWORD);
            return;
        }
        else if (!password.equals(confirmPassword)) {
            // Passwords do not match
            emailSignupListener.onEmailUserRegisterFailure(user.getEmailId(), AppUtilities.FailureCodes.PASSWORD_MISMATCH);
            return;
        }
        this.user = user;
        OnSuccessListener<AuthResult> registerSuccessListener = new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Log.i(TAG, "onSuccess: *");
                emailSignupListener.onEmailUserRegisterSuccess(user.getEmailId());
                // TODO: GetBookListener the user data
                getCurrentUserProfile();
//                updateUserProfile(user);
            }
        };

        OnFailureListener registerFailureListener = new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i(TAG, "onFailure: "+e.getMessage());
                emailSignupListener.onEmailUserRegisterFailure(user.getEmailId(), e.getMessage());
            }
        };
        firebaseAuth.createUserWithEmailAndPassword(user.getEmailId(), password)
        .addOnSuccessListener(registerSuccessListener)
        .addOnFailureListener(registerFailureListener);
    }

    /**
     * This method takes credentials as string values
     * and uses them to login the user.
     * As of now, this method is designed only for Email authentication
     * @param email
     * @param password
     */
    public void loginUserWithEmailCredentials(String email, String password) {
        Log.i(TAG, "loginUserWithEmailCredentials: Email - "+email+" Password - "+password);
        // Check if the credentials are given and are not empty
        if (email == null || email.equals("") || password == null || password.equals("")) {
            emailLoginListener.onEmailUserLoginFailure("EMPTY_FIELDS");
            return;
        }
        OnSuccessListener<AuthResult> loginSuccessListener = new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Log.i(TAG, "onSuccess: *");
                emailLoginListener.onEmailUserLoginSuccess();
                // TODO: After the login is successful, save the user data into cache
                getCurrentUserProfile();
            }
        };

        OnFailureListener loginFailureListener = new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i(TAG, "onFailure: "+e.getMessage());
                emailLoginListener.onEmailUserLoginFailure("");
            }
        };
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(loginSuccessListener)
                .addOnFailureListener(loginFailureListener);
    }

    /**
     * This method takes a mobile number and
     * authenticates it with the Firebase.
     * The logic remains same for both login and signup.
     * @param mobile
     */
    public void authorizePhoneCredentials (String mobile) {
        Log.i(TAG, "authorizePhoneCredentials: Mobile - "+mobile);
        PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                Log.i(TAG, "onVerificationCompleted: *");
                authorizePhoneCredentials(phoneAuthCredential);
            }

            @Override
            public void onCodeAutoRetrievalTimeOut(String s) {
                super.onCodeAutoRetrievalTimeOut(s);
                Log.i(TAG, "onCodeAutoRetrievalTimeOut: *");
                // TODO: Take the OTP from the user manually
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                Log.i(TAG, "onCodeSent: *");
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Log.i(TAG, "onVerificationFailed: " + e.getLocalizedMessage());
            }
        };

        mobile = PhoneNumberUtils.formatNumberToE164(mobile, "IN");
        // Check if the mobile number is valid and not empty
        if (mobile == null || mobile.equals("")) {
            mobileLoginListener.onMobileAuthenticationFailure("EMPTY_FIELDS");
            return;
        }
        PhoneAuthProvider.getInstance().verifyPhoneNumber(mobile, 10, TimeUnit.SECONDS, TaskExecutors.MAIN_THREAD, callbacks);
    }

    /**
     * The method authenticates a user using a mobile number
     * @param phoneAuthCredential
     */
    private void authorizePhoneCredentials (PhoneAuthCredential phoneAuthCredential) {
        Log.i(TAG, "authorizePhoneCredentials: *");
        OnSuccessListener<AuthResult> successListener = new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Log.i(TAG, "onSuccess: *");
                // Once authenticated, check and add the user to the database
                String m = authResult.getUser().getPhoneNumber().substring(3);
                user = new User();
                user.setMobile(Long.parseLong(m));
                getCurrentUserProfile();
                mobileLoginListener.onMobileAuthenticationSuccess();
            }
        };

        OnFailureListener failureListener = new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i(TAG, "onFailure: "+e.getMessage());
                mobileLoginListener.onMobileAuthenticationFailure(""); // TODO: Mobile number cannot be passed??
            }
        };
        firebaseAuth.signInWithCredential(phoneAuthCredential)
        .addOnSuccessListener(successListener)
        .addOnFailureListener(failureListener);
    }

    /**
     * This method checks if the user has logged in the
     * first time or not, and changes the logic accordingly
     */
    public void checkIfUserLoggedInFirstTime() {
        final DatabaseReference newUserNode = firebaseDatabase.getReference().child("users").child(firebaseAuth.getUid()).child("profile");

        final ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // GetBookListener the data at this node and see if it is null
                // If the user was already created, the snapshot will have non-null
                // Else, it will have null value
                Log.i(TAG, "onDataChange: NAME: "+dataSnapshot.getValue());
                Log.i(TAG, "onDataChange: Name Exists  : "+dataSnapshot.hasChild("name"));
                Log.i(TAG, "onDataChange: Mobile Exists: "+dataSnapshot.hasChild("mobile"));
                if (dataSnapshot.hasChild("name") == false || dataSnapshot.getValue(User.class).getName().equals("NA")) {
                    initialProfileListener.onUserProfilePending();
                }
                else {
                    // Since the user is not logged in first time, save the user to the cache
                    AuthenticationViewModel.this.user = dataSnapshot.getValue(User.class);
                    saveUserInCache(user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i(TAG, "onCancelled: *");
            }
        };

        newUserNode.addListenerForSingleValueEvent(eventListener);
    }

    /**
     * This method is used when a complete profile needs to be
     * put in the database for the current currentUser.
     * @param currentUser
     */
    public void updateUserProfile (User currentUser) {
        Log.i(TAG, "updateUserProfile: Name: "+currentUser.getName());
        // This has to be validated and put in cache as it is
        String name = currentUser.getName();
        String email = currentUser.getEmailId();
        Integer age = currentUser.getAge();
        Long mobile = currentUser.getMobile();
        // TODO: Validations must be done here!
        saveUserInCache(currentUser);
        // Once the cache is updated, also update this in Firebase
        updateUserProfile(currentUser, firebaseAuth.getUid());
    }

    /**
     * This method writes the user details into the database,
     * with the details from the user instance
     * @param user - Info of the new user
     * @param uid - UID provided by the Firebase Auth
     */
    private void updateUserProfile(final User user, @NonNull String uid) {
        DatabaseReference newUserNode = firebaseDatabase.getReference().child(ALL_USERS_NODE).child(uid).child("profile");
        final DatabaseReference registeredUsersNode = firebaseDatabase.getReference().child(ALL_REGISTERED_USERS_NODE).child(CURRENT_USER.getUid());

        OnSuccessListener successListener = new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                Log.i(TAG, "onSuccess: User added in the database");
                profileListener.onProfileUpdated();
            }
        };
        OnFailureListener failureListener = new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i(TAG, "onFailure: User could not be added in DB::: "+e.getLocalizedMessage());
            }
        };
        OnCompleteListener<Task> completeListener = new OnCompleteListener<Task>() {
            @Override
            public void onComplete(@NonNull Task<Task> task) {
                Log.i(TAG, "onComplete: "+task.isSuccessful());
            }
        };

        newUserNode.setValue(user)
                .addOnSuccessListener(successListener)
                .addOnFailureListener(failureListener)
                .addOnCompleteListener(completeListener);
        
        final OnCompleteListener<Void> completeListener1 = new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.i(TAG, "onComplete: User added in registered users list.");
                }
                else {
                    Log.i(TAG, "onComplete: User was not added in the registered users list.");
                }
            }
        };

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Check if the user is phone or email authenticated
                if (firebaseAuth.getCurrentUser().getProviders().get(0).equals(EMAIL_PROVIDER)) {
                    // Email provider - Add email for this UID
                    registeredUsersNode.setValue(user.getEmailId().toLowerCase()).addOnCompleteListener(completeListener1);
                }
                else if (firebaseAuth.getCurrentUser().getProviders().get(0).equals(PHONE_PROVIDER)) {
                    // Phone provider - Add phone for this UID
                    registeredUsersNode.setValue(user.getMobile().toString()).addOnCompleteListener(completeListener1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        registeredUsersNode.addListenerForSingleValueEvent(valueEventListener);
    }

    /**
     * This method gets the current user profile from the Firebase Database
     * and gives it to the caller through callback method
     */
    private void getCurrentUserProfile() {
        String uid = firebaseAuth.getCurrentUser().getUid();
        DatabaseReference currentUserProfileNode = firebaseDatabase.getReference().child("users").child(uid).child("profile");
        // Create a value event authenticationListener to get the data from this node
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.i(TAG, "onDataChange: getting user profile...");
                User currentUser = new User();
                Log.i(TAG, "onDataChange: Provider: "+firebaseAuth.getCurrentUser().getProviders());
                if (dataSnapshot.getValue() != null && !dataSnapshot.getValue(User.class).getName().equals("NA")) {
                    currentUser = dataSnapshot.getValue(User.class);
                    Log.i(TAG, "onDataChange: Email: "+currentUser.getEmailId());

                }
                else {
                    // TODO: Since the data is null, based on the provider, put either email or mobile in the user
                    String provider = firebaseAuth.getCurrentUser().getProviders().get(0);
                    if (provider.equals(EMAIL_PROVIDER)) {
                        currentUser.setEmailId(firebaseAuth.getCurrentUser().getEmail());
                    }
                    else if (provider.equals(PHONE_PROVIDER)){
                        Log.i(TAG, "onDataChange: Mobile: "+firebaseAuth.getCurrentUser().getPhoneNumber());
                        Long mobile = Long.parseLong(convertE164toNormalMobile(firebaseAuth.getCurrentUser().getPhoneNumber()));
                        currentUser.setMobile(mobile);
                        Log.i(TAG, "onDataChange: Mobile: "+mobile);
                    }
                }
                saveUserInCache(currentUser);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i(TAG, "onCancelled: "+databaseError.getDetails());
            }
        };
        currentUserProfileNode.addListenerForSingleValueEvent(valueEventListener);
    }

    /**
     * This method saves the current user data
     * in a shared preferences file. This happens when the
     * user has logged in to the device
     * @param currentUser
     */
    private void saveUserInCache(User currentUser) {
        Log.i(TAG, "saveUserInCache: *");
        SharedPreferences userProfile = getApplication().getSharedPreferences("USER_PROFILE", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = userProfile.edit();
        editor.putString("name", currentUser.getName());
        editor.putLong("mobile", currentUser.getMobile());
        editor.putInt("age",currentUser.getAge());
        editor.putString("gender",currentUser.getGender());
        editor.putString("emailId", currentUser.getEmailId());
        editor.putString("location", currentUser.getLocation());
        editor.apply();
    }

    /**
     * This will logout the current user
     */
    public void logout() {
        Log.i(TAG, "logout: *");
        firebaseAuth.signOut();
        UPDATE_PROFILE_DIALOG_SHOWN = false;
    }

    /**
     * Similar to the save method, this will erase the user profile
     * from the device cache
     */
    private void eraseUserDataFromCache() {
        Log.i(TAG, "eraseUserDataFromCache: *");
        SharedPreferences userProfile = getApplication().getSharedPreferences("USER_PROFILE", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = userProfile.edit();
        editor.clear();
        editor.apply();
    }

    /**
     * This method loads the user profile data from the
     * shared preferences file. The task runs in the
     * background.
     */
    public User loadUserProfile() {
        SharedPreferences sharedPreferences = getApplication().getSharedPreferences("USER_PROFILE", Context.MODE_PRIVATE);
        User user = new User();
        user.setName(sharedPreferences.getString("name", "NA"));
        user.setMobile(sharedPreferences.getLong("mobile", 0l));
        user.setAge(sharedPreferences.getInt("age", 0));
        user.setGender(sharedPreferences.getString("gender", "NA"));
        user.setEmailId(sharedPreferences.getString("emailId", "NA"));
        user.setLocation(sharedPreferences.getString("location", "NA"));
        return user;
    }

    /**
     * Called in order to add an auth state listener
     */
    private void addAuthStateListener () {
        firebaseAuth.addAuthStateListener(this.authStateListener);
    }

    public void setEmailLoginListener(AuthListener.EmailLogin emailLoginListener) {
        this.emailLoginListener = emailLoginListener;
    }

    public void setEmailSignupListener(AuthListener.EmailSignup emailSignupListener) {
        this.emailSignupListener = emailSignupListener;
    }

    public void setMobileLoginListener(AuthListener.Mobile mobileLoginListener) {
        this.mobileLoginListener = mobileLoginListener;
    }

    public void setLogoutListener(AuthListener.Logout logoutListener) {
        this.logoutListener = logoutListener;
        addAuthStateListener();
    }

    public void setInitialProfileListener(ProfileListener.InitialProfile initialProfileListener) {
        this.initialProfileListener = initialProfileListener;
    }

    public void setProfileListener(ProfileListener profileListener) {
        this.profileListener = profileListener;
    }

    private String convertE164toNormalMobile (String mobile) {
        if (mobile == null || mobile.equals("")) {
            return "0";
        }
        mobile = mobile.substring(3);
        return mobile;
    }
}
