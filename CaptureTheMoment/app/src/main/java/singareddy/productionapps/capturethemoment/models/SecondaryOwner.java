package singareddy.productionapps.capturethemoment.models;

import android.util.Log;

import singareddy.productionapps.capturethemoment.utils.AppUtilities;

public class SecondaryOwner {

    private String username = "";
    private Boolean canEdit = false;
    private Integer validated = AppUtilities.Book.SEC_OWNER_NOT_VALIDATED;

    public SecondaryOwner() {
    }

    public SecondaryOwner(String username) {
        this.username = username;
    }

    public SecondaryOwner(String username, Boolean canEdit) {
        this.username = username;
        this.canEdit = canEdit;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Boolean getCanEdit() {
        return canEdit;
    }

    public void setCanEdit(Boolean canEdit) {
        this.canEdit = canEdit;
    }

    public Integer getValidated() {
        return validated;
    }

    public void setValidated(Integer validated) {
        this.validated = validated;
    }

    @Override
    public boolean equals(Object obj) {
        try {
            SecondaryOwner owner = (SecondaryOwner) obj;
            return owner.getUsername().toLowerCase().trim().equals(this.username.toLowerCase().trim());
        }
        catch (ClassCastException e) {
            System.out.println("Exception: "+e.getLocalizedMessage());
            return false;
        }
    }
}
