package singareddy.productionapps.capturethemoment.book.delete;

import android.app.Activity;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import singareddy.productionapps.capturethemoment.DataRepository;
import singareddy.productionapps.capturethemoment.book.add.AddBookViewModel;

public final class DeleteBookModelFactory implements ViewModelProvider.Factory {

    private DataRepository mRepository;

    public static DeleteBookModelFactory createFactory (Activity activity) {
        // TODO: Figure out if the activity has been started or not
        return new DeleteBookModelFactory(DataRepository.getInstance(activity));
    }

    private DeleteBookModelFactory(DataRepository repository) {
        mRepository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new DeleteBookViewModel(mRepository);
    }
}
