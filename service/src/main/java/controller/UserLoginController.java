package controller;

import datasource.UserMapper;
import domain.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.json.JSONObject;
import util.Authentication;
import util.ExclusiveReadLockManager;

@WebServlet(name = "UserLoginController", value = {"/login"})
public class UserLoginController extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        setAccessControlHeaders(response);

        BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
        String line;
        StringBuilder builder = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }
        String jsonString = builder.toString();
        JSONObject json = new JSONObject(jsonString);
        String userPassword = json.getString("user_password");
        String userName = json.getString("user_name");

        User currentUser = UserMapper.FindUserByName(userName);
        JSONObject jsonResponse = new JSONObject();
        System.out.println(userName);
        if(currentUser == null){
            jsonResponse.put("success", false);
            jsonResponse.put("message", "No such user!");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("No such user!");
        } else if (!Authentication.checkPasswordMatchesHash(userPassword, currentUser.getHashPassword())) {
            jsonResponse.put("success", false);
           jsonResponse.put("message", "Incorrect password!");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Incorrect password!");
        }
        else {
            int judge = currentUser.getPower();
            if (judge > 0) {
                int userId = currentUser.getUserId();
                int userPower = currentUser.getPower();
                String actualUserName = currentUser.getUserName();
                request.getSession().setAttribute("currentUser", currentUser);
                jsonResponse.put("success", true);
                jsonResponse.put("user_power", userPower);
                jsonResponse.put("user_name", actualUserName);
                jsonResponse.put("user_id", userId);

                if(ExclusiveReadLockManager.hasLock(1,userName)){
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    response.getWriter().write("user information changed!");
                    return;
                }
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(jsonResponse.toString());
            } else {
                response.getWriter().write("{\"success\":false, \"message\":\"Invalid credentials\"}");
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
