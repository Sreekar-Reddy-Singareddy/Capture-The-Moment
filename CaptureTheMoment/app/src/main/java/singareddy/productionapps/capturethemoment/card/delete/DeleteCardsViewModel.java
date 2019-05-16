package singareddy.productionapps.capturethemoment.card.delete;

import android.arch.lifecycle.ViewModel;

import singareddy.productionapps.capturethemoment.DataRepository;

public class DeleteCardsViewModel extends ViewModel {

    private DataRepository dataRepo;

    public DeleteCardsViewModel(DataRepository dataRepo) {
        this.dataRepo = dataRepo;
    }


    public void deleteCardWithId(String cardId) {
        dataRepo.deleteCardWithId(cardId);
    }
}
