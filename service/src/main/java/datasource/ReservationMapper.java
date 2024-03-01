package datasource;

import domain.Reservation;
import domain.Ticket;
import domain.User;
import util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ReservationMapper {

    public static int insert(Reservation reservation) throws Exception {

        int inserted=0;
        try (Connection con = DatabaseConnection.getConnection()) {
            PreparedStatement ps = con.prepareStatement("INSERT INTO reservation(reservation_ticket_id, reservation_user_id, reservation_number, reservation_price) VALUES(?, ?, ?, ?)");
            ps.setInt(1, reservation.getReservationTicketId());
            ps.setInt(2, reservation.getReservationUserId());
            ps.setInt(3, reservation.getReservationNumber());
            ps.setInt(4, reservation.getReservationPrice());
            inserted = ps.executeUpdate();

        } catch (Exception e) {
            throw new Exception(e);
        }
        return inserted;

    }
    public List<Reservation> getAllReservation() {
        List<Reservation> reservations = new ArrayList<>();
        try (Connection con = DatabaseConnection.getConnection()){
            // ...（数据库连接代码）
            PreparedStatement ps = con.prepareStatement("SELECT * FROM reservation ");

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Reservation reservation = new Reservation(
                        rs.getInt("reservation_id"),
                        rs.getInt("reservation_ticket_id"),
                        rs.getInt("reservation_user_id"),
                        rs.getInt("reservation_number"),
                        rs.getInt("reservation_price")
                );
                reservations.add(reservation);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return reservations;
    }

    public int getReservationNumber(int ReservationId) {
        int reservationNumber = 0;
        try (Connection con = DatabaseConnection.getConnection()){
            // ...（数据库连接代码）
            PreparedStatement ps = con.prepareStatement("SELECT reservation_number FROM reservation WHERE reservation_id=?");
            ps.setInt(1, ReservationId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                reservationNumber=rs.getInt("reservation_number");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return reservationNumber;
    }

    public static int deleteReservation(Reservation reservation) {
        int deleted = 0;
        try (Connection con = DatabaseConnection.getConnection()){
            PreparedStatement ps = con.prepareStatement("DELETE FROM reservation WHERE reservation_id=?");
            ps.setInt(1, reservation.getReservationId());
            deleted = ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return deleted;
    }
    public static int deleteReservationUserId(Reservation reservation) {
        int deleted = 0;
        try (Connection con = DatabaseConnection.getConnection()){
            PreparedStatement ps = con.prepareStatement("DELETE FROM reservation WHERE reservation_user_id=?");
            ps.setInt(1, reservation.getReservationUserId());
            deleted = ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return deleted;
    }

    public Reservation getSelectReservation(int reservation_id) {
        Reservation reservation = null;
        try (Connection con = DatabaseConnection.getConnection()) {
            // ...（数据库连接代码）
            PreparedStatement ps = con.prepareStatement("SELECT * FROM reservation where reservation_id=?");
            ps.setInt(1, reservation_id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                reservation = new Reservation(
                        rs.getInt("reservation_id"),
                        rs.getInt("reservation_ticket_id"),
                        rs.getInt("reservation_user_id"),
                        rs.getInt("reservation_number"),
                        rs.getInt("reservation_price")
                );

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return reservation;
    }

    public List<Reservation> getSelectUserReservation(int userID) {
        List<Reservation> reservations = new ArrayList<>();
        try (Connection con = DatabaseConnection.getConnection()){
            // ...（数据库连接代码）
            PreparedStatement ps = con.prepareStatement("SELECT * FROM reservation WHERE reservation_user_id=?");
            ps.setInt(1, userID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Reservation reservation = new Reservation(
                        rs.getInt("reservation_id"),
                        rs.getInt("reservation_ticket_id"),
                        rs.getInt("reservation_user_id"),
                        rs.getInt("reservation_number"),
                        rs.getInt("reservation_price")
                );
                reservations.add(reservation);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return reservations;
    }

}
