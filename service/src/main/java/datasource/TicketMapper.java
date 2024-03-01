package datasource;

import domain.Event;
import domain.Ticket;
import domain.User;
import domain.Venue;
import org.springframework.dao.ConcurrencyFailureException;
import util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TicketMapper {

    public static int insert(Ticket ticket) {

        int inserted=0;
        try (Connection con = DatabaseConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement("INSERT INTO ticket(ticket_venue, ticket_event, ticket_type, ticket_price, ticket_rest, ticket_version) VALUES(?, ?, ?, ?, ?, ?)");
            ps.setString(1, ticket.getTicketVenue());
            ps.setString(2, ticket.getTicketEvent());
            ps.setString(3, ticket.getTicketType());
            ps.setInt(4, ticket.getTicketPrice());
            ps.setInt(5, ticket.getTicketRest());
            ps.setInt(6, 1);
            inserted = ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return inserted;

    }

    public List<Ticket> getTicketAll() {
        List<Ticket> tickets = new ArrayList<>();
        try (Connection con = DatabaseConnection.getConnection()){
            // ...（数据库连接代码）
            PreparedStatement ps = con.prepareStatement("SELECT * FROM ticket ");

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Ticket ticket = new Ticket(
                        rs.getInt("ticket_id"),
                        rs.getString("ticket_venue"),
                        rs.getString("ticket_event"),
                        rs.getString("ticket_type"),
                        rs.getInt("ticket_price"),
                        rs.getInt("ticket_rest")
                );
                tickets.add(ticket);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return tickets;
    }

    public int updatePrice(int ticketId,int ticketPrice) {

        int inserted=0;
        try (Connection con = DatabaseConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement("UPDATE ticket SET ticket_price=? WHERE ticket_id=?");

            ps.setInt(1, ticketPrice);
            ps.setInt(2, ticketId);
            inserted = ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return inserted;

    }
    public int getRestTicket(int ticketId) {
        int rest=0;
        try (Connection con = DatabaseConnection.getConnection()){
            // ...（数据库连接代码）
            PreparedStatement ps = con.prepareStatement("SELECT ticket_rest FROM ticket WHERE ticket_id=?");
            ps.setInt(1, ticketId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                rest=rs.getInt("ticket_rest");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return rest;
    }

    public void updateRest(int ticketId,int ticketRest) {

        int inserted=0;
        try (Connection con = DatabaseConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement("UPDATE ticket SET ticket_rest=? WHERE ticket_id=?");

            ps.setInt(1, ticketRest);
            ps.setInt(2, ticketId);
            inserted = ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public static Ticket getTicket(int ticketId) {

        Ticket ticket = null;
        try (Connection con = DatabaseConnection.getConnection()) {
            // ...（数据库连接代码）
            PreparedStatement ps = con.prepareStatement("SELECT * FROM ticket WHERE ticket_id=?");
            ps.setInt(1, ticketId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ticket = new Ticket(rs.getInt("ticket_id"),
                        rs.getString("ticket_venue"),
                        rs.getString("ticket_event"),
                        rs.getString("ticket_type"),
                        rs.getInt("ticket_price"),
                        rs.getInt("ticket_rest"),
                        rs.getInt("ticket_version"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return ticket;
    }

    public static void updateTicket(Ticket ticket) throws Exception {

        int inserted = 0;

        try (Connection con = DatabaseConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement("UPDATE ticket SET ticket_venue=?, ticket_event=?, ticket_type=?, ticket_price=?, ticket_rest=?, ticket_version=? WHERE ticket_id=? AND ticket_version=?");

            ps.setString(1, ticket.getTicketVenue());
            ps.setString(2, ticket.getTicketEvent());
            ps.setString(3, ticket.getTicketType());
            ps.setInt(4, ticket.getTicketPrice());
            ps.setInt(5, ticket.getTicketRest());
            ps.setInt(6, ticket.getTicketVersion()+1);
            ps.setInt(7, ticket.getTicketId());
            ps.setInt(8, ticket.getTicketVersion());

            inserted = ps.executeUpdate();


        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error updating ticket", e);
        }
        if (inserted > 0) {

        } else {
            throwConcurrencyException(ticket);

        }


    }

    public static Ticket getTicket2(String ticket_event, String ticket_type) {

        Ticket ticket = null;
        try (Connection con = DatabaseConnection.getConnection()) {

            // ...（数据库连接代码）
            PreparedStatement ps = con.prepareStatement("SELECT * FROM ticket WHERE ticket_event=? AND ticket_type=?");
            ps.setString(1, ticket_event);
            ps.setString(2, ticket_type);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ticket = new Ticket(rs.getInt("ticket_id"),
                        rs.getString("ticket_venue"),
                        rs.getString("ticket_event"),
                        rs.getString("ticket_type"),
                        rs.getInt("ticket_price"),
                        rs.getInt("ticket_rest"),
                        rs.getInt("ticket_version"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return ticket;
    }

    public List<Ticket> getTicketByEvent(String ticket_event) {
        List<Ticket> tickets = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConnection()) {
            // ...（数据库连接代码）
            PreparedStatement ps = con.prepareStatement("SELECT * FROM ticket WHERE ticket_event=? ");
            ps.setString(1, ticket_event);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Ticket ticket = new Ticket(
                        rs.getInt("ticket_id"),
                        rs.getString("ticket_venue"),
                        rs.getString("ticket_event"),
                        rs.getString("ticket_type"),
                        rs.getInt("ticket_price"),
                        rs.getInt("ticket_rest"),
                        rs.getInt("ticket_version"));
                tickets.add(ticket);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return tickets;
    }

    private static void throwConcurrencyException(Ticket ticket) throws Exception {

        try (Connection con = DatabaseConnection.getConnection()) {

            PreparedStatement ps = con.prepareStatement("SELECT ticket_version FROM ticket WHERE ticket_id = ?");
            ps.setInt(1, ticket.getTicketId());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int version = rs.getInt("ticket_version");
                System.out.println(version + "  " + ticket.getTicketVersion());
                if (version > ticket.getTicketVersion()) {

                    throw new ConcurrencyFailureException("busy system! please try again!");
                } else {

                    throw new Exception("Unexpected error occurred");
                }
            } else {
                throw new ConcurrencyFailureException("Ticket " + ticket.getTicketId() + " has been deleted");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new Exception("Database error occurred", e);
        }

    }

}
