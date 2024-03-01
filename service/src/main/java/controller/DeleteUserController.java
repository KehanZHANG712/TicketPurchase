package controller;

import domain.Event;
import domain.Reservation;
import domain.User;
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

@WebServlet(name = "DeleteUserController", value = {"/deleteUser"})
public class DeleteUserController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        setAccessControlHeaders(response);
        int power = Authentication.getUserPowerFromSession(request);
        //int power=2;
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
            String user_name = jsonObject.getString("userName");
            ExclusiveReadLockManager.acquireLock(1,user_name);
            System.out.println("lock");

            int user_id = jsonObject.getInt("userId");
            String user_password = jsonObject.getString("password");
            int user_power = jsonObject.getInt("power");
            String user_email = jsonObject.getString("email");


            User user = new User(user_id, user_name, user_password, user_power, user_email);
            UnitOfWork uow = UnitOfWork.getCurrent();
            if (uow == null) {
                uow = new UnitOfWork();
                UnitOfWork.setCurrent(uow);
            }

            if(user_power==1){
                Reservation reservation=new Reservation(-1,user_id);
                uow.registerDeleted(reservation);
            }else if(user_power==3) {
                Event event = new Event(user_id, -1);
                uow.registerDeleted(event);
            }

            uow.registerDeleted(user);
            try {
                uow.commit();
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("Delete user successfully");

            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("Failed to delete user");

            }finally {

                ExclusiveReadLockManager.releaseLock(1,user_name);
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
