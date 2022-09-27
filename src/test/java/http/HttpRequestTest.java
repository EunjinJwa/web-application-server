package http;

import model.HttpRequest;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

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

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
