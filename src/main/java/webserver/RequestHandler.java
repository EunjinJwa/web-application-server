package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Map;

import model.HttpRequest;
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

            HttpRequest request = IOUtils.parseHttpRequest(in);
            final String url = request.getUrl();
            String returnUrl = url;

            if ("POST".equals(request.getMethod())) {
                if ("/user/create".equals(url)) {
                    User user = parseQueryStringToUser(request.getRequestBody());
                    returnUrl = "/index.html";
                }
            }

            DataOutputStream dos = new DataOutputStream(out);
            if (!returnUrl.equals(url)) {
                response302Header(dos, returnUrl);
            } else {
                byte[] body = Files.readAllBytes(new File("webapp" + url).toPath());

                response200Header(dos, body.length);
                responseBody(dos, body);
            }

        } catch (IOException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }


    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
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
}
