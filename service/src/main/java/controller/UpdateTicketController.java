package controller;

import datasource.TicketMapper;
import domain.Ticket;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import util.UnitOfWork;

import org.json.JSONObject;


import java.io.BufferedReader;
import java.io.IOException;
@WebServlet(name = "UpdatePriceController", value = {"/updateTicket"})
public class UpdateTicketController extends HttpServlet {
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

        String ticket_id = jsonObject.getString("ticket_id");
        String ticket_price = jsonObject.getString("ticket_price");
        String ticket_venue = jsonObject.getString("ticket_venue");
        String ticket_event = jsonObject.getString("ticket_event");
        String ticket_type = jsonObject.getString("ticket_type");
        String ticket_rest = jsonObject.getString("ticket_rest");
        int ticketId=0;
        int ticketPrice=0;
        int ticketRest =0;
        if(ticket_id!=null && ticket_price!=null && ticket_rest!=null){
            ticketId = Integer.parseInt(ticket_id);
            ticketPrice = Integer.parseInt(ticket_price);
            ticketRest = Integer.parseInt(ticket_rest);
        }
        System.out.println(ticketId+ticket_venue+ticket_event+ticket_type+ticketPrice+ticketRest);
        Ticket ticket=new Ticket( ticketId,  ticket_venue,  ticket_event,  ticket_type,  ticketPrice,  ticketRest);

        UnitOfWork uow = UnitOfWork.getCurrent();
        if (uow == null) {
            uow = new UnitOfWork();
            UnitOfWork.setCurrent(uow);
        }
        uow.registerDirty(ticket);
        try {
            uow.commit();
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("Update ticket successfully");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Failed to Update ticket");
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
