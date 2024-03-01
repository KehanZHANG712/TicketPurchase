package controller;

import datasource.TicketMapper;
import datasource.UserMapper;
import domain.Ticket;
import domain.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.sf.json.JSONArray;
import util.Authentication;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "GetAllUserController", value = {"/getAllUser"})
public class GetAllUserController extends HttpServlet {

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
            UserMapper userMapper = new UserMapper();
            List<User> users= userMapper.getAllUser();
            JSONArray json = JSONArray.fromObject(users);
            String json_str = json.toString();

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(json_str);
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
