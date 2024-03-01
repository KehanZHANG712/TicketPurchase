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
import org.json.JSONObject;
import util.UnitOfWork;

import java.io.BufferedReader;
import java.io.IOException;
@WebServlet(name = "SetTicketController", value = {"/setTicket"})
public class SetTicketController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        setAccessControlHeaders(response);

        // 从前端获取JSON数据
        StringBuilder sb = new StringBuilder();
        String line = null;
        BufferedReader reader = request.getReader();
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        String jsonString = sb.toString();
        JSONObject jsonObject = new JSONObject(jsonString);

        String ticketVenue = jsonObject.getString("ticket_venue");
        String ticketEvent = jsonObject.getString("ticket_event");
        String ticketType =jsonObject.getString("ticket_type");
        String ticket_price = jsonObject.getString("ticket_price");
        String ticket_rest = jsonObject.getString("ticket_rest");
        int ticketRest=0;
        int ticketPrice=0;
        if(ticket_rest!=null && ticket_price!=null){
            ticketRest = Integer.parseInt(ticket_rest);
            ticketPrice = Integer.parseInt(ticket_price);
        }

        Ticket ticket=new Ticket( ticketVenue,  ticketEvent,  ticketType,  ticketPrice,  ticketRest);

        UnitOfWork uow = UnitOfWork.getCurrent();
        if (uow == null) {
            uow = new UnitOfWork();
            UnitOfWork.setCurrent(uow);
        }
        uow.registerNew(ticket);
        try {
            uow.commit();
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("Create ticket successfully");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Failed to create ticket");
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
