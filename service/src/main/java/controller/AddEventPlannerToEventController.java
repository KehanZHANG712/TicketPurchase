package controller;

import datasource.EventMapper;
import datasource.UserMapper;
import domain.Event;
import domain.EventPlanner;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import util.Authentication;
import util.UnitOfWork;

import java.io.BufferedReader;
import java.io.IOException;

@WebServlet(name = "AddEventPlannerToEventController", value = {"/addplanner"})
public class AddEventPlannerToEventController extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        setAccessControlHeaders(response);
        int power = Authentication.getUserPowerFromSession(request);
        //System.out.println(power);
        if(power != 3){
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            request.getSession().invalidate();
            response.getWriter().write("Do not have right to create events");
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
            Event updateEvent = EventMapper.findEventByName(jsonObject.getString("event_name"));
            EventPlanner newPlanner = UserMapper.findEventPlannerByUserName(jsonObject.getString("event_planner_name"));
            updateEvent.getEventPlanners().add(newPlanner);
            UnitOfWork uow = UnitOfWork.getCurrent();
            if (uow == null) {
                uow = new UnitOfWork();
                UnitOfWork.setCurrent(uow);
            }
            uow.registerDirty(updateEvent);
            try {
                uow.commit();
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("Add event planner successfully");
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("Failed to add event planner");
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
