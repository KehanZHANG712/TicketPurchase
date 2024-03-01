package controller;

import datasource.ReservationMapper;
import datasource.TicketMapper;
import domain.Reservation;
import domain.Ticket;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import util.UnitOfWork;

import java.io.BufferedReader;
import java.io.IOException;

@WebServlet(name = "CancelReservationController", value = {"/cancelReservation"})
public class CancelReservationController extends HttpServlet {

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

        int reservationId = jsonObject.getInt("reservationId");

        int reservationTicketId=0;


        TicketMapper ticketMapper = new TicketMapper();
        ReservationMapper reservationMapper= new ReservationMapper();
        reservationTicketId = reservationMapper.getSelectReservation(reservationId).getReservationTicketId();
        int rest= ticketMapper.getRestTicket(reservationTicketId);
        System.out.println(rest);
        Ticket ticket=ticketMapper.getTicket(reservationTicketId);
        int reservationNumber= reservationMapper.getReservationNumber(reservationId);
        System.out.println(reservationNumber);
        rest=rest+reservationNumber;
        System.out.println(rest);
        ticket.setTicketRest(rest);
        Reservation reservation=reservationMapper.getSelectReservation(reservationId);
        UnitOfWork uow = UnitOfWork.getCurrent();
        if (uow == null) {
            uow = new UnitOfWork();
            UnitOfWork.setCurrent(uow);
        }
        uow.registerDirty(ticket);
        uow.registerDeleted(reservation);
        try {
            uow.commit();
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("cancel ticket successfully");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Failed to cancel ticket");
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
