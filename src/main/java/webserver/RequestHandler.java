package webserver;

import java.io.*;
import java.net.Socket;
import java.util.Collection;
import java.util.Map;

import db.DataBase;
import model.HttpRequest;
import model.HttpResponse;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler extends Thread {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
            HttpRequest httpRequest = new HttpRequest(in);
            System.out.println("request > " + httpRequest.getPath());

            if ("/user/create".equals(httpRequest.getPath())) {
                User user = parseParametersToUser(httpRequest.getParameters());
                DataBase.addUser(user);
                HttpResponse response = new HttpResponse(out);
                response.sendRedirect("/index.html");

            } else if ("/user/login".equals(httpRequest.getPath())) {
                User member = DataBase.findUserById(httpRequest.getParameter("userId"));
                if (member != null && member.getPassword().equals(httpRequest.getParameter("password"))) {
                    log.info("로그인 성공");

                    HttpResponse response = new HttpResponse(out);
                    response.setHeader("Set-Cookie", "logined=true; name="+ member.getName());
                    response.sendRedirect("/index.html");

                } else {
                    log.error("로그인 실패");
                    HttpResponse response = new HttpResponse(out);
                    response.setHeader("Set-Cookie", "logined=false");
                    response.sendRedirect("/user/login_failed.html");

                }
            } else if ("/user/list".equals(httpRequest.getPath())) {
                HttpResponse response = new HttpResponse(out);

                if (!isLogined(httpRequest)) {
                    log.info("로그인 페이지로 이동");
                    response.sendRedirect("/user/login.html");
                    return;
                }
                log.info("로그인 상태");
                Collection<User> allUsers = DataBase.findAll();
                response.setBody(genUserListHtmlBody(allUsers).getBytes());
                response.forward();

            } else {
                HttpResponse response = new HttpResponse(out);
                response.forward(httpRequest.getPath());
            }
        } catch (IOException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private User parseParametersToUser(Map<String, String> userParams) {
        User user = new User(userParams.get("userId"), userParams.get("password"), userParams.get("name"), userParams.get("email"));
        System.out.println("User >> " + user);
        return user;
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
