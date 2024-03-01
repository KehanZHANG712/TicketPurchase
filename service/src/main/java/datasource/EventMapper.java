package datasource;
import domain.Event;
import domain.EventPlanner;
import domain.Ticket;
import domain.User;
import org.springframework.dao.ConcurrencyFailureException;
import util.DatabaseConnection;
import util.LockingException;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

public class EventMapper {

    public static boolean insertEvent(Event event) throws SQLException {
        try (Connection con = DatabaseConnection.getConnection()){
            // 插入新的 Event，并获取生成的 ID
            String eventQuery = "INSERT INTO event(event_artist_name, event_name, event_venue, event_start_time, event_end_time, event_permission) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement eventPS = con.prepareStatement(eventQuery, Statement.RETURN_GENERATED_KEYS);
            eventPS.setString(1, event.getEventArtistName());
            eventPS.setString(2, event.getEventName());
            eventPS.setString(3, event.getEventVenue());
            eventPS.setObject(4, event.getEventStartTime());
            eventPS.setObject(5, event.getEventEndTime());
            eventPS.setInt(6, event.getEventPermission());
            eventPS.executeUpdate();

            ResultSet rs = eventPS.getGeneratedKeys();
            if (rs.next()) {
                int eventId = rs.getInt(1);
                event.setEventId(eventId);
            }

            // 插入与 Event 关联的 EventPlanner
            String relationQuery = "INSERT INTO event_planner_association(association_event_id, association_planner_id) VALUES (?, ?)";
            PreparedStatement relationPS = con.prepareStatement(relationQuery);
            for (EventPlanner planner : event.getEventPlanners()) {
                relationPS.setInt(1, event.getEventId());
                relationPS.setInt(2, planner.getUserId());
                relationPS.addBatch();
            }
            relationPS.executeBatch();
            return true;

        } catch (SQLException e) {
            throw new SQLException("name already in use");
        }
    }


    public static List<Event> findEventsByName(String name) {
        List<Event> events = new ArrayList<>();
        try (Connection con = DatabaseConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM event WHERE event_name LIKE ?");
            ps.setString(1, "%" + name + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int eventId = rs.getInt("event_id");
                String eventArtistName = rs.getString("event_artist_name");
                String eventName = rs.getString("event_name");
                String eventVenue = rs.getString("event_venue");
                LocalDateTime eventStartTime = rs.getObject("event_start_time", LocalDateTime.class);
                LocalDateTime eventEndTime = rs.getObject("event_end_time", LocalDateTime.class);
                int eventPermission = rs.getInt("event_permission");

                // 获取与该事件相关的所有 EventPlanner
                List<EventPlanner> eventPlanners = UserMapper.findEventPlannersByEventId(eventId);

                Event event = new Event(
                        eventId,
                        eventArtistName,
                        eventName,
                        eventVenue,
                        eventStartTime,
                        eventEndTime,
                        eventPermission,
                        eventPlanners
                );
                events.add(event);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return events;
    }

    public static List<Event> findEventsByDate(LocalDate date) {
        List<Event> events = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM event WHERE DATE(event_start_time) = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setDate(1, Date.valueOf(date));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int eventId = rs.getInt("event_id");
                String eventArtistName = rs.getString("event_artist_name");
                String eventName = rs.getString("event_name");
                String eventVenue = rs.getString("event_venue");
                LocalDateTime eventStartTime = rs.getObject("event_start_time", LocalDateTime.class);
                LocalDateTime eventEndTime = rs.getObject("event_end_time", LocalDateTime.class);
                int eventPermission = rs.getInt("event_permission");

                // 获取与该事件相关的所有 EventPlanner
                List<EventPlanner> eventPlanners = UserMapper.findEventPlannersByEventId(eventId);

                Event event = new Event(
                        eventId,
                        eventArtistName,
                        eventName,
                        eventVenue,
                        eventStartTime,
                        eventEndTime,
                        eventPermission,
                        eventPlanners
                );
                events.add(event);
            }
        } catch (Exception e) {
            // Handle the exception...
            e.printStackTrace();
        }

        return events;
    }

