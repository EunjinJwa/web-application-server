package model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;

import java.util.HashMap;
import java.util.Map;

public class RequestLine {

    private static final Logger log = LoggerFactory.getLogger(RequestLine.class);

    HttpMethod method;
    String path;
    Map<String, String> params = new HashMap<String, String>();

    public RequestLine(String requestLine) {
        String[] token = requestLine.split(" ");
        method = HttpMethod.valueOf(token[0]);

        String url = token[1];
        if (method.isPost()) {
            path = url;
            return ;
        }

        if (url.indexOf("?") == -1) {
            path = url;
        } else {
            path = url.substring(0, url.indexOf("?"));
            params = HttpRequestUtils.parseQueryString(url.substring(url.indexOf("?")+1));
        }
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getParams() {
        return params;
    }

    @Override
    public String toString() {
        return "RequestLine{" +
                "method=" + method +
                ", path='" + path + '\'' +
                ", params=" + params +
                '}';
    }
}
