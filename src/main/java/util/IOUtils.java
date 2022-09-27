package util;

import model.HttpHeaderType;
import model.HttpRequest_o;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

public class IOUtils {

    public static HttpRequest_o parseHttpRequest(InputStream in) throws IOException {
        HttpRequest_o httpRequestO = new HttpRequest_o();
        BufferedReader br = new BufferedReader(new InputStreamReader(in, "utf-8"));
        String line = br.readLine();
        final String header = line;

        System.out.println("[Header] " + header);
        if (header != null) {
            String method = HttpRequestUtils.extractUrlHeader(header, HttpHeaderType.METHOD);
            String url = HttpRequestUtils.extractUrlHeader(header, HttpHeaderType.URL);

            httpRequestO.setMethod(method);
            httpRequestO.setUrl(url);
            if (method.equals("GET")) {
                httpRequestO.setQueryString(HttpRequestUtils.extractQueryString(url));
            }
        }

        while (!"".equals(line)) {
            line = br.readLine();
            if (line.startsWith("Content-Length")) {
                httpRequestO.setContentLength(HttpRequestUtils.getValueFromHeader(line));
            } else if (line.startsWith("Cookie")) {
                Map<String, String> stringStringMap = HttpRequestUtils.parseCookies(HttpRequestUtils.getValueFromHeader(line));
                Boolean logined = Boolean.valueOf(stringStringMap.get("logined"));
                httpRequestO.getCookie().setLogined(logined);
            }
            if (line == null) {
                break;
            }
            System.out.println("> " + line);
        }

        if (httpRequestO.getMethod().equals("POST")) {
            String readData = readData(br, Integer.parseInt(httpRequestO.getContentLength()));
            httpRequestO.setRequestBody(readData);
        }
        return httpRequestO;
    }

    /**
     * @param BufferedReader는
     *            Request Body를 시작하는 시점이어야
     * @param contentLength는
     *            Request Header의 Content-Length 값이다.
     * @return
     * @throws IOException
     */
    public static String readData(BufferedReader br, int contentLength) throws IOException {
        char[] body = new char[contentLength];
        br.read(body, 0, contentLength);
        return String.copyValueOf(body);
    }
}
