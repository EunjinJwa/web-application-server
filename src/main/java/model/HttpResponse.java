package model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.HashMap;

public class HttpResponse {

    private static final Logger log = LoggerFactory.getLogger(HttpResponse.class);

    private DataOutputStream dos;
    private HashMap<String, String> headers = new HashMap<>();
//    byte[] body;

    public HttpResponse(OutputStream outputStream) {
        this.dos = new DataOutputStream(outputStream);
    }

    public void forwardBody(String url) throws Exception {
        byte[] body = getBodyByPath(url);
        if (url.endsWith(".css")) {
            headers.put("Content-Type", "text/css;charset=utf-8");
        } else if (url.endsWith(".js")){
            headers.put("Content-Type", "application/javascript");
        } else {
            headers.put("Content-Type", "text/html;charset=utf-8");
        }
        headers.put("Content-Length", String.valueOf(body.length));
        response200Header();
        responseBody(body);
    }

    public void forwardBody(byte[] body) throws Exception {
        headers.put("Content-Length", String.valueOf(body.length));
        response200Header();
        responseBody(body);
    }

    private void response200Header() throws IOException {
        dos.writeBytes("HTTP/1.1 200 OK \r\n");
        headerWrite();
        dos.writeBytes("\r\n");
    }

    private void responseBody(byte[] body) throws IOException {
        dos.write(body, 0, body.length);
        dos.flush();
    }

    public void sendRedirect(String redirectUrl) throws IOException {
        headers.put("Location", redirectUrl);
        response302Header();
    }

    private void response302Header() throws IOException {
        dos.writeBytes("HTTP/1.1 302 Found \r\n");
        headerWrite();
        dos.writeBytes("\r\n");
    }


    private void headerWrite() throws IOException {
        if (this.headers == null) {
            return;
        }
        this.headers.keySet().stream()
                .forEach(key -> {
                    try {
                        dos.writeBytes(key.concat(": ").concat(this.headers.get(key)).concat(" \r\n"));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }


    public void setHeader(String header, String value) {
        headers.put(header, value);
    }

    private byte[] getBodyByPath(String path) throws IOException {
        return Files.readAllBytes(new File("webapp" + path).toPath());
    }
}
