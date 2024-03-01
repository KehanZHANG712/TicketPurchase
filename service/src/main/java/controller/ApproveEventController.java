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
@WebServlet(name = "ApproveEventController", value = {"/approveevent"})
public class ApproveEventController extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        setAccessControlHeaders(response);
        int power = Authentication.getUserPowerFromSession(request);
        //int power = 2;
        if(power != 2){
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            request.getSession().invalidate();
            response.getWriter().write("Do not have right!");
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
            Event updateEvent = EventMapper.findEventById(jsonObject.getInt("event_id"));
            String eventName = updateEvent.getEventName();
            ExclusiveReadLockManager.acquireLock(1, eventName);
            System.out.println("admin-lock: "+ExclusiveReadLockManager.hasLock(1, eventName) + " " + eventName);

            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            updateEvent.setEventPermission(1);
            UnitOfWork uow = UnitOfWork.getCurrent();
            if (uow == null) {
                uow = new UnitOfWork();
                UnitOfWork.setCurrent(uow);
            }
            uow.registerDirty(updateEvent);
            try {
                uow.commit();
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("Approve event successfully");
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("Failed to approve event");
            } finally {
                ExclusiveReadLockManager.releaseLock(1, eventName);
                System.out.println("admin-lock: " + ExclusiveReadLockManager.hasLock(1, eventName)+ " " + eventName);
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
