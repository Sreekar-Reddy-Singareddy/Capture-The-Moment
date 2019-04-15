package singareddy.productionapps.capturethemoment.models;

import java.util.List;

public class User {
    private String name = "NA";
    private Long mobile = 0l;
    private Integer age = 0;
    private String gender = "NA";
    private String emailId = "NA";
    private String profilePic = "NA";
    private String location = "NA";
    private List<String> ownedBooks;
    private List<ShareInfo> sharedBooks;

    public User() {

    }

    public User(String name, Long mobile, Integer age, String gender, String emailId) {
        this.name = name;
        this.mobile = mobile;
        this.age = age;
        this.gender = gender;
        this.emailId = emailId;
    }

    public User(String name, Long mobile, Integer age, String gender, String emailId, String profilePic, String location) {
        this.name = name;
        this.mobile = mobile;
        this.age = age;
        this.gender = gender;
        this.emailId = emailId;
        this.profilePic = profilePic;
        this.location = location;
    }

    public User(String name, Long mobile, Integer age, String gender, String emailId, String profilePic, String location, List<String> ownedBooks) {
        this.name = name;
        this.mobile = mobile;
        this.age = age;
        this.gender = gender;
        this.emailId = emailId;
        this.profilePic = profilePic;
        this.location = location;
        this.ownedBooks = ownedBooks;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getMobile() {
        return mobile;
    }

    public void setMobile(Long mobile) {
        this.mobile = mobile;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<String> getOwnedBooks() {
        return ownedBooks;
    }

    public void setOwnedBooks(List<String> ownedBooks) {
        this.ownedBooks = ownedBooks;
    }

    public List<ShareInfo> getSharedBooks() {
        return sharedBooks;
    }

    public void setSharedBooks(List<ShareInfo> sharedBooks) {
        this.sharedBooks = sharedBooks;
    }
}
