package controller;

import domain.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.dao.ConcurrencyFailureException;
import util.Authentication;
import util.UnitOfWork;

import org.json.JSONObject;


import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet(name = "UserRegisterController", value = {"/register"})
public class UserRegisterController extends HttpServlet {
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

        String userEmail = jsonObject.getString("user_email");
        String userPassword =jsonObject.getString("user_password");
        System.out.println(userPassword);
        String hashPassword = Authentication.hashPassword(userPassword);
        System.out.println(hashPassword);
        String userName = jsonObject.getString("user_name");
        int userPower = jsonObject.getInt("user_power");

        User user = new User(userName,hashPassword,userPower,userEmail);
        System.out.println(userName);
        UnitOfWork uow = UnitOfWork.getCurrent();
        if (uow == null) {
            uow = new UnitOfWork();
            UnitOfWork.setCurrent(uow);
        }
        uow.registerNew(user);


        try {
            uow.commit();
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("Register successfully");
        } catch (ConcurrencyFailureException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Username already exists");
        } catch (Exception e) {
            if (e instanceof SQLException) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("SQL problems occur");
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("Failed to register");
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
