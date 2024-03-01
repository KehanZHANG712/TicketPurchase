package controller;

import datasource.ReservationMapper;
import domain.Reservation;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.sf.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "GetSelectedReservationController", value = {"/getSelectReservation"})
public class GetSelectedReservationController extends HttpServlet {

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
        String userId = json.getString("userId");
        int user_id=0;
        if(userId!=null){
            user_id=Integer.parseInt(userId);
        }
        ReservationMapper reservationMapper=new ReservationMapper();
        List<Reservation> reservationList=reservationMapper.getSelectUserReservation(user_id);
        JSONArray json1 = JSONArray.fromObject(reservationList);
        String json_str = json1.toString();

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(json_str);
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
