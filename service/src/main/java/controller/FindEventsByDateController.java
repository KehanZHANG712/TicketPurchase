package controller;
import domain.Event;
import com.google.gson.Gson;
import datasource.EventMapper;
import domain.EventPlanner;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
@WebServlet(name = "FindEventsByDateController", value = {"/findEventsByDate"})
public class FindEventsByDateController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        setAccessControlHeaders(resp);
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = req.getReader();
        String line;

        while ((line = reader.readLine()) != null) {
            sb.append(line).append('\n');
        }

        String jsonString = sb.toString();
        JSONObject json = new JSONObject(jsonString);
        // 解析 JSON 格式的搜索词
        String searchDate = json.getString("search_date");

        // 转换为 LocalDate 对象
        LocalDate date = LocalDate.parse(searchDate);

        // 调用 EventMapper 的 findEventsByDate 方法
        //EventMapper eventMapper = new EventMapper();
        List<Event> events = EventMapper.findEventsByDate(date);

        // 转换为 JSON 格式并返回给前端
        JSONArray jsonArray = new JSONArray();
        for (Event event : events) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("event_id", event.getEventId());
            jsonObject.put("event_name", event.getEventName());
            jsonObject.put("event_artist_name", event.getEventArtistName());
            jsonObject.put("event_start_time", event.getEventStartTime());
            jsonObject.put("event_end_time", event.getEventEndTime());
            jsonObject.put("event_venue", event.getEventVenue());
            List<EventPlanner> planners = event.getEventPlanners();
            JSONArray plannersArray = new JSONArray();
            for (EventPlanner planner : planners) {
                JSONObject plannerObject = new JSONObject();
                plannerObject.put("event_planner_id", planner.getUserId());
                plannerObject.put("event_planner_name", planner.getUserName());
                plannersArray.put(plannerObject);
            }
            jsonObject.put("event_planners", plannersArray);
            jsonArray.put(jsonObject);
        }

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(jsonArray.toString());
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

