package datasource;

import domain.User;
import domain.Venue;
import util.DatabaseConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class VenueMapper {
    public static int insert(Venue venue) {

        int inserted=0;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://hk-cdb-29mv0y7d.sql.tencentcdb.com:63928/swen90007?user=root", "root", "swen90007");

            PreparedStatement ps = con.prepareStatement("INSERT INTO venue(venue_address, venue_capacity, mosh_capacity, standing_capacity,seated_capacity,vip_capacity) VALUES(?, ?, ?, ?, ?, ?)");
            ps.setString(1, venue.getVenueAddress());
            ps.setInt(2, venue.getVenueCapacity());
            ps.setInt(3, venue.getMoshCapacity());
            ps.setInt(4, venue.getStandingCapacity());
            ps.setInt(5, venue.getSeatedCapacity());
            ps.setInt(6, venue.getVipCapacity());
            inserted = ps.executeUpdate();
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return inserted;

    }
    public static List<Venue> getAllVenues() {
        List<Venue> venues = new ArrayList<>();
        String sql = "SELECT * FROM venue";  // 假设你的表名是 venues

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                int venueId = resultSet.getInt("venue_id");
                String venueAddress = resultSet.getString("venue_address");
                int venueCapacity = resultSet.getInt("venue_capacity");
                int moshCapacity = resultSet.getInt("mosh_capacity");
                int standingCapacity = resultSet.getInt("standing_capacity");
                int seatedCapacity = resultSet.getInt("seated_capacity");
                int vipCapacity = resultSet.getInt("vip_capacity");

                Venue venue = new Venue(venueAddress, venueCapacity, moshCapacity, standingCapacity, seatedCapacity, vipCapacity);
                venue.setVenueId(venueId);  // 设置ID
                venues.add(venue);
            }

        } catch (Exception e) {
            e.printStackTrace();
            // 处理异常，可能需要更详细的处理或日志记录
        }
        return venues;
    }
    public static Venue findVenueByName(String venueName) {
        Venue venue = null; // 初始化为 null，如果找不到则返回 null
        String sql = "SELECT * FROM venue WHERE venue_address = ?"; // 使用 WHERE 子句来查找特定名称的场地

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, venueName);  // 设置查询参数
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int venueId = resultSet.getInt("venue_id");
                String venueAddress = resultSet.getString("venue_address");
                int venueCapacity = resultSet.getInt("venue_capacity");
                int moshCapacity = resultSet.getInt("mosh_capacity");
                int standingCapacity = resultSet.getInt("standing_capacity");
                int seatedCapacity = resultSet.getInt("seated_capacity");
                int vipCapacity = resultSet.getInt("vip_capacity");

                venue = new Venue(venueAddress, venueCapacity, moshCapacity, standingCapacity, seatedCapacity, vipCapacity);
                venue.setVenueId(venueId); // 设置ID
                // 如果有一个用于设置名称的方法，也可以设置名称
                // venue.setVenueName(venueName);
            }

        } catch (Exception e) {
            e.printStackTrace();
            // 处理异常，可能需要更详细的处理或日志记录
        }
        return venue; // 返回找到的 Venue 对象，或者如果找不到则返回 null
    }

    public static int deleteVenue(Venue venue) {
        int deleted = 0;
        try (Connection con = DatabaseConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement("DELETE FROM venue WHERE venue_id=?");
            ps.setInt(1, venue.getVenueId());
            deleted = ps.executeUpdate();
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return deleted;
    }

    public static Venue findVenueById(int venueName) {
        Venue venue = null; // 初始化为 null，如果找不到则返回 null
        String sql = "SELECT * FROM venue WHERE venue_id = ?"; // 使用 WHERE 子句来查找特定名称的场地

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, venueName);  // 设置查询参数
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int venueId = resultSet.getInt("venue_id");
                String venueAddress = resultSet.getString("venue_address");
                int venueCapacity = resultSet.getInt("venue_capacity");
                int moshCapacity = resultSet.getInt("mosh_capacity");
                int standingCapacity = resultSet.getInt("standing_capacity");
                int seatedCapacity = resultSet.getInt("seated_capacity");
                int vipCapacity = resultSet.getInt("vip_capacity");

                venue = new Venue(venueAddress, venueCapacity, moshCapacity, standingCapacity, seatedCapacity, vipCapacity);
                venue.setVenueId(venueId); // 设置ID
                // 如果有一个用于设置名称的方法，也可以设置名称
                // venue.setVenueName(venueName);
            }

        } catch (Exception e) {
            e.printStackTrace();
            // 处理异常，可能需要更详细的处理或日志记录
        }
        return venue; // 返回找到的 Venue 对象，或者如果找不到则返回 null
    }
    public static int updateVenue(Venue venue) {
        int updated = 0;
        try (Connection con = DatabaseConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement("UPDATE venue SET venue_address=?, venue_capacity=?, mosh_capacity=?, standing_capacity=?, seated_capacity=?, vip_capacity=? WHERE venue_id=?");
            ps.setString(1, venue.getVenueAddress());
            ps.setInt(2, venue.getVenueCapacity());
            ps.setInt(3, venue.getMoshCapacity());
            ps.setInt(4, venue.getStandingCapacity());
            ps.setInt(5, venue.getSeatedCapacity());
            ps.setInt(6, venue.getVipCapacity());
            ps.setInt(7, venue.getVenueId());
            updated = ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return updated;
    }
}
