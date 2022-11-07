package model;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.HashMap;

public class HttpResponse {

    DataOutputStream dos;
    HashMap<String, String> headers;
    byte[] body;

    public HttpResponse(OutputStream outputStream) {
        this.dos = new DataOutputStream(outputStream);
        headers = new HashMap<>();
    }

    public void forward(String path) throws Exception {
        dos.writeBytes("HTTP/1.1 200 OK \r\n");
        if (path.endsWith(".css")) {
            dos.writeBytes("Content-Type: text/css;charset=utf-8\r\n");
        } else {
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
        }
        headerWrite();
        setBody(getBodyByPath(path));
        dos.writeBytes("Content-Length: " + body.length + "\r\n");
        dos.writeBytes("\r\n");

        dos.write(this.body, 0, this.body.length);
        dos.flush();
    }

    public void forward() throws Exception {
        dos.writeBytes("HTTP/1.1 200 OK \r\n");
        headerWrite();
        dos.writeBytes("Content-Length: " + body.length + "\r\n");
        dos.writeBytes("\r\n");

        dos.write(this.body, 0, this.body.length);
        dos.flush();
    }

    public void sendRedirect(String path) throws IOException {
        dos.writeBytes("HTTP/1.1 302 Found \r\n");
        headerWrite();
        dos.writeBytes("Location: " + path+ " \r\n");
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

    public void setBody(byte[] body) {
        this.body = body;
    }

    private byte[] getBodyByPath(String path) throws IOException {
        return Files.readAllBytes(new File("webapp" + path).toPath());
    }

    private boolean isCss(String path) {
        return path.endsWith(".css");
    }
}
