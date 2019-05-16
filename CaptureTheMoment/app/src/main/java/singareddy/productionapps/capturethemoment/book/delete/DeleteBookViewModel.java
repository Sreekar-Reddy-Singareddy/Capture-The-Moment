package singareddy.productionapps.capturethemoment.book.delete;

import android.arch.lifecycle.ViewModel;

import singareddy.productionapps.capturethemoment.DataRepository;

public class DeleteBookViewModel extends ViewModel implements DeleteBookListener {

    private DataRepository dataRepo;
    private DeleteBookListener deleteBookListener;

    public DeleteBookViewModel(DataRepository repository) {
        dataRepo = repository;
    }

    public void deleteBook(String bookId) {
        dataRepo.setDeleteBookListener(this);
        dataRepo.deleteBook(bookId);
    }

    public void setDeleteBookListener(DeleteBookListener deleteBookListener) {
        this.deleteBookListener = deleteBookListener;
    }

    @Override
    public void onBookDeleted() {
        deleteBookListener.onBookDeleted();
    }
}
