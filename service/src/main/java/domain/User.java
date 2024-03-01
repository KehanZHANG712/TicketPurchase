package domain;

public class User {
    public User(String userName, String hashPassword, int power, String email) {
        this.userName = userName;
        this.hashPassword = hashPassword;
        this.power = power;
        this.email = email;
    }

    public User(int userId, String userName, String hashPassword, int power, String email) {
        this.userId = userId;
        this.userName = userName;
        this.hashPassword = hashPassword;
        this.power = power;
        this.email = email;
    }

    private int userId;
    private String userName;
    private String hashPassword;
    private int power;
    private String email;
    // getters and setters
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getHashPassword() {
        return hashPassword;
    }

    public void setHashPassword(String hashPassword) {
        this.hashPassword = hashPassword;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

