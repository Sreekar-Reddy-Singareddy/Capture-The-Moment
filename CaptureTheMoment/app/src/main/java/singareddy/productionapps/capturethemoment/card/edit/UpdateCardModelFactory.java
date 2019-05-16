package singareddy.productionapps.capturethemoment.card.edit;

import android.app.Activity;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import singareddy.productionapps.capturethemoment.DataRepository;

public class UpdateCardModelFactory implements ViewModelProvider.Factory {

    private DataRepository dataRepo;

    public UpdateCardModelFactory(DataRepository repository) {
        dataRepo = repository;
    }

    public static UpdateCardModelFactory createFactory (Activity activity) {
        DataRepository repository = DataRepository.getInstance(activity);
        return new UpdateCardModelFactory(repository);
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new UpdateCardViewModel(dataRepo);
    }
}
