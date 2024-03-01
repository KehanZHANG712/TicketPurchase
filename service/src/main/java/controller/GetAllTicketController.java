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

import java.io.IOException;
import java.util.List;

@WebServlet(name = "GetAllTicketController", value = {"/getAllTicket"})
public class GetAllTicketController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        setAccessControlHeaders(response);

        TicketMapper ticketMapper = new TicketMapper();
        List<Ticket> tickets= ticketMapper.getTicketAll();
        JSONArray json = JSONArray.fromObject(tickets);
        String json_str = json.toString();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write( json_str );


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
