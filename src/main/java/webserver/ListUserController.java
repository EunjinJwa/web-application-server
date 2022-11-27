package webserver;

import db.DataBase;
import model.HttpRequest;
import model.HttpResponse;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;

public class ListUserController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);
    @Override
    public void doPost(HttpRequest request, HttpResponse response) throws IOException {

    }

    @Override
    public void doGet(HttpRequest request, HttpResponse response) throws Exception {
        if (!isLogined(request)) {
            log.info("로그인 페이지로 이동");
            response.sendRedirect("/user/login.html");
            return;
        }
        log.info("로그인 상태");
        Collection<User> allUsers = DataBase.findAll();
        response.setBody(genUserListHtmlBody(allUsers).getBytes());
        response.forward();
    }

    private String genUserListHtmlBody(Collection<User> users) {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html>\n" +
                "<html lang=\"kr\">\n");
        sb.append("<body>");
        sb.append("<table border=1>");

        for (User user : users) {
            sb.append("<tr>");
            sb.append("<td>" + user.getUserId() + "</td>\n");
            sb.append("<td>" + user.getName() + "</td>\n");
            sb.append("<td>" + user.getEmail() + "</td>\n");
            sb.append("</tr>");
        }
        sb.append("</table>");

        sb.append("</body>");
        sb.append("</html>");
        return sb.toString();
    }
    public boolean isLogined(HttpRequest httpRequest) {
        return Boolean.parseBoolean(httpRequest.getCookie("logined"));
    }
}
