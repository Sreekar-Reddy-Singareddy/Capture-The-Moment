import java.util.List;

public class User {
    private String name;
    private Long mobile;
    private Integer age;
    private String gender;
    private String emailId;
    private String profilePic;
    private String location;
    private List<String> ownedBooks;
    private List<SharedBookAccessInfo> sharedBooks;

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

    public List<SharedBookAccessInfo> getSharedBooks() {
        return sharedBooks;
    }

    public void setSharedBooks(List<SharedBookAccessInfo> sharedBooks) {
        this.sharedBooks = sharedBooks;
    }
}
