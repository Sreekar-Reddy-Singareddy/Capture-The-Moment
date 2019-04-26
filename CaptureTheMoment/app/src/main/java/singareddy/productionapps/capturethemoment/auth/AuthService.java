package singareddy.productionapps.capturethemoment.auth;

import android.support.annotation.NonNull;
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

import java.util.concurrent.TimeUnit;

public class AuthService {
    private static String TAG = "AuthService";

    private FirebaseAuth mFirebaseAuth;
    private String verificationId;

    private AuthenticationListener.EmailLogin emailLoginListener;
    private AuthenticationListener.Mobile mobileAuthListener;

    public AuthService () {
        mFirebaseAuth = FirebaseAuth.getInstance();
    }

    public void loginUserWithEmail(String email, String password) {
        OnSuccessListener<AuthResult> loginSuccessListener = new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Log.i(TAG, "onSuccess: *");
                emailLoginListener.onEmailUserLoginSuccess();
            }
        };

        OnFailureListener loginFailureListener = new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i(TAG, "onFailure: "+e.getMessage());
                emailLoginListener.onEmailUserLoginFailure("");
            }
        };
        mFirebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(loginSuccessListener)
                .addOnFailureListener(loginFailureListener);
    }

    public void authorizePhoneCredentials(String mobile, String otpCode) {
        // Use manual OTP and create a credential
        if (otpCode != null && !otpCode.isEmpty()) {
            Log.i(TAG, "authorizePhoneCredentials: MANUAL_OTP");
            // Manual OTP entered.
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otpCode);
            verifyCredential(credential);
            return;
        }

        Log.i(TAG, "authorizePhoneCredentials: AUTO-OTP");
        PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                Log.i(TAG, "onVerificationCompleted: *");
                verifyCredential(phoneAuthCredential);
            }

            @Override
            public void onCodeAutoRetrievalTimeOut(String verificationId) {
                super.onCodeAutoRetrievalTimeOut(verificationId);
                Log.i(TAG, "onCodeAutoRetrievalTimeOut: *");
                AuthService.this.verificationId = verificationId;
                mobileAuthListener.onOtpRetrievalFailed();
            }

            @Override
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(verificationId, forceResendingToken);
                Log.i(TAG, "onCodeSent: *");
                mobileAuthListener.onOtpSent();
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Log.i(TAG, "onVerificationFailed: " + e.getLocalizedMessage());
            }
        };
        PhoneAuthProvider.getInstance().verifyPhoneNumber(mobile, 10, TimeUnit.SECONDS, TaskExecutors.MAIN_THREAD, callbacks);
    }

    private void verifyCredential(PhoneAuthCredential credential) {
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            Log.i(TAG, "onComplete: Mobile verified.");
                            mobileAuthListener.onMobileAuthenticationSuccess();
                        }
                        else {
                            mobileAuthListener.onMobileAuthenticationFailure("LOGIN_FAIL");
                        }
                    }
                });
    }

    public void setEmailLoginListener(AuthenticationListener.EmailLogin emailLoginListener) {
        this.emailLoginListener = emailLoginListener;
    }

    public void setMobileAuthListener(AuthenticationListener.Mobile mobileAuthListener) {
        this.mobileAuthListener = mobileAuthListener;
    }
}
