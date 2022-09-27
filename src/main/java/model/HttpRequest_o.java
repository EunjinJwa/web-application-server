package model;

import lombok.*;

@Getter @Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HttpRequest_o {

    private String method;
    private String url;
    private String queryString;
    private String contentType;
    private String contentLength;
    private String requestBody;
    private HeaderCookie cookie = new HeaderCookie();


}
