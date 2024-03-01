package domain;

import java.util.List;

public class Admin extends User{
    public Admin(int userId, String userName, String password, String email) {
        super(userId, userName, password, 2, email);
    }
}
