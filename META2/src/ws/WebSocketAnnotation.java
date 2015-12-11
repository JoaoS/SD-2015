package ws;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import javax.websocket.server.ServerEndpoint;
import javax.websocket.OnOpen;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnError;
import javax.websocket.Session;


@ServerEndpoint(value = "/ws")
public class WebSocketAnnotation {
    private static final AtomicInteger sequence = new AtomicInteger(1);
    private final String username;
    private Session session;
    private static final CopyOnWriteArrayList<WebSocketAnnotation> users = new CopyOnWriteArrayList<WebSocketAnnotation>();

    public WebSocketAnnotation() {
        username = "User" + sequence.getAndIncrement();
    }

    @OnOpen
    public void start(Session session) {
        this.session = session;
        users.add(this);
    }

    @OnClose
    public void end() {
        // clean up once the WebSocket connection is closed
        users.remove(this);
    }

    @OnMessage
    public void receiveMessage(String message) {
        // one should never trust the client, and sensitive HTML
        // characters should be replaced with &lt; &gt; &quot; &amp;
        String sendMessage = new StringBuffer(message).toString();
        sendMessage(sendMessage);
    }

    @OnError
    public void handleError(Throwable t) {
        t.printStackTrace();
        users.remove(this);
    }

    private void sendMessage(String text) {
        // uses *this* object's session to call sendText()
        try {
            for(int i = 0;i<users.size();i++)
            {
                users.get(i).session.getBasicRemote().sendText(text);
            }
        } catch (IOException e) {
            // clean up once the WebSocket connection is closed
            try {
                this.session.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
}
