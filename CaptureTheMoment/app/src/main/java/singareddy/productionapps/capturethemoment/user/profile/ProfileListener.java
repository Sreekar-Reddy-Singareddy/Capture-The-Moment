package singareddy.productionapps.capturethemoment.user.profile;

public interface ProfileListener {
    public void onProfileUpdated();
    default public void onProfilePicUpdated(){}
}