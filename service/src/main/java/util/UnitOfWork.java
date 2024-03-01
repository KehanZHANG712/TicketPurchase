package util;

import datasource.*;
import domain.*;
import org.springframework.dao.ConcurrencyFailureException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UnitOfWork {
    private static final ThreadLocal<UnitOfWork> current = new ThreadLocal<>();
    private final List<Object> newObjects = new ArrayList<>();
    private final List<Object> dirtyObjects = new ArrayList<>();
    private final List<Object> deletedObjects = new ArrayList<>();

    public static void newCurrent() {
        setCurrent(new UnitOfWork());
    }

    public static void setCurrent(UnitOfWork uow) {
        current.set(uow);
    }

    public static UnitOfWork getCurrent() {
        return current.get();
    }

    public void registerNew(Object obj) {
        assert !dirtyObjects.contains(obj);
        assert !deletedObjects.contains(obj);
        newObjects.add(obj);
    }

    public void registerDirty(Object obj) {
        assert !deletedObjects.contains(obj);
        if (!dirtyObjects.contains(obj)) {
            dirtyObjects.add(obj);
        }
    }

    public void registerDeleted(Object obj) {
        if (newObjects.remove(obj)) return;
        dirtyObjects.remove(obj);
        if (!deletedObjects.contains(obj)) {
            deletedObjects.add(obj);
        }
    }

    public void commit() throws Exception {
        Connection con = null;
        try {
            con = DatabaseConnection.getConnection();
            con.setAutoCommit(false);



            // 处理脏对象（已更改但尚未保存的对象）
            for (Object obj : dirtyObjects) {
                if (obj instanceof User) {
                    UserMapper.updateUser((User) obj);
                } else if (obj instanceof Event) {
                    EventMapper.updateEvent((Event) obj);
                } else if (obj instanceof Ticket) {
                    TicketMapper.updateTicket((Ticket) obj);
                }else if (obj instanceof Venue) {
                    VenueMapper.updateVenue((Venue) obj);
                }
            }
            for (Object obj : newObjects) {
                if (obj instanceof User) {
                    UserMapper.insert((User) obj);
                } else if (obj instanceof Event) {
                    EventMapper.insertEvent((Event) obj);
                } else if (obj instanceof Ticket){
                    TicketMapper.insert((Ticket) obj);
                }else if (obj instanceof Venue) {
                    VenueMapper.insert((Venue) obj);
                }else if (obj instanceof Reservation) {
                    ReservationMapper.insert((Reservation) obj);
                }
            }

            // 处理已删除的对象
            for (Object obj : deletedObjects) {
                if (obj instanceof Event) {
                    if(((Event) obj).getEventPermission() == -1){
                        EventMapper.deleteAssoEvent((Event) obj);
                    } else {
                        EventMapper.deleteEvent((Event) obj);
                    }
                }
            }

            // 2. Delete Reservations
            for (Object obj : deletedObjects) {
                if (obj instanceof Reservation) {
                    if(((Reservation) obj).getReservationId() == -1) {
                        ReservationMapper.deleteReservationUserId((Reservation) obj);
                    } else {
                        ReservationMapper.deleteReservation((Reservation) obj);
                    }
                }
            }

            // 3. Delete other entities (like Venue)
            for (Object obj : deletedObjects) {
                if (obj instanceof Venue) {
                    VenueMapper.deleteVenue((Venue) obj);
                }
            }

            // 4. Finally, delete Users (since they are referenced by other entities)
            for (Object obj : deletedObjects) {
                if (obj instanceof User) {
                    UserMapper.deleteUser((User) obj);
                }
            }
            con.commit();
        } catch (ConcurrencyFailureException e) {
            if (con != null) {
                System.out.println("roll back");
                con.rollback();  // rollback transaction in case of concurrency conflict
            }
            // Handle the exception, possibly by informing the user
            throw e;
        } catch (SQLException e) {
            if (con != null) {
                System.out.println("roll back");
                con.rollback();  // rollback transaction in case of other database errors
            }
            throw e;
        } finally {
            newObjects.clear();
            dirtyObjects.clear();
            deletedObjects.clear();
            if (con != null) {
                con.setAutoCommit(true);
            }
        }
    }

}
