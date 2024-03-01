package controller;

import datasource.EventMapper;
import datasource.VenueMapper;
import domain.User;
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

@WebServlet(name = "UpdateVenueController", value = {"/updateVenue"})
public class UpdateVenueController extends HttpServlet {

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

            int venueId = jsonObject.getInt("venueId");
            String venueAddress = jsonObject.getString("venueAddress");
            int venueCapacity = jsonObject.getInt("venueCapacity");
            int moshCapacity = jsonObject.getInt("moshCapacity");
            int standingCapacity = jsonObject.getInt("standingCapacity");
            int seatedCapacity = jsonObject.getInt("seatedCapacity");
            int vipCapacity = jsonObject.getInt("vipCapacity");
            EventMapper venueMapper= new EventMapper();
            Venue venue = new Venue(venueId, venueAddress, venueCapacity, moshCapacity, standingCapacity,seatedCapacity,vipCapacity);
            Venue venue1 = VenueMapper.findVenueById(venueId);
            int judge= venueMapper.findEventByVenueName(venue1.getVenueAddress());
            System.out.println(judge);
            if(judge==0) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("Already exist event");
            }else if(vipCapacity+seatedCapacity+standingCapacity+moshCapacity!=venueCapacity) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("capacity number fault");
            }else{
                UnitOfWork uow = UnitOfWork.getCurrent();
                if (uow == null) {
                    uow = new UnitOfWork();
                    UnitOfWork.setCurrent(uow);
                }
                uow.registerDirty(venue);
                try {
                    uow.commit();
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getWriter().write("Update venue successfully");
                } catch (Exception e) {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.getWriter().write("Failed to update venue");
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
