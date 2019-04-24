package singareddy.productionapps.capturethemoment.book.addbook;

import android.app.Activity;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import singareddy.productionapps.capturethemoment.DataRepository;

public final class AddBookModelFactory implements ViewModelProvider.Factory {

    private DataRepository mRepository;

    public static AddBookModelFactory createFactory (Activity activity) {
        // TODO: Figure out if the activity has been started or not
        return new AddBookModelFactory(DataRepository.getInstance(activity));
    }

    private AddBookModelFactory (DataRepository repository) {
        mRepository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new AddBookViewModel(mRepository);
    }
}
