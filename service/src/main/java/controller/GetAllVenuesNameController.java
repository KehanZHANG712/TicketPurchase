package controller;
import com.google.gson.Gson;
import datasource.VenueMapper;
import domain.Venue;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
@WebServlet(name = "GetAllVenuesNameController", value = {"/getallvenuesname"})
public class GetAllVenuesNameController extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        setAccessControlHeaders(response);
        List<Venue> allVenues = VenueMapper.getAllVenues();

        // 提取所有的场地名称
        List<String> venueNames = new ArrayList<>();
        for (Venue venue : allVenues) {
            venueNames.add(venue.getVenueAddress());  // 或者其他你想提供的名称字段
        }

        // 转换为JSON
        Gson gson = new Gson();
        String json = gson.toJson(venueNames);

        // 设置响应类型并返回JSON
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json);
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

