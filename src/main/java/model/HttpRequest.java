package model;

import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;

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

    private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);

    RequestLine requestLine;
    Map<String, String> headers;
    Map<String, String> cookies;
    Map<String, String> params;

    public HttpRequest(InputStream in) {
        headers = new HashMap<>();
        params = new HashMap<>();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "utf-8"));

            String line = br.readLine();
            requestLine = new RequestLine(line);

            line = br.readLine();
            while (!"".equals(line)) {
                HttpRequestUtils.Pair pair = HttpRequestUtils.parseHeader(line);
                headers.put(pair.getKey(), pair.getValue());
                if ("Cookie".equals(pair.getKey())) {
                    cookies = HttpRequestUtils.parseCookies(pair.getValue());
                }

                line = br.readLine();

                if (line == null) {
                    return;
                }
            }

            if (headers.containsKey("Content-Length")) {
                String body = IOUtils.readData(br, Integer.parseInt(this.getHeader("Content-Length")));
                params = HttpRequestUtils.parseQueryString(body);
            } else {
                params = requestLine.getParams();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return "HttpRequest{" +
                "requestLine=" + requestLine +
                ", headers=" + headers +
                ", cookies=" + cookies +
                ", params=" + params +
                '}';
    }

    public String getHeader(String header) {
        return headers.get(header);
    }

    public String getParameter(String parameter) {
        return params.get(parameter);
    }

    public HttpMethod getMethod() {
        return requestLine.getMethod();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public Map<String, String> getParams() {
        return params;
    }

    public Map<String, String> getCookies() {
        return cookies;
    }

    public String getCookie(String name) {
        if (cookies == null) {
            return null;
        }
        return cookies.get(name);
    }
}
