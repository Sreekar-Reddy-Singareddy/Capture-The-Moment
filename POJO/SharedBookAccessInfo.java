public class SharedBookAccessInfo {
    private String bookId;
    private Boolean canEdit;

    public SharedBookAccessInfo() {
    }

    public SharedBookAccessInfo(String bookId, Boolean canEdit) {
        this.bookId = bookId;
        this.canEdit = canEdit;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public Boolean getCanEdit() {
        return canEdit;
    }

    public void setCanEdit(Boolean canEdit) {
        this.canEdit = canEdit;
    }
}
