package singareddy.productionapps.capturethemoment.book.details;

import android.app.Activity;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import singareddy.productionapps.capturethemoment.DataRepository;

public class UpdateBookModelFactory implements ViewModelProvider.Factory {

    private DataRepository mRepository;

    private UpdateBookModelFactory(DataRepository repository) {
        mRepository = repository;
    }

    public static UpdateBookModelFactory createFactory (Activity activity) {
        return new UpdateBookModelFactory(DataRepository.getInstance(activity));
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new UpdateBookViewModel(mRepository);
    }
}
