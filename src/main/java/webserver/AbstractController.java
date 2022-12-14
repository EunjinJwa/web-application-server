package webserver;

import model.HttpRequest;
import model.HttpResponse;

import java.io.IOException;

public class AbstractController implements Controller {
    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception {
        if (request.getMethod().isPost()) {
            doPost(request, response);
        } else {
            doGet(request, response);
        }
    }

    public void doPost(HttpRequest request, HttpResponse response) throws IOException {

    }

    public void doGet(HttpRequest request, HttpResponse response) throws Exception {
        response.forwardBody(request.getPath());
    }
}
