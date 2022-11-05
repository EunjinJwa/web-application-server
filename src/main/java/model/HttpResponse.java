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
    }

    public void forward(String path) throws Exception {
        headerWrite();
        setBody(getBodyByPath(path));
        dos.write(this.body, 0, this.body.length);
        dos.flush();
    }

    private void headerWrite() throws IOException {
        if (this.headers == null) {
            return;
        }
        this.headers.keySet().stream()
                .forEach(key -> {
                    try {
                        dos.writeBytes(key.concat(": ").concat(this.headers.get(key)));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
        dos.writeBytes("\r\n");
    }

    public void sendRedirect(String path) {

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
