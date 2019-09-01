package singareddy.productionapps.capturethemoment.card.delete;

import android.arch.lifecycle.ViewModel;

import singareddy.productionapps.capturethemoment.DataRepository;
import singareddy.productionapps.capturethemoment.models.Card;

public class DeleteCardsViewModel extends ViewModel implements DeleteCardListener {

    private DataRepository dataRepo;
    private DeleteCardListener deleteCardListener;

    public DeleteCardsViewModel(DataRepository dataRepo) {
        this.dataRepo = dataRepo;
    }


    public void deleteCardWithId(Card card) {
        dataRepo.setDeleteCardListener(this);
        dataRepo.deleteCardWithId(card);
    }

    public void setDeleteCardListener(DeleteCardListener deleteCardListener) {
        this.deleteCardListener = deleteCardListener;
    }

    @Override
    public void onCardDeleted(String cardId, String bookId) {
        deleteCardListener.onCardDeleted(cardId, null);
    }
}
