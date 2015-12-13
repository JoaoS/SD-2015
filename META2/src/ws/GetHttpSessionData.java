package ws;

/**
 * Created by joaosubtil on 12/12/15.
 */
import javax.servlet.http.HttpSession;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;

public class GetHttpSessionData extends ServerEndpointConfig.Configurator {
@Override
public void modifyHandshake(ServerEndpointConfig config,
        HandshakeRequest request,
        HandshakeResponse response) {
        HttpSession httpSession = (HttpSession)request.getHttpSession();
        config.getUserProperties().put(HttpSession.class.getName(),httpSession);
        }
}
