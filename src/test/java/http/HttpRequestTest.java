package http;

import com.google.common.base.Strings;
import model.HttpRequest;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpRequestTest {

    private String testDirectory = "./src/test/resources/";

    @Test
    public void request_GET() {
        try {
            File request = new File(testDirectory.concat("Http_Get.txt"));
            InputStream in = new FileInputStream(request);

            HttpRequest httpRequest = new HttpRequest(in);

            Assertions.assertThat(httpRequest.getMethod()).isEqualTo("GET");
            Assertions.assertThat(httpRequest.getPath()).isEqualTo("/user/create");
            Assertions.assertThat(httpRequest.getHeader("Connection")).isEqualTo("keep-alive");
            Assertions.assertThat(httpRequest.getParameter("userId")).isEqualTo("kassy");

            System.out.println("Get Test Completed.");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void request_POST() {
        try {
            InputStream in = new FileInputStream(new File(testDirectory.concat("Http_POST.txt")));

            HttpRequest httpRequest = new HttpRequest(in);

            Assertions.assertThat(httpRequest.getMethod()).isEqualTo("POST");
            Assertions.assertThat(httpRequest.getPath()).isEqualTo("/user/create");
            Assertions.assertThat(httpRequest.getParameter("userId")).isEqualTo("kassy");

            System.out.println("POST Test Completed.");
        } catch (Exception e) {

        }
    }

    @Test
    public void parseCookieTest() {
        String cookie = "logined=false; name=jinny";
        String[] cookieValues = cookie.split(";");
        Map<String, String> cookieMap = Arrays.stream(cookieValues).map(c -> c.split("="))
                .collect(Collectors.toMap(p -> p[0], p -> p[1]));
        Boolean isLogined = Boolean.valueOf(cookieMap.get("logined"));

        Assertions.assertThat(isLogined).isTrue();
    }

}
