package model;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class RequestLineTest {

    @Test
    public void getMethod() {
        RequestLine data = getRequestLine("GET /user/create?userId=kassy&password=123&name=jinny HTTP/1.1");

        Assertions.assertThat(data.getMethod()).isEqualTo(HttpMethod.GET);
    }

    @Test
    public void getPath() {
        RequestLine data = getRequestLine("GET /user/create?userId=kassy&password=123&name=jinny HTTP/1.1");
        Assertions.assertThat(data.getPath()).isEqualTo("/user/create");

        data = getRequestLine("POST /user/create HTTP/1.1");
        Assertions.assertThat(data.getPath()).isEqualTo("/user/create");
    }

    @Test
    public void getParams() {
        RequestLine data = getRequestLine("GET /user/create?userId=kassy&password=123&name=jinny HTTP/1.1");

        Assertions.assertThat(data.getParams().get("password")).isEqualTo("123");
    }

    private RequestLine getRequestLine(String requestLine) {
        return new RequestLine(requestLine);
    }
}