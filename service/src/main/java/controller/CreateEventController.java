package controller;
import datasource.UserMapper;
import datasource.VenueMapper;
import domain.Event;
import datasource.EventMapper;
import domain.EventPlanner;
import domain.Ticket;
import domain.Venue;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.format.DateTimeFormatter;

import util.Authentication;
import util.UnitOfWork;

import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@WebServlet(name = "CreateEventController", value = {"/createEvent"})
public class CreateEventController extends HttpServlet {
    private static final ReentrantReadWriteLock eventLock = new ReentrantReadWriteLock();
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        setAccessControlHeaders(resp);
        int power = Authentication.getUserPowerFromSession(req);
        if(power != 3){
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            req.getSession().invalidate();
            resp.getWriter().write("Do not have right to create events");
        }
        else {
            // 获取或创建当前线程的UnitOfWork实例
            UnitOfWork uow = UnitOfWork.getCurrent();
            if (uow == null) {
                uow = new UnitOfWork();
                UnitOfWork.setCurrent(uow);
            }

            // 从前端获取JSON数据
            StringBuilder sb = new StringBuilder();
            String line = null;
            BufferedReader reader = req.getReader();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            String jsonString = sb.toString();
            JSONObject jsonObject = new JSONObject(jsonString);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

            eventLock.readLock().lock();
            try{
                if(EventMapper.isTimeSlotAvailable(jsonObject.getString("event_venue"),
                        LocalDateTime.parse(jsonObject.getString("event_start_time"), formatter),
                        LocalDateTime.parse(jsonObject.getString("event_end_time"), formatter))){
                    eventLock.readLock().unlock();
                    eventLock.writeLock().lock();
                    try{
                        if(EventMapper.isTimeSlotAvailable(jsonObject.getString("event_venue"),
                                LocalDateTime.parse(jsonObject.getString("event_start_time"), formatter),
                                LocalDateTime.parse(jsonObject.getString("event_end_time"), formatter))){
                            Event event = new Event(
                                    jsonObject.getString("event_artist_name"),
                                    jsonObject.getString("event_name"),
                                    jsonObject.getString("event_venue"),
                                    LocalDateTime.parse(jsonObject.getString("event_start_time"), formatter),
                                    LocalDateTime.parse(jsonObject.getString("event_end_time"), formatter),
                                    jsonObject.getInt("event_permission"),
                                    new ArrayList<>()
                            );

                            Venue venue = VenueMapper.findVenueByName(jsonObject.getString("event_venue"));

                            Ticket mosh_ticket = new Ticket(
                                    jsonObject.getString("event_venue"),
                                    jsonObject.getString("event_name"),
                                    "mosh",
                                    jsonObject.getInt("moshPrice"),
                                    venue.getMoshCapacity()
                            );
                            Ticket standing_ticket = new Ticket(
                                    jsonObject.getString("event_venue"),
                                    jsonObject.getString("event_name"),
                                    "standing",
                                    jsonObject.getInt("standingPrice"),
                                    venue.getStandingCapacity()
                            );
                            Ticket seated_ticket = new Ticket(
                                    jsonObject.getString("event_venue"),
                                    jsonObject.getString("event_name"),
                                    "seated",
                                    jsonObject.getInt("seatedPrice"),
                                    venue.getSeatedCapacity()
                            );
                            Ticket vip_ticket = new Ticket(
                                    jsonObject.getString("event_venue"),
                                    jsonObject.getString("event_name"),
                                    "vip",
                                    jsonObject.getInt("vipPrice"),
                                    venue.getVipCapacity()
                            );
                            // 其他字段...

                            // 获取当前登录的EventPlanner的ID
                            EventPlanner loggedInEventPlanner = UserMapper.findEventPlannerByUserName(event.getEventArtistName());

                            // 将当前登录的EventPlanner添加到事件的EventPlanners列表中
                            List<EventPlanner> eventPlanners = event.getEventPlanners();
                            eventPlanners.add(loggedInEventPlanner);
                            event.setEventPlanners(eventPlanners);

                            // 注册为新对象
                            uow.registerNew(event);
                            uow.registerNew(mosh_ticket);
                            uow.registerNew(standing_ticket);
                            uow.registerNew(seated_ticket);
                            uow.registerNew(vip_ticket);

                            try {
                                uow.commit();
                                resp.setStatus(HttpServletResponse.SC_OK);
                                System.out.println("OK");
                                resp.getWriter().write("Event created successfully");
                                resp.setStatus(HttpServletResponse.SC_OK);
                            } catch (SQLException e) {
                                System.out.println("SQL Exception");
                                e.printStackTrace();
                                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                                resp.getWriter().write("already have its name");
                            } catch (Exception e) {
                                System.out.println("Exception");
                                e.printStackTrace();
                                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                                resp.getWriter().write("Failed to create event");
                            }
                        } else {
                            resp.setStatus(HttpServletResponse.SC_CONFLICT);
                            System.out.println("1");
                            resp.getWriter().write("Concurrency issue happens! The time slot is not available!");
                        }
                    }finally {
                        eventLock.writeLock().unlock();
                    }
                }
                else {
                    resp.setStatus(HttpServletResponse.SC_CONFLICT);
                    System.out.println("2");
                    resp.getWriter().write("The time slot is not available!");
                }
            }finally {
                eventLock.readLock().unlock();
            }
        }
    }
    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        setAccessControlHeaders(resp);
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    private void setAccessControlHeaders(HttpServletResponse response) {
        response.addHeader("Access-Control-Allow-Origin", "https://trywz.onrender.com");
        response.addHeader("Access-Control-Allow-Methods", "POST, GET, PUT, OPTIONS, DELETE");
        response.addHeader("Access-Control-Allow-Headers", "Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");
        response.addHeader("Access-Control-Allow-Credentials", "true");
    }
}


