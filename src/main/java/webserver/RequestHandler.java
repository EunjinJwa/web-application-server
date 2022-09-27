package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Map;

import db.DataBase;
import model.HttpRequest_o;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;

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
            DataOutputStream dos = new DataOutputStream(out);
            HttpRequest_o request = IOUtils.parseHttpRequest(in);
            final String url = request.getUrl();

            String returnUrl = url;
            if ("/user/create".equals(url)) {
                User user = parseQueryStringToUser(request.getRequestBody());
                DataBase.addUser(user);
                returnUrl = "/index.html";
                response302Header(dos, returnUrl);
            } else if ("/user/login".equals(url)) {
                User user = parseQueryStringToUser(request.getRequestBody());
                log.info("로그인 시도 : " + user);
                User member = DataBase.findUserById(user.getUserId());
                if (member != null && member.getPassword().equals(user.getPassword())) {
                    log.info("로그인 성공");
                    // 로그인 성공
                    responseLoginSuccess(dos, "/index.html");
                } else {
                    // 로그인 실패
                    log.error("로그인 실패");
                    responseLoginFail(dos, "/user/login_failed.html");
                }
            } else if ("/user/list".equals(url)) {
                if (!request.getCookie().getLogined()) {
                    log.info("로그인 페이지로 이동");
                    responseRedirect(dos, "/user/login.html");
                    return;
                }
                log.info("로그인 상태");
                Collection<User> allUsers = DataBase.findAll();
                System.out.println(allUsers);

                byte[] body = genUserListHtmlBody(allUsers).getBytes();
                response200Header(dos, body.length, null);
                responseBody(dos, body);

            } else {
                byte[] body = Files.readAllBytes(new File("webapp" + url).toPath());

                response200Header(dos, body.length, url);
                responseBody(dos, body);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }

    private void responseLoginFail(DataOutputStream dos, String redirectionUrl) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: " + redirectionUrl + " \r\n");
            dos.writeBytes("Set-Cookie: logined=false \r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseRedirect(DataOutputStream dos, String redirectionUrl) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: " + redirectionUrl + " \r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
    private void responseLoginSuccess(DataOutputStream dos, String redirectionUrl) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Set-Cookie: logined=true \r\n");
            dos.writeBytes("Set-Cookie: name=jinny \r\n");
            dos.writeBytes("Location: " + redirectionUrl + " \r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }


    private void response200Header(DataOutputStream dos, int lengthOfBodyContent, String extension) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            if (extension != null && extension.endsWith(".css")) {
                dos.writeBytes("Content-Type: text/css;charset=utf-8\r\n");
            } else {
                dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
            }
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void response302Header(DataOutputStream dos, String redirectionUrl) {
        try {
            dos.writeBytes("HTTP/1.1 302 Found \r\n");
            dos.writeBytes("Location: " + redirectionUrl + " \r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private User parseQueryStringToUser(String queryString) {
        Map<String, String> queryMap = HttpRequestUtils.parseQueryString(queryString);
        User user = new User(queryMap.get("userId"), queryMap.get("password"), queryMap.get("name"), queryMap.get("email"));
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
}
