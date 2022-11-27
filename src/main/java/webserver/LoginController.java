package webserver;

import db.DataBase;
import model.HttpRequest;
import model.HttpResponse;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    @Override
    public void doPost(HttpRequest request, HttpResponse response) throws IOException {
        User member = DataBase.findUserById(request.getParameter("userId"));
        if (member != null && member.getPassword().equals(request.getParameter("password"))) {
            log.info("로그인 성공");

            response.setHeader("Set-Cookie", "logined=true; name="+ member.getName());
            response.sendRedirect("/index.html");

        } else {
            log.error("로그인 실패");
            response.setHeader("Set-Cookie", "logined=false");
            response.sendRedirect("/user/login_failed.html");
        }
    }

    @Override
    public void doGet(HttpRequest request, HttpResponse response) {

    }
}
