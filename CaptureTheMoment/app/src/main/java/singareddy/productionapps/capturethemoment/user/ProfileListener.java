package singareddy.productionapps.capturethemoment.user;

public interface ProfileListener {
    public void onProfileUpdated();
    public void onProfileUpdateFailed(String failureCause);

    public interface InitialProfile {
        public void onUserProfilePending();
    }
}
