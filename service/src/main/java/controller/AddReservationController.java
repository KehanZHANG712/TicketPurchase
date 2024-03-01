package controller;

import datasource.TicketMapper;
import domain.Reservation;
import domain.Ticket;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import org.springframework.dao.ConcurrencyFailureException;
import util.ExclusiveReadLockManager;
import util.UnitOfWork;

import java.io.BufferedReader;
import java.io.IOException;

import static java.lang.Thread.sleep;

@WebServlet(name = "AddReservationController", value = {"/addReservation"})
public class AddReservationController extends HttpServlet {


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

        String ticket_type = jsonObject.getString("ticket_type");
        String ticket_event = jsonObject.getString("ticket_event");
        String reservation_user_id =jsonObject.getString("reservation_user_id");
        String reservation_number = jsonObject.getString("reservation_number");
        String reservation_price = jsonObject.getString("reservation_price");
        String user_name = jsonObject.getString("user_name");
        System.out.println(user_name);
        int reservationUserId=0;
        int reservationNumber=0;
        int reservationPrice=0;
        if( reservation_user_id!=null && reservation_number!=null && reservation_price!=null){
            reservationUserId = Integer.parseInt(reservation_user_id);
            reservationNumber = Integer.parseInt(reservation_number);
            reservationPrice = Integer.parseInt(reservation_price);
        }


            TicketMapper ticketMapper = new TicketMapper();
            int judge = 0;
            for (int x = 0; x < 3; x++) {
                Ticket ticket = ticketMapper.getTicket2(ticket_event, ticket_type);
                int ticket_id = ticket.getTicketId();
                int rest = ticket.getTicketRest();
                if(rest - reservationNumber < 0) {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.getWriter().write("{there is not enough ticket, \"only left\":\"" + rest + "\"}");
                    return;
                } else {
                    rest = rest - reservationNumber;
                    System.out.println(rest);
                    Reservation reservation = new Reservation(ticket_id, reservationUserId, reservationNumber, reservationPrice);
                    ticket.setTicketRest(rest);
                    UnitOfWork uow = UnitOfWork.getCurrent();
                    if (uow == null) {
                        uow = new UnitOfWork();
                        UnitOfWork.setCurrent(uow);
                    }
                    UnitOfWork uow2 = UnitOfWork.getCurrent();
                    if (uow2 == null) {
                        uow2 = new UnitOfWork();
                        UnitOfWork.setCurrent(uow2);
                    }
                    uow.registerDirty(ticket);
                    uow.registerNew(reservation);
                    judge = 0;
                    try {
                        sleep(100);
                        if(ExclusiveReadLockManager.hasLock(1,user_name)){
                            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                            response.getWriter().write("user information changed!");
                            return;
                        }
                        System.out.println("no check");
                        uow.commit();
                        response.setStatus(HttpServletResponse.SC_OK);
                        response.getWriter().write("purchase ticket successfully");
                        return;
                    } catch (ConcurrencyFailureException e) {
                        judge = 2;
                    }  catch (Exception e) {
                        e.printStackTrace();
                        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        response.getWriter().write("Failed to purchase ticket");
                        judge = 1;
                        return;
                    }
                    System.out.println("judge" + judge);

                }
            }
            if (judge == 2) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("busy system! please try again!");
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
