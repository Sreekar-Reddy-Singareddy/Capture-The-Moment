package singareddy.productionapps.capturethemoment.card.getcards;

import android.app.Activity;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import singareddy.productionapps.capturethemoment.DataRepository;

public class GetCardsModelFactory implements ViewModelProvider.Factory {

    private DataRepository dataRepo;

    public static GetCardsModelFactory createFactory (Activity activity) {
        DataRepository repository = DataRepository.getInstance(activity);
        return new GetCardsModelFactory(repository);
    }

    private GetCardsModelFactory (DataRepository repository) {
        dataRepo = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new GetCardsViewModel(dataRepo);
    }
}
