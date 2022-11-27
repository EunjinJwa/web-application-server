package webserver;

import model.HttpRequest;
import model.HttpResponse;

import java.io.IOException;

public interface Controller {

    void service(HttpRequest request, HttpResponse response) throws Exception;

}
