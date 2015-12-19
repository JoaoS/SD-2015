package com.github.scribejava.apis.examples;

import com.github.scribejava.apis.TumblrApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.*;
import com.github.scribejava.core.oauth.OAuthService;
import org.json.JSONException;
import org.json.simple.parser.ParseException;
import java.util.Scanner;
import org.json.JSONObject;


public class TumblrExample {

    private static final String PROTECTED_RESOURCE_URL = "http://api.tumblr.com/v2/user/info";

    public static void main(String[] args) throws ParseException, JSONException {
        OAuthService service = new ServiceBuilder()
                .provider(TumblrApi.class)
                .apiKey("54j8EOb53ihVMtfuSwvkyoY8i7cth91cWoFOugFT1wgyX6x0t4")
                .apiSecret("A1ZLlO8VPDHkZizbrJP94aRYrFYnVjoifZX3WnkQ8E001X314k")
                .callback("http://localhost:8080") // OOB forbidden. We need an url and the better is on the tumblr website !
                .build();
        Scanner in = new Scanner(System.in);

        System.out.println("=== Tumblr's OAuth Workflow ===");
        System.out.println();

        // Obtain the Request Token
        System.out.println("Fetching the Request Token...");
        Token requestToken = service.getRequestToken();
        System.out.println("Got the Request Token!");
        System.out.println();

        System.out.println("Now go and authorize Scribe here:");
        System.out.println(service.getAuthorizationUrl(requestToken));
        System.out.println("And paste the verifier here");
        System.out.print(">>");
        Verifier verifier = new Verifier(in.nextLine());
        System.out.println();

        // Trade the Request Token and Verfier for the Access Token
        System.out.println("Trading the Request Token for an Access Token...");
        Token accessToken = service.getAccessToken(requestToken,
                verifier);
        System.out.println("Got the Access Token!");
        System.out.println("(if your curious it looks like this: " + accessToken + " )");
        System.out.println();

        // Now let's go and ask for a protected resource!
        System.out.println("Now we're going to access a protected resource...");
        OAuthRequest request = new OAuthRequest(Verb.GET, PROTECTED_RESOURCE_URL, service);
        service.signRequest(accessToken, request);
        Response response = request.send();
        System.out.println(response.getBody());
        System.out.println("Got it! Lets see what we found...");
        System.out.println();
        //----------get username------------
        JSONObject obj = new JSONObject(response.getBody());
        String blogUrl = obj.getJSONObject("response").getJSONObject("user").getJSONArray("blogs").getJSONObject(0).get("url").toString();
        System.out.println(blogUrl);
        blogUrl = blogUrl.replace("http://","");
        blogUrl = blogUrl.replace("/","");
        System.out.println(blogUrl);

        System.out.println();
        System.out.println("Thats it man! Go and build something awesome with Scribe! :)");
    }
}
