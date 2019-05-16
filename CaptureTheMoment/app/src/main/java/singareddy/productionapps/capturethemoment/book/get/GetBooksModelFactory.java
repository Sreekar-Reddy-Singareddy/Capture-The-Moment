package singareddy.productionapps.capturethemoment.book.get;

import android.app.Activity;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import singareddy.productionapps.capturethemoment.DataRepository;

public class GetBooksModelFactory implements ViewModelProvider.Factory {
    private static String TAG = "GetBooksModelFactory";

    private DataRepository mRepository;

    public static GetBooksModelFactory createFactory(Activity activity) {
        return new GetBooksModelFactory(DataRepository.getInstance(activity));
    }

    private GetBooksModelFactory (DataRepository repository) {
        mRepository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new GetBooksViewModel(mRepository);
    }
}
