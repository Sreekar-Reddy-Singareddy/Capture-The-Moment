package singareddy.productionapps.capturethemoment.book;

public interface AddBookListener {
    public void onBookNameInvalid (String code);
    public void onAllSecOwnersValidated ();
    public void onThisSecOwnerValidated ();
}
