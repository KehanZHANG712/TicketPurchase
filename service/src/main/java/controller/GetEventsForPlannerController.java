package controller;

import datasource.EventMapper;
import datasource.TicketMapper;
import datasource.UserMapper;
import domain.Event;
import domain.EventPlanner;
import domain.Ticket;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import util.Authentication;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "GetEventsForPlannerController", value = {"/eventsbyplanner"})
public class GetEventsForPlannerController extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        setAccessControlHeaders(response);
        int power = Authentication.getUserPowerFromSession(request);
        if(power != 3){
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            request.getSession().invalidate();
            response.getWriter().write("Do not have right");
        }
        else {
            // 从前端获取JSON数据
            StringBuilder sb = new StringBuilder();
            String line = null;
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            String jsonString = sb.toString();
            JSONObject jsonObject = new JSONObject(jsonString);
            String plannerName = jsonObject.getString("event_planner_name");
            EventPlanner curPlanner = UserMapper.findEventPlannerByUserName(plannerName);
            List<Event> events = EventMapper.findEventsByPlanner(curPlanner.getUserId());

            JSONArray jsonArray = new JSONArray();
            for (Event event : events) {
                JSONObject newJsonObject = new JSONObject();
                Ticket moshTic = TicketMapper.getTicket2(event.getEventName(), "mosh");
                Ticket standTic = TicketMapper.getTicket2(event.getEventName(), "standing");
                Ticket seatedTic = TicketMapper.getTicket2(event.getEventName(), "seated");
                Ticket vipTic = TicketMapper.getTicket2(event.getEventName(), "vip");
                newJsonObject.put("event_id", event.getEventId());
                newJsonObject.put("event_name", event.getEventName());
                newJsonObject.put("event_artist_name", event.getEventArtistName());
                newJsonObject.put("event_start_time", event.getEventStartTime());
                newJsonObject.put("event_end_time", event.getEventEndTime());
                newJsonObject.put("event_venue", event.getEventVenue());
                newJsonObject.put("mosh_price", moshTic.getTicketPrice());
                newJsonObject.put("standing_price", standTic.getTicketPrice());
                newJsonObject.put("seated_price", seatedTic.getTicketPrice());
                newJsonObject.put("vip_price", vipTic.getTicketPrice());
                List<EventPlanner> planners = event.getEventPlanners();
                JSONArray plannersArray = new JSONArray();
                for (EventPlanner planner : planners) {
                    JSONObject plannerObject = new JSONObject();
                    plannerObject.put("event_planner_id", planner.getUserId());
                    plannerObject.put("event_planner_name", planner.getUserName());
                    plannersArray.put(plannerObject);
                }
                newJsonObject.put("event_planners", plannersArray);
                jsonArray.put(newJsonObject);
            }
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(jsonArray.toString());
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
