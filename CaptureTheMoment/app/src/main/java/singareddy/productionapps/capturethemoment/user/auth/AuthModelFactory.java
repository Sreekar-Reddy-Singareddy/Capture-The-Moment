package singareddy.productionapps.capturethemoment.user.auth;

import android.app.Activity;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import singareddy.productionapps.capturethemoment.DataRepository;

public class AuthModelFactory implements ViewModelProvider.Factory {
    private DataRepository mRepository;

    private AuthModelFactory (DataRepository repository) {
        mRepository = repository;
    }

    public static AuthModelFactory createFactory (Activity activity) {
        return new AuthModelFactory(DataRepository.getInstance(activity));
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new AuthViewModel(mRepository);
    }
}
