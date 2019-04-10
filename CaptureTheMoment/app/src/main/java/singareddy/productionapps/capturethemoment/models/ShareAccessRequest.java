package singareddy.productionapps.capturethemoment.models;

import java.util.List;

public class ShareAccessRequest {
    private String sender;
    private String reciever;
    private List<String> bookIds;
    private String action;
    private Boolean response;
    private Long postedTime;
    private Boolean readStatus;

    public ShareAccessRequest() {
    }

    public ShareAccessRequest(String sender, String reciever, List<String> bookIds, String action, Long postedTime) {
        this.sender = sender;
        this.reciever = reciever;
        this.bookIds = bookIds;
        this.action = action;
        this.postedTime = postedTime;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReciever() {
        return reciever;
    }

    public void setReciever(String reciever) {
        this.reciever = reciever;
    }

    public List<String> getBookIds() {
        return bookIds;
    }

    public void setBookIds(List<String> bookIds) {
        this.bookIds = bookIds;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Boolean getResponse() {
        return response;
    }

    public void setResponse(Boolean response) {
        this.response = response;
    }

    public Long getPostedTime() {
        return postedTime;
    }

    public void setPostedTime(Long postedTime) {
        this.postedTime = postedTime;
    }

    public Boolean getReadStatus() {
        return readStatus;
    }

    public void setReadStatus(Boolean readStatus) {
        this.readStatus = readStatus;
    }
}
