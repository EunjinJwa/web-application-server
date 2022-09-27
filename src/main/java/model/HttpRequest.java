package model;

import lombok.*;
import util.HttpRequestUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;


@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HttpRequest {

    private String method;
    private String path;
    Map<String, String> headers;
    Map<String, String> parameters;

    public HttpRequest(InputStream in) {
        headers = new HashMap<>();
        parameters = new HashMap<>();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            setReqeustUrl(br);
            setHeaders(br);

            if (this.method.equals("POST"))
                setPostParameters(br);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void setReqeustUrl(BufferedReader br) throws IOException {
        String request = br.readLine();
        String[] token = request.split(" ");
        setMethod(token[0]);
        String url = token[1];
        setPath(url);
        if (this.getMethod().equals("GET")) {
            System.out.println("call get Parameter");
            setGetParameter(url);
        }
    }

    private void setPath(String url) {
        this.path = parsePathFromUrl(url);
    }

    private String parsePathFromUrl(String url) {
        if (this.getMethod().equals("GET") && url.indexOf("?") > 0) {
            return url.substring(0, url.indexOf("?"));
        } else {
            return url;
        }
    }

    private void setGetParameter(String url) {
        this.parameters = HttpRequestUtils.parseQueryString(HttpRequestUtils.extractQueryString(url));
    }
    private void setPostParameters(BufferedReader br) {
        try {
            this.parameters = HttpRequestUtils.parseQueryString(br.readLine());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void setMethod(String request) {
        this.method = HttpRequestUtils.extractHeader(request, 0);
    }

    private void setHeaders(BufferedReader br) {

        try {
            String line = br.readLine();
            while (!"".equals(line)) {
                HttpRequestUtils.Pair pair = HttpRequestUtils.parseHeader(line);
                headers.put(pair.getKey(), pair.getValue());

                line = br.readLine();

                if (line == null) {
                    return;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getHeader(String header) {
        return headers.get(header);
    }

    public String getParameter(String parameter) {
        return parameters.get(parameter);
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }
}
