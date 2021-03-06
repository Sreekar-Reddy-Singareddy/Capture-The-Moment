package singareddy.productionapps.capturethemoment.user.auth;

import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.telephony.PhoneNumberUtils;

import com.google.firebase.auth.FirebaseAuth;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import singareddy.productionapps.capturethemoment.DataRepository;
import singareddy.productionapps.capturethemoment.models.User;
import singareddy.productionapps.capturethemoment.user.profile.ProfileListener;

import static singareddy.productionapps.capturethemoment.utils.AppUtilities.FailureCodes.*;
import static singareddy.productionapps.capturethemoment.utils.AppUtilities.FileNames.*;

public class AuthViewModel extends ViewModel implements
        AuthListener.EmailLogin, AuthListener.Mobile,
        AuthListener.EmailSignup, ProfileListener {
    private static String TAG = "AuthViewModel";

    private DataRepository mRepository;
    private AuthListener.EmailLogin emailLoginListener;
    private AuthListener.Mobile mobileAuthListener;
    private AuthListener.EmailSignup emailSignupListener;
    private ProfileListener profileListener;

    private static Boolean OTP_NEEDED = false;

    public AuthViewModel (DataRepository repository) {
        mRepository = repository;
    }

    public void registerEmailUser(String email, String password, String confPassword) {
        if (!isEmailValid(email)) {
            emailSignupListener.onEmailUserRegisterFailure(email, EMPTY_EMAIL);
            return;
        }

        if (!isPasswordValid(password) || !isPasswordValid(confPassword)) {
            emailSignupListener.onEmailUserRegisterFailure(email, EMPTY_PASSWORD);
            return;
        }

        if (!password.equals(confPassword)) {
            emailSignupListener.onEmailUserRegisterFailure(email, PASSWORD_MISMATCH);
            return;
        }

        // If all are valid, then proceed further
        mRepository.setEmailSignupListener(this);
        mRepository.registerEmailUser(email, password);
    }

    public void loginUserWithEmail(String email, String password) {
        if (!isEmailValid(email)) {
            emailLoginListener.onEmailUserLoginFailure(EMPTY_EMAIL);
            return;
        }

        if (!isPasswordValid(password)) {
            emailLoginListener.onEmailUserLoginFailure(EMPTY_PASSWORD);
            return;
        }

        mRepository.setEmailLoginListener(this);
        mRepository.loginUserWithEmail(email, password);
    }

    public void sendPasswordResetEmail(String email) {
        mRepository.setEmailLoginListener(this);
        mRepository.sendPasswordResetEmail(email);
    }

    private boolean isPasswordValid(String password) {
        if (password == null || password.isEmpty()) return false;
        return true;
    }

    private boolean isEmailValid(String email) {
        if (email == null || email.isEmpty()) return false;
        return true;
    }

    public void authorizePhoneCredentials(String mobile, String otpCode) {
        mobile = PhoneNumberUtils.formatNumberToE164(mobile, "IN");
        // Check if the mobile number is valid and not empty
        if (mobile == null || mobile.equals("")) {
            mobileAuthListener.onMobileAuthenticationFailure(EMPTY_MOBILE);
            return;
        }

        // If OTP is needed and it is null, then this is an invalid case
        if (OTP_NEEDED && (otpCode == null || otpCode.isEmpty())) {
            mobileAuthListener.onMobileAuthenticationFailure(NO_OTP);
            return;
        }

        // Since both mobile and otp are valid enough, continue
        mRepository.setMobileAuthListener(this);
        mRepository.authorizePhoneCredentials(mobile, otpCode);
    }

    public void eraseLocalData() {
        mRepository.eraseLocalData();
    }

    public void setupInitialData() {
        mRepository.setupInitialData();
    }

    public void setupUserProfile() {
        mRepository.setupUserProfile();
    }

    public SharedPreferences getUserProfileData() {
        return mRepository.getUserProfileData();
    }

    public Integer getNumberOfSharedBooks() {
        return mRepository.getNumberOfSharedBooks();
    }

    public Integer getNumberOfOwnedBooks() {
        return mRepository.getNumberOfOwnedBooks();
    }

    public Bitmap setProfilePic(Context context) {
        File userProfilePictureFile = new File(context.getFilesDir(), USER_PROFILE_PICTURE);
        if (userProfilePictureFile.exists()) {
            try {
                FileInputStream fileInputStream = new FileInputStream(userProfilePictureFile);
                byte[] imageData = IOUtils.toByteArray(fileInputStream);
                return BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    public void updateUserProfile(User userProfileToUpdate) {
        mRepository.setProfileListener(this);
        mRepository.updateUserProfile(userProfileToUpdate);
    }

    public void saveProfilePic(Uri profilePicUri) {
        if (profilePicUri == null) return;
        mRepository.setProfileListener(this);
        mRepository.saveProfilePic(profilePicUri);
    }

    public void logout() {
        FirebaseAuth.getInstance().signOut();
    }

    public Long validateMobile(String mobile) {
        Long convertedNumber = mobile.matches("[0-9]*") ? Long.parseLong(mobile) : null;
        return convertedNumber;
    }

    public void setEmailSignupListener(AuthListener.EmailSignup emailSignupListener) {
        this.emailSignupListener = emailSignupListener;
    }

    public void setEmailLoginListener(AuthListener.EmailLogin emailLoginListener) {
        this.emailLoginListener = emailLoginListener;
    }

    public void setMobileAuthListener(AuthListener.Mobile mobileAuthListener) {
        this.mobileAuthListener = mobileAuthListener;
    }

    public void setProfileListener(ProfileListener profileListener) {
        this.profileListener = profileListener;
    }

    @Override
    public void onEmailUserRegisterSuccess(String email) {
        emailSignupListener.onEmailUserRegisterSuccess(email);
    }

    @Override
    public void onEmailUserRegisterFailure(String email, String failureCode) {
        emailSignupListener.onEmailUserRegisterFailure(email, failureCode);
    }

    @Override
    public void onPasswordResetMailSent(String email) {
        emailLoginListener.onPasswordResetMailSent(email);
    }

    @Override
    public void onEmailUserLoginSuccess() {
        emailLoginListener.onEmailUserLoginSuccess();
    }

    @Override
    public void onEmailUserLoginFailure(String failureCode) {
        emailLoginListener.onEmailUserLoginFailure(failureCode);
    }

    @Override
    public void onMobileAuthenticationSuccess() {
        mobileAuthListener.onMobileAuthenticationSuccess();
    }

    @Override
    public void onMobileAuthenticationFailure(String failureCode) {
        mobileAuthListener.onMobileAuthenticationFailure(failureCode);
    }

    @Override
    public void onOtpSent() {
        mobileAuthListener.onOtpSent();
    }

    @Override
    public void onOtpRetrievalFailed() {
        mobileAuthListener.onOtpRetrievalFailed();
    }

    @Override
    public void onProfileUpdated() {
        profileListener.onProfileUpdated();
    }

    @Override
    public void onProfilePicUpdated() {
        profileListener.onProfilePicUpdated();
    }
}
