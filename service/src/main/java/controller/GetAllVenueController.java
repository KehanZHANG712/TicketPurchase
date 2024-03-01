package controller;

import com.google.gson.Gson;
import datasource.UserMapper;
import datasource.VenueMapper;
import domain.User;
import domain.Venue;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.sf.json.JSONArray;
import util.Authentication;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
@WebServlet(name = "GetAllVenueController", value = {"/getAllVenue"})
public class GetAllVenueController extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        setAccessControlHeaders(response);
        int power = Authentication.getUserPowerFromSession(request);
        if(power != 2){
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            request.getSession().invalidate();
            response.getWriter().write("Do not have right!");
        }
        else {
            // 提取所有的场地名称

            List<Venue> venues = VenueMapper.getAllVenues();
            JSONArray json = JSONArray.fromObject(venues);
            String json_str = json.toString();

            // 设置响应类型并返回JSON
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
