package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Map;

import db.DataBase;
import model.HttpRequest;
import model.HttpResponse_;
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

            DataOutputStream dos = new DataOutputStream(out);

            if ("/user/create".equals(httpRequest.getPath())) {
                User user = parseParametersToUser(httpRequest.getParameters());
                DataBase.addUser(user);
                HttpResponse_ httpResponse = new HttpResponse_();
                httpResponse.setRedirectUrl("/index.html");
                sendRedirect(dos, httpResponse);
            } else if ("/user/login".equals(httpRequest.getPath())) {
                User member = DataBase.findUserById(httpRequest.getParameter("userId"));
                if (member != null && member.getPassword().equals(httpRequest.getParameter("password"))) {
                    log.info("로그인 성공");
                    HttpResponse_ httpResponse = new HttpResponse_();
                    httpResponse.setRedirectUrl("/index.html");
                    httpResponse.setHeader("Set-Cookie", "logined=true; name="+ member.getName());
                    sendRedirect(dos, httpResponse);
                } else {
                    log.error("로그인 실패");
                    HttpResponse_ httpResponse = new HttpResponse_();
                    httpResponse.setRedirectUrl("/user/login_failed.html");
                    httpResponse.setHeader("Set-Cookie", "logined=false");
                    sendRedirect(dos, httpResponse);
                }
            } else if ("/user/list".equals(httpRequest.getPath())) {
                HttpResponse_ httpResponse = new HttpResponse_();

                if (!isLogined(httpRequest)) {
                    log.info("로그인 페이지로 이동");
                    httpResponse.setRedirectUrl("/user/login.html");
                    sendRedirect(dos, httpResponse);
                    return;
                }
                log.info("로그인 상태");
                Collection<User> allUsers = DataBase.findAll();

                byte[] body = genUserListHtmlBody(allUsers).getBytes();
                forward(dos, body);
            } else {
                forward(dos, httpRequest.getPath());
            }
        } catch (IOException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }

    private void sendRedirect(DataOutputStream dos, HttpResponse_ response) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes(response.getHeaderString() + " \r\n");
            dos.writeBytes("Location: " + response.getRedirectUrl() + " \r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void forward(DataOutputStream dos, String path) {
        try {
            byte[] body = Files.readAllBytes(new File("webapp" + path).toPath());
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            if (path.endsWith(".css")) {
                dos.writeBytes("Content-Type: text/css;charset=utf-8\r\n");
            } else {
                dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            }
            dos.writeBytes("Content-Length: " + body.length + "\r\n");
            dos.writeBytes("\r\n");

            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void forward(DataOutputStream dos, byte[] body) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Length: " + body.length + "\r\n");
            dos.writeBytes("\r\n");

            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
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
