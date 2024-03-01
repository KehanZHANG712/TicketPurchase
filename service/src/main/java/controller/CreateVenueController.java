package controller;

import datasource.VenueMapper;
import domain.Venue;
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
@WebServlet(name = "CreateVenueController", value = {"/createVenue"})
public class CreateVenueController extends HttpServlet {

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


            String venueAddress = jsonObject.getString("venue_address");
            String venue_capacity = jsonObject.getString("venue_capacity");
            String mosh_capacity = jsonObject.getString("mosh_capacity");
            String standing_capacity = jsonObject.getString("standing_capacity");
            String seated_capacity = jsonObject.getString("seated_capacity");
            String vip_capacity = jsonObject.getString("vip_capacity");
            int venueCapacity=0;
            int moshCapacity=0;
            int standingCapacity=0;
            int seatedCapacity=0;
            int vipCapacity=0;
            if(venue_capacity!=null && mosh_capacity!=null && standing_capacity!=null && seated_capacity!=null && vip_capacity!=null){
                venueCapacity = Integer.parseInt(venue_capacity);
                moshCapacity = Integer.parseInt(mosh_capacity);
                standingCapacity = Integer.parseInt(standing_capacity);
                seatedCapacity = Integer.parseInt(seated_capacity);
                vipCapacity = Integer.parseInt(vip_capacity);
            }
            if(venueCapacity != moshCapacity+standingCapacity+seatedCapacity+vipCapacity){
                response.getWriter().write("{\"success\":false, \"message\":\"Capacity number error\"}");
            }else {

                Venue venue = new Venue(venueAddress, venueCapacity, moshCapacity, standingCapacity, seatedCapacity, vipCapacity);
                UnitOfWork uow = UnitOfWork.getCurrent();
                if (uow == null) {
                    uow = new UnitOfWork();
                    UnitOfWork.setCurrent(uow);
                }
                uow.registerNew(venue);
                try {
                    uow.commit();
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getWriter().write("Create venue successfully");
                } catch (Exception e) {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.getWriter().write("Failed to create venue");
                }

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
