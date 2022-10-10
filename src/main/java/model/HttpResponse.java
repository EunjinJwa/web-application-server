package model;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpResponse {

    String redirectUrl;
    Map<String, String> responseHeader;

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public Map<String, String> getResponseHeader() {
        return responseHeader;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public HttpResponse() {
        this.responseHeader = new HashMap<>();
    }

    public void setHeader(String header, String value) {
        responseHeader.put(header, value);
    }

    public String getHeaderString() {
        return responseHeader.keySet()
                .stream()
                .map(key -> key + ": " + responseHeader.get(key))
                .collect(Collectors.joining(" \r\n"));
    }
}
