package controller;

import datasource.EventMapper;
import datasource.TicketMapper;
import datasource.VenueMapper;
import domain.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import org.springframework.dao.ConcurrencyFailureException;
import util.Authentication;
import util.ExclusiveReadLockManager;
import util.LockingException;
import util.UnitOfWork;

import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@WebServlet(name = "EditEventController", value = {"/editevent"})
public class EditEventController extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        setAccessControlHeaders(response);
        int power = Authentication.getUserPowerFromSession(request);
        //int power=3;
        if(power != 3){
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            request.getSession().invalidate();
            response.getWriter().write("Do not have right");
        }
        else {
            StringBuilder sb = new StringBuilder();
            String line = null;
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            String jsonString = sb.toString();
            JSONObject jsonObject = new JSONObject(jsonString);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
            Event updateEvent = EventMapper.findEventById(jsonObject.getInt("event_id"));
            String originalName = updateEvent.getEventName();



            User currentUser = (User) request.getSession().getAttribute("currentUser");
            String planner_name = currentUser.getUserName();
            List<EventPlanner> planners = updateEvent.getEventPlanners();
            boolean containsPlannerName = planners.stream()
                    .anyMatch(planner -> planner_name.equals(planner.getUserName()));
            if(!containsPlannerName){
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("Do not have right");
                return;
            }
            if(updateEvent.getEventPermission()==1) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("The event had already been permitted!");
                return;
            }
            Ticket moshTic = TicketMapper.getTicket2(updateEvent.getEventName(), "mosh");
            Ticket standTic = TicketMapper.getTicket2(updateEvent.getEventName(), "standing");
            Ticket seatedTic = TicketMapper.getTicket2(updateEvent.getEventName(), "seated");
            Ticket vipTic = TicketMapper.getTicket2(updateEvent.getEventName(), "vip");
            Venue newVenue = VenueMapper.findVenueByName(jsonObject.getString("event_venue"));
            if(EventMapper.isTimeSlotAvailable(jsonObject.getString("event_venue"),
                    LocalDateTime.parse(jsonObject.getString("event_start_time"), formatter),
                    LocalDateTime.parse(jsonObject.getString("event_end_time"), formatter))){
                updateEvent.setEventName(jsonObject.getString("event_name"));
                updateEvent.setEventVenue(jsonObject.getString("event_venue"));
                updateEvent.setEventStartTime(LocalDateTime.parse(jsonObject.getString("event_start_time"), formatter));
                updateEvent.setEventEndTime(LocalDateTime.parse(jsonObject.getString("event_end_time"), formatter));
                moshTic.setTicketPrice(jsonObject.getInt("mosh_price"));
                moshTic.setTicketVenue(newVenue.getVenueAddress());
                moshTic.setTicketRest(newVenue.getMoshCapacity());
                standTic.setTicketPrice(jsonObject.getInt("standing_price"));
                standTic.setTicketVenue(newVenue.getVenueAddress());
                standTic.setTicketRest(newVenue.getStandingCapacity());
                seatedTic.setTicketPrice(jsonObject.getInt("seated_price"));
                seatedTic.setTicketVenue(newVenue.getVenueAddress());
                seatedTic.setTicketRest(newVenue.getSeatedCapacity());
                vipTic.setTicketPrice(jsonObject.getInt("vip_price"));
                vipTic.setTicketVenue(newVenue.getVenueAddress());
                vipTic.setTicketRest(newVenue.getVipCapacity());

                UnitOfWork uow = UnitOfWork.getCurrent();
                if (uow == null) {
                    uow = new UnitOfWork();
                    UnitOfWork.setCurrent(uow);
                }
                uow.registerDirty(updateEvent);
                uow.registerDirty(moshTic);
                uow.registerDirty(standTic);
                uow.registerDirty(seatedTic);
                uow.registerDirty(vipTic);
                try {
                    System.out.println("planner-lock: " + ExclusiveReadLockManager.hasLock(1, originalName) +" "+ originalName);
                    if(ExclusiveReadLockManager.hasLock(1, originalName)){
                        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        response.getWriter().write("Event is being approved or cancelled by the admin!");
                        return;
                    }
                    uow.commit();
                    System.out.println(planner_name + " edit successfully.");
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getWriter().write("Update event successfully");
                } catch (ConcurrencyFailureException e) {
                    System.out.println(planner_name + " edit failed.");
                    response.setStatus(HttpServletResponse.SC_CONFLICT); // 409 Conflict
                    response.getWriter().write("Concurrency Issue Occurred");
                }catch (Exception e) {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.getWriter().write("Failed to Update event");
                }
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("The time slot is not available!");
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
