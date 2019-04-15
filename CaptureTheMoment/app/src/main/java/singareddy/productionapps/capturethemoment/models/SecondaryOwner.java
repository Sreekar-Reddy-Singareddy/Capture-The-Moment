package singareddy.productionapps.capturethemoment.models;

public class SecondaryOwner {

    private String username = "";
    private Boolean canEdit = false;
    private Integer validated = 0;

    public SecondaryOwner() {
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
}
