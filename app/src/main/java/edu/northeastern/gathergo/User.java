package edu.northeastern.gathergo;

import com.google.firebase.database.IgnoreExtraProperties;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@IgnoreExtraProperties
public class User {
    private String userId;
    private String userName;
    private String password;
    private String avatarId;
    private String email;
    private String phoneNumber;
    private String address;
    private String description;
    private Map<String, String> driverPostIds;
    private Map<String, String> riderPostIds;
    private List<String> createdEvents;
    private List<String> addedEvents;
    private List<String> likedEvents;
    private String createDateTimeStr;
    private Integer credits;

    public User() {

    }

    public User(String userId, String userName, String password, String avatarId, String email, String phoneNumber, String address, String description) {
        this.userId = userId;
        this.userName = userName;
        this.password = password;
        this.avatarId = avatarId;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.description = description;
        this.driverPostIds = new HashMap<>();
        this.riderPostIds = new HashMap<>();
        this.createdEvents = new ArrayList<>();
        this.addedEvents = new ArrayList<>();
        this.likedEvents = new ArrayList<>();

        // Create formatted datetime string
        LocalDateTime dateTimeObj = LocalDateTime.now();
        DateTimeFormatter formatObj = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss");
        this.createDateTimeStr = dateTimeObj.format(formatObj);
        this.credits = 10;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getAvatarId() {
        return avatarId;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getcreatedEvents() {
        return createdEvents;
    }

    public List<String> getaddedEvents() {
        return addedEvents;
    }

    public List<String> getLikedEvents() {
        return likedEvents;
    }

    public Map<String, String> getDriverPostIds() {
        return driverPostIds;
    }

    public void setDriverPostIds(Map<String, String> driverPostIds) {
        this.driverPostIds = driverPostIds;
    }

    public void setLikedEvents(List<String> likedEvents) {
        this.likedEvents = likedEvents;
    }

    public void setAddedEvents(List<String> addedEvents) {
        this.addedEvents = addedEvents;
    }

    public void setCreatedEvents(List<String> createdEvents) {
        this.createdEvents = createdEvents;
    }
    public void RemoveRegEvent(String theEvent) {
        this.addedEvents.remove(theEvent);
    }

    public Map<String, String> getRiderPostIds() {
        return riderPostIds;
    }

    public void setRiderPostIds(Map<String, String> riderPostIds) {
        this.riderPostIds = riderPostIds;
    }

    public String getCreateDateTimeStr() {
        return createDateTimeStr;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setAvatarId(String avatarId) {
        this.avatarId = avatarId;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setAddress() {
        this.address = address;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCreateDateTimeStr(String createDateTimeStr) {
        this.createDateTimeStr = createDateTimeStr;
    }

    public Integer getCredits() {
        return credits;
    }

    public void setCredits(Integer credits) {
        this.credits = credits;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", avatarId='" + avatarId + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", address='" + address + '\'' +
                ", description='" + description + '\'' +
                ", driverPostIds=" + driverPostIds +
                ", riderPostIds=" + riderPostIds +
                ", createDateTimeStr='" + createDateTimeStr + '\'' +
                ", credits=" + credits +
                '}';
    }
}
