package ws;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;


@ServerEndpoint(value = "/ws")
public class WebSocketAnnotation {
    private static final AtomicInteger sequence = new AtomicInteger(1);
    private Session session;
    private static final CopyOnWriteArrayList<WebSocketAnnotation> users = new CopyOnWriteArrayList<WebSocketAnnotation>();

    public WebSocketAnnotation() {}

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
