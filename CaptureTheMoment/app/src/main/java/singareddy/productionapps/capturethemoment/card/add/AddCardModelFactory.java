package singareddy.productionapps.capturethemoment.card.add;

import android.app.Activity;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import singareddy.productionapps.capturethemoment.DataRepository;

public class AddCardModelFactory implements ViewModelProvider.Factory {

    private DataRepository dataRepo;

    public static AddCardModelFactory createFactory(Activity activity) {
        DataRepository repo = DataRepository.getInstance(activity);
        return new AddCardModelFactory(repo);
    }

    private AddCardModelFactory (DataRepository repository) {
        dataRepo = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new AddCardViewModel(dataRepo);
    }
}
