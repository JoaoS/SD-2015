package ws;

/**
 * Created by joaosubtil on 12/12/15.
 */

import fundStarter.model.FundStarterBean;
import org.apache.struts2.interceptor.SessionAware;

import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;


@ServerEndpoint(value = "/wsGeneric", configurator = GetHttpSessionData.class)
public class GenericNotification {

    private static String username=null;
    public static CopyOnWriteArrayList<SessionKeeper> onlineUsers = new CopyOnWriteArrayList<SessionKeeper>();
    private static Session session;

    public GenericNotification() {

    }


    @OnOpen
    public void start(Session session, EndpointConfig config) {

        this.session=session;
        HttpSession sessionaux = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
        FundStarterBean beanS = (FundStarterBean) sessionaux.getAttribute("fundStarterBean");
        this.username=beanS.getUsername();
        System.out.println("created websocket for:\nusername="+beanS.getUsername()+"\narraySize="+onlineUsers.size());
        onlineUsers.add(new SessionKeeper(session,beanS.getUsername(),sessionaux,beanS));

    }

    @OnClose
    public void end() {
        // clean up once the WebSocket connection is closed
        for(int i=0;i<onlineUsers.size();i++){
            if(onlineUsers.get(i).getUsername()==username){
                onlineUsers.remove(i);
                System.out.println("removed user in="+i);
            }
        }
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
        System.out.println("Websocket Error");
        t.printStackTrace();
    }


    private static void sendMessage(String text) {
        // uses *this* object's session to call sendText()
        System.out.println("este user mandou notificação="+username);
        int i=0;
        try {
           /* for(i = 0;i<onlineUsers.size();i++)
            {
                //ver se aquele user é  admin do projecto
                /*SessionKeeper aux=onlineUsers.get(i);
                String s=aux.beanS.getAdminProjectIds();
                System.out.println("user é admin="+s);

                onlineUsers.get(i).getSession().getBasicRemote().sendText(text);
            }
            */
            session.getBasicRemote().sendText(text);

        }  catch (IOException e) {
            // clean up once the WebSocket connection is closed
            try {
                onlineUsers.get(i).getSession().close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    public static void sendNotification(String messsage){

        sendMessage(messsage);
        System.out.println("notification sent");


    }

}




