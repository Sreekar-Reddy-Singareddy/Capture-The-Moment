package singareddy.productionapps.capturethemoment.user.auth;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import singareddy.productionapps.capturethemoment.HomeActivity;
import singareddy.productionapps.capturethemoment.R;

public class MobileSignup extends Fragment implements AuthListener.Mobile {
    private static String TAG = "MobileSignup";

    AuthViewModel authViewModel;
    private MobileAuthLogic mobileAuthLogic;
    View fragmentView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.activity_mobile_login, container, false);
        initialiseViewModel();
        initialiseUI();
        return fragmentView;
    }

    private void initialiseUI() {
        ImageView logo = fragmentView.findViewById(R.id.activity_mobile_login_iv_logo);
        logo.setVisibility(View.GONE);
        mobileAuthLogic = new MobileAuthLogic(getContext(), fragmentView, authViewModel);
    }

    private void initialiseViewModel() {
        AuthModelFactory factory = AuthModelFactory.createFactory(getActivity());
        authViewModel = ViewModelProviders.of(this, factory).get(AuthViewModel.class);
        authViewModel.setMobileAuthListener(this);
    }

    @Override
    public void onMobileAuthenticationSuccess() {
        mobileAuthLogic.loginSuccess();
        getActivity().finish();
    }

    @Override
    public void onMobileAuthenticationFailure(String failureCode) {
        mobileAuthLogic.loginFailed(failureCode);
    }

    @Override
    public void onOtpSent() {
        Log.i(TAG, "onOtpSent: **");
        mobileAuthLogic.autoRetrieveOtp();
    }

    @Override
    public void onOtpRetrievalFailed() {

    }

    @Override
    public void onPause() {
        super.onPause();
        authViewModel.reset();
    }
}
