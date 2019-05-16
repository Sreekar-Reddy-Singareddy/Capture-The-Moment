package singareddy.productionapps.capturethemoment.card.delete;

import android.app.Activity;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import singareddy.productionapps.capturethemoment.DataRepository;
import singareddy.productionapps.capturethemoment.card.getcards.GetCardsViewModel;

public class DeleteCardsModelFactory implements ViewModelProvider.Factory {

    private DataRepository dataRepo;

    public static DeleteCardsModelFactory createFactory (Activity activity) {
        DataRepository repository = DataRepository.getInstance(activity);
        return new DeleteCardsModelFactory(repository);
    }

    private DeleteCardsModelFactory(DataRepository repository) {
        dataRepo = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new DeleteCardsViewModel(dataRepo);
    }
}
