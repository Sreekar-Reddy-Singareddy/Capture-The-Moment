package singareddy.productionapps.capturethemoment.book;

public interface BookListener {
    public void onBookNameInvalid (String code);
    public void onAllSecOwnersValidated ();
    public void onThisSecOwnerValidated ();
    default public void onNewBookCreated () {

    }

    public interface UpdateBook {
        default public void onBookUpdatedWithNewId(String newBookId) {}
    }
}
