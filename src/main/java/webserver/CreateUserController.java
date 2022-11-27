package webserver;

import db.DataBase;
import model.HttpRequest;
import model.HttpResponse;
import model.User;

import java.io.IOException;

public class CreateUserController extends AbstractController {

    @Override
    public void doPost(HttpRequest request, HttpResponse response) throws IOException {
        User user = User.builder()
                .userId(request.getParameter("userId"))
                .name(request.getParameter("name"))
                .password(request.getParameter("password"))
                .email(request.getParameter("password"))
                .build();
        DataBase.addUser(user);
        response.sendRedirect("/index.html");
    }

    @Override
    public void doGet(HttpRequest request, HttpResponse response) {

    }

}