    public List<Event> findEventsByVenue(String venueAddress) {
        List<Event> events = new ArrayList<>();
        try (Connection con = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM event WHERE event_venue = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, venueAddress);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int eventId = rs.getInt("event_id");
                String eventArtistName = rs.getString("event_artist_name");
                String eventName = rs.getString("event_name");
                String eventVenue = rs.getString("event_venue");
                LocalDateTime eventStartTime = rs.getObject("event_start_time", LocalDateTime.class);
                LocalDateTime eventEndTime = rs.getObject("event_end_time", LocalDateTime.class);
                int eventPermission = rs.getInt("event_permission");

                // 获取与该事件相关的所有 EventPlanner
                List<EventPlanner> eventPlanners = UserMapper.findEventPlannersByEventId(eventId);

                Event event = new Event(
                        eventId,
                        eventArtistName,
                        eventName,
                        eventVenue,
                        eventStartTime,
                        eventEndTime,
                        eventPermission,
                        eventPlanners
                );
                events.add(event);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return events;
    }

    public static int updateEvent(Event event) {
        int updated = 0;
        try (Connection con = DatabaseConnection.getConnection()) {
            try (PreparedStatement ps = con.prepareStatement("UPDATE event SET event_artist_name=?, event_name=?, event_venue=?, event_start_time=?, event_end_time=?, event_permission=?, version=? WHERE event_id=? AND version=?")) {
                ps.setString(1, event.getEventArtistName());
                ps.setString(2, event.getEventName());
                ps.setString(3, event.getEventVenue());
                ps.setObject(4, event.getEventStartTime());
                ps.setObject(5, event.getEventEndTime());
                ps.setInt(6, event.getEventPermission());
                ps.setInt(7, event.getEventVersion() + 1);
                ps.setInt(8, event.getEventId());
                ps.setInt(9, event.getEventVersion());
                updated = ps.executeUpdate();
            } catch (SQLException ex){
                ex.printStackTrace();;
            }
            if(updated == 0){
                System.out.println("concurrency happens" + event.getEventStartTime());
                throwConcurrencyException(event);
            }
            try (PreparedStatement psDelete = con.prepareStatement("DELETE FROM event_planner_association WHERE association_event_id=?")) {
                psDelete.setInt(1, event.getEventId());
                psDelete.executeUpdate();
            }
            for (EventPlanner planner : event.getEventPlanners()) {
                try (PreparedStatement psInsert = con.prepareStatement("INSERT INTO event_planner_association (association_event_id, association_planner_id) VALUES(?, ?)")) {
                    psInsert.setInt(1, event.getEventId());
                    psInsert.setInt(2, planner.getUserId()); // 假设 EventPlanner 类有一个 getUserId 方法
                    psInsert.executeUpdate();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return updated;
    }


    public static int deleteEvent(Event event) {
        int deleted = 0;
        try (Connection con = DatabaseConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement("DELETE FROM event WHERE event_id=?");
            ps.setInt(1, event.getEventId());
            deleted = ps.executeUpdate();

            // 删除与该事件相关的所有 EventPlanner 关系
            PreparedStatement psDelete = con.prepareStatement("DELETE FROM event_planner_association WHERE association_event_id=?");
            psDelete.setInt(1, event.getEventId());
            psDelete.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return deleted;
    }
    public static int deleteAssoEvent(Event event) {
        int deleted = 0;
        try (Connection con = DatabaseConnection.getConnection()) {

            // 删除与该事件相关的所有 EventPlanner 关系
            PreparedStatement psDelete = con.prepareStatement("DELETE FROM event_planner_association WHERE association_planner_id=?");
            psDelete.setInt(1, event.getEventId());
            psDelete.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return deleted;
    }

    public List<Event> getAllEvent() {
        List<Event> events = new ArrayList<>();
        try (Connection con = DatabaseConnection.getConnection()){
            // ...（数据库连接代码）
            PreparedStatement ps = con.prepareStatement("SELECT * FROM event ");

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                Event event = new Event(
                        rs.getInt("event_id"),
                        rs.getString("event_artist_name"),
                        rs.getString("event_name"),
                        rs.getString("event_venue"),
                        LocalDateTime.parse(rs.getString("event_start_time"), formatter),
                        LocalDateTime.parse(rs.getString("event_end_time"), formatter),
                        rs.getInt("event_permission"),
                        UserMapper.findEventPlannersByEventId(rs.getInt("event_id"))
                );
                events.add(event);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return events;
    }
    public static boolean isTimeSlotAvailable(String eventVenue, LocalDateTime newStartTime, LocalDateTime newEndTime) {
        String sql = "SELECT COUNT(*) FROM event WHERE event_venue = ? " +
                "AND (event_start_time < ? AND event_end_time > ?) OR " +
                "(event_start_time < ? AND event_end_time > ?) OR " +
                "(event_start_time >= ? AND event_end_time <= ?)";

        try (Connection con = DatabaseConnection.getConnection()) {
            PreparedStatement statement = con.prepareStatement(sql);
            statement.setString(1, eventVenue);
            statement.setObject(2, newEndTime);
            statement.setObject(3, newStartTime);
            statement.setObject(4, newEndTime);
            statement.setObject(5, newStartTime);
            statement.setObject(6, newStartTime);
            statement.setObject(7, newEndTime);

            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                int count = rs.getInt(1);
                return count == 0;
            }
        } catch (SQLException e) {
            // Handle the exception
            e.printStackTrace();
        }
        return false;
    }


    public  int findEventByVenueName(String venueName) {
        int judge = 1; // 初始化为 null，如果找不到则返回 null
        String sql = "SELECT * FROM event WHERE event_venue = ?"; // 使用 WHERE 子句来查找特定名称的场地

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, venueName);  // 设置查询参数
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                judge=0;
            }

        } catch (Exception e) {
            e.printStackTrace();
            // 处理异常，可能需要更详细的处理或日志记录
        }
        return judge; // 返回找到的 Venue 对象，或者如果找不到则返回 null
    }

    public static List<Event> findEventsByPermission( ) {
        List<Event> events = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM event WHERE event_permission=?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, 1);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int eventId = rs.getInt("event_id");
                String eventArtistName = rs.getString("event_artist_name");
                String eventName = rs.getString("event_name");
                String eventVenue = rs.getString("event_venue");
                LocalDateTime eventStartTime = rs.getObject("event_start_time", LocalDateTime.class);
                LocalDateTime eventEndTime = rs.getObject("event_end_time", LocalDateTime.class);
                int eventPermission = rs.getInt("event_permission");

                // 获取与该事件相关的所有 EventPlanner
                List<EventPlanner> eventPlanners = UserMapper.findEventPlannersByEventId(eventId);

                Event event = new Event(
                        eventId,
                        eventArtistName,
                        eventName,
                        eventVenue,
                        eventStartTime,
                        eventEndTime,
                        eventPermission,
                        eventPlanners
                );
                events.add(event);
            }
        } catch (Exception e) {
            // Handle the exception...
            e.printStackTrace();
        }

