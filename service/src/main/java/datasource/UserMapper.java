package datasource;

import domain.Admin;
import domain.EventPlanner;
import domain.Ticket;
import domain.User;
import org.springframework.dao.ConcurrencyFailureException;
import util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserMapper {
    public static void insert(User user) throws Exception {
        int version=0;
        int inserted = 0;
        try (Connection con = DatabaseConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement("INSERT INTO user(user_name, user_email, user_password, user_power) VALUES(?, ?, ?, ?)");
            ps.setString(1, user.getUserName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getHashPassword());
            ps.setInt(4, user.getPower());
            inserted = ps.executeUpdate();

        } catch (SQLException e) {

        }finally {
            version=version+inserted;

            if (inserted > 0) {

            } else {

                throwConcurrencyException(user ,version);

            }
        }


    }

    public static int login(String userName, String userPassword) {
        int user_power = 0;
        try (Connection con = DatabaseConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT user_power FROM user WHERE user_name=? AND user_password=?");
            ps.setString(1, userName);
            ps.setString(2, userPassword);
            ResultSet rs = ps.executeQuery();
            user_power = 0;
            if (rs.next()) {
                user_power = rs.getInt("user_power");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user_power;
    }

    public static User getUserByNameAndPassword(String userName, String userPassword) {
        User user = null;
        try (Connection con = DatabaseConnection.getConnection()) {
            // ...（数据库连接代码）
            PreparedStatement ps = con.prepareStatement("SELECT * FROM user WHERE user_name=? AND user_password=?");
            ps.setString(1, userName);
            ps.setString(2, userPassword);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int userId = rs.getInt("user_id");
                int userPower = rs.getInt("user_power");
                String email = rs.getString("user_email");
                user = new User(userId, userName, userPassword, userPower, email);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    public List<User> getAllUser() {
        List<User> users = new ArrayList<>();
        try (Connection con = DatabaseConnection.getConnection()) {
            // ...（数据库连接代码）
            PreparedStatement ps = con.prepareStatement("SELECT * FROM user ");

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                User user = new User(
                        rs.getInt("user_id"),
                        rs.getString("user_name"),
                        rs.getString("user_password"),
                        rs.getInt("user_power"),
                        rs.getString("user_email")
                );
                users.add(user);
            }

        } catch (Exception e) {
            e.printStackTrace();

        }
        return users;
    }

    public static int updateUser(User user) throws SQLIntegrityConstraintViolationException {
        int updated = 0;
        try (Connection con = DatabaseConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement("UPDATE user SET user_name=?, user_email=?, user_password=?, user_power=? WHERE user_id=?");
            ps.setString(1, user.getUserName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getHashPassword());
            ps.setInt(4, user.getPower());
            ps.setInt(5, user.getUserId());
            updated = ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            throw new SQLIntegrityConstraintViolationException("Already have this username", e);
        }
        return updated;
    }

    public static int deleteUser(User user) throws Exception {
        int deleted = 0;
        try (Connection con = DatabaseConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement("DELETE FROM user WHERE user_id=?");
            ps.setInt(1, user.getUserId());
            deleted = ps.executeUpdate();
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e);
        }
        return deleted;
    }

    public static List<EventPlanner> findEventPlannersByEventId(int eventId) {
        List<EventPlanner> eventPlanners = new ArrayList<>();
        try (Connection con = DatabaseConnection.getConnection()) {
            // SQL 查询用于从关联表中获取所有与给定 Event 相关联的 EventPlanner
            String query = "SELECT * FROM event_planner_association JOIN user ON event_planner_association.association_planner_id = user.user_id WHERE association_event_id = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, eventId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                // 创建一个新的 EventPlanner 对象，使用从 ResultSet 中获取的信息
                // 注意：这里需要你根据实际的 EventPlanner 和 User 的属性来填充 EventPlanner 对象。
                EventPlanner planner = new EventPlanner(
                        rs.getInt("user_id"),
                        rs.getString("user_name"),
                        rs.getString("user_password"),
                        rs.getString("user_email")
                );

                // 将新创建的 EventPlanner 对象添加到列表中
                eventPlanners.add(planner);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return eventPlanners;
    }

    public static EventPlanner findEventPlannerByUserName(String userName) {
        EventPlanner foundEventPlanner = null;
        try (Connection con = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM user WHERE user_name = ? AND user_power = 3";

            // 使用PreparedStatement对象来执行查询
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, userName);

            // 执行查询并获取结果集
            ResultSet rs = ps.executeQuery();

            // 处理结果集
            if (rs.next()) {
                foundEventPlanner = new EventPlanner(
                        rs.getInt("user_id"),
                        rs.getString("user_name"),
                        rs.getString("user_password"),
                        rs.getString("user_email")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return foundEventPlanner;
    }
    public static User FindUserById(int userId){
        try (Connection con = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM user WHERE user_id = ?";

            // 使用PreparedStatement对象来执行查询
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, userId);

            // 执行查询并获取结果集
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("user_id");
                String userName = rs.getString("user_name");
                String password = rs.getString("user_password");
                int power = rs.getInt("user_power");
                String email = rs.getString("user_email");

                // Determine the type of user based on the power
                switch (power) {
                    case 1: // Regular user
                        return new User(id, userName, password, power, email);

                    case 2: // Admin user
                        return new Admin(id, userName, password, email);

                    case 3: // EventPlanner user
                        return new EventPlanner(id, userName, password, email);

                    default:
                        return null;
                }
            } else {
                // User not found
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static User FindUserByName(String userName){
        try (Connection con = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM user WHERE user_name = ?";

            // 使用PreparedStatement对象来执行查询
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, userName);

            // 执行查询并获取结果集
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("user_id");
                String retrievedUserName = rs.getString("user_name");
                String password = rs.getString("user_password");
                int power = rs.getInt("user_power");
                String email = rs.getString("user_email");

                // Determine the type of user based on the power
                switch (power) {
                    case 1: // Regular user
                        return new User(id, retrievedUserName, password, power, email);

                    case 2: // Admin user
                        return new Admin(id, retrievedUserName, password, email);

                    case 3: // EventPlanner user
                        return new EventPlanner(id, retrievedUserName, password, email);

                    default:
                        return null;
                }
            } else {
                // User not found
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void throwConcurrencyException(User user, int version) throws Exception {

        try (Connection con = DatabaseConnection.getConnection()) {

            PreparedStatement ps = con.prepareStatement("SELECT user_version FROM user WHERE user_name = ?");
            ps.setString(1, user.getUserName());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int version2 = rs.getInt("user_version");
                System.out.println(version + "  " + version2);
                if (version2 > version) {

                    throw new ConcurrencyFailureException("user name concurrency!");
                } else {

                    throw new Exception("Unexpected error occurred");
                }
            } else {
                throw new ConcurrencyFailureException("No user " + user.getUserName() );
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Database error occurred", e);
        }

    }

}
