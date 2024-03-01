package controller;

import datasource.EventMapper;
import domain.Event;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import util.Authentication;
import util.ExclusiveReadLockManager;
import util.UnitOfWork;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
@WebServlet(name = "DeleteEventController", value = {"/deleteevent"})
public class DeleteEventController extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        setAccessControlHeaders(response);
        int power = Authentication.getUserPowerFromSession(request);
        if(power != 2 && power != 3){
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            request.getSession().invalidate();
            response.getWriter().write("Do not have right!");
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
            Event deleteEvent = EventMapper.findEventById(jsonObject.getInt("event_id"));
            String eventName = deleteEvent.getEventName();
            ExclusiveReadLockManager.acquireLock(1, eventName);
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            UnitOfWork uow = UnitOfWork.getCurrent();
            if (uow == null) {
                uow = new UnitOfWork();
                UnitOfWork.setCurrent(uow);
            }
            uow.registerDeleted(deleteEvent);
            try {
                uow.commit();
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("Delete event successfully");
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("Failed to delete event");
            } finally {
                ExclusiveReadLockManager.releaseLock(1, eventName);
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