        return events;
    }

    public static Event findEventById(int id) {
        Event event = null;
        try (Connection con = DatabaseConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM event WHERE event_id = ?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int eventId = rs.getInt("event_id");
                String eventArtistName = rs.getString("event_artist_name");
                String eventName = rs.getString("event_name");
                String eventVenue = rs.getString("event_venue");
                LocalDateTime eventStartTime = rs.getObject("event_start_time", LocalDateTime.class);
                LocalDateTime eventEndTime = rs.getObject("event_end_time", LocalDateTime.class);
                int eventPermission = rs.getInt("event_permission");
                int eventVersion = rs.getInt("version");
                // 获取与该事件相关的所有 EventPlanner
                List<EventPlanner> eventPlanners = UserMapper.findEventPlannersByEventId(eventId);

                event = new Event(
                        eventId,
                        eventArtistName,
                        eventName,
                        eventVenue,
                        eventStartTime,
                        eventEndTime,
                        eventPermission,
                        eventPlanners,
                        eventVersion
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return event;
    }

    public static boolean insertEventPlanner(int eventId, int plannerId) {
        try (Connection con = DatabaseConnection.getConnection()) {
            // 插入与 Event 关联的 EventPlanner
            String query = "INSERT INTO event_planner_association(association_event_id, association_planner_id) VALUES (?, ?)";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, eventId);
            ps.setInt(2, plannerId);
            int affectedRows = ps.executeUpdate();

            // 检查是否成功插入
            if (affectedRows > 0) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static List<Event> findEventsByPlanner(int plannerId) {
        List<Event> events = new ArrayList<>();
        try (Connection con = DatabaseConnection.getConnection()) {
            String query = "SELECT e.* " +
                    "FROM event e " +
                    "JOIN event_planner_association epa ON e.event_id = epa.association_event_id " +
                    "WHERE epa.association_planner_id = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, plannerId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int eventId = rs.getInt("event_id");
                String eventArtistName = rs.getString("event_artist_name");
                String eventName = rs.getString("event_name");
                String eventVenue = rs.getString("event_venue");
                LocalDateTime eventStartTime = rs.getObject("event_start_time", LocalDateTime.class);
                LocalDateTime eventEndTime = rs.getObject("event_end_time", LocalDateTime.class);
                int eventPermission = rs.getInt("event_permission");

                // 获取与该事件相关的所有 EventPlanner
                List<EventPlanner> eventPlanners = UserMapper.findEventPlannersByEventId(eventId);

                Event event = new Event(
                        eventId,
                        eventArtistName,
                        eventName,
                        eventVenue,
                        eventStartTime,
                        eventEndTime,
                        eventPermission,
                        eventPlanners
                );
                events.add(event);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return events;
    }

    public static Event findEventByName(String name) {
        Event event = null;
        try (Connection con = DatabaseConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM event WHERE event_name = ?");
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int eventId = rs.getInt("event_id");
                String eventArtistName = rs.getString("event_artist_name");
                String eventName = rs.getString("event_name");
                String eventVenue = rs.getString("event_venue");
                LocalDateTime eventStartTime = rs.getObject("event_start_time", LocalDateTime.class);
                LocalDateTime eventEndTime = rs.getObject("event_end_time", LocalDateTime.class);
                int eventPermission = rs.getInt("event_permission");

                // 获取与该事件相关的所有 EventPlanner
                List<EventPlanner> eventPlanners = UserMapper.findEventPlannersByEventId(eventId);

                event = new Event(
                        eventId,
                        eventArtistName,
                        eventName,
                        eventVenue,
                        eventStartTime,
                        eventEndTime,
                        eventPermission,
                        eventPlanners
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return event;
    }

    private static void throwConcurrencyException(Event event) throws Exception {

        try (Connection con = DatabaseConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement("SELECT version FROM event WHERE event_id = ?");
            ps.setInt(1, event.getEventId());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int version = rs.getInt("version");
                System.out.println(version + "  " + event.getEventVersion());
                if (version > event.getEventVersion()) {
                    System.out.println("con");
                    throw new ConcurrencyFailureException("busy system! please try again!");
                } else {
                    System.out.println("eee");
                    throw new Exception("Unexpected error occurred");
                }
            } else {
                throw new ConcurrencyFailureException("Event " + event.getEventName() + " has been deleted");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Database error occurred", e);
        }
    }
}

