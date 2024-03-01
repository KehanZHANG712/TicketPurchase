package controller;

import domain.Ticket;
import domain.User;
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

@WebServlet(name = "UpdateUserController", value = {"/updateUser"})
public class UpdateUserController extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        setAccessControlHeaders(response);
        int power = Authentication.getUserPowerFromSession(request);
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

            int user_id = jsonObject.getInt("userId");
            String user_name = jsonObject.getString("userName");
            String user_password = jsonObject.getString("password");
            int user_power = jsonObject.getInt("power");
            String user_email = jsonObject.getString("email");


            User user = new User(user_id, user_name, user_password, user_power, user_email);


            UnitOfWork uow = UnitOfWork.getCurrent();
            if (uow == null) {
                uow = new UnitOfWork();
                UnitOfWork.setCurrent(uow);
            }
            uow.registerDirty(user);
            try {
                uow.commit();
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("Update user successfully");
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("Failed to Update user");
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
