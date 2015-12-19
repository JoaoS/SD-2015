package ws;

import fundStarter.model.FundStarterBean;

import javax.servlet.http.HttpSession;
import javax.websocket.Session;

/**
 * Created by joaosubtil on 13/12/15.
 */

/*
* class made to know online users and their data
* */
public class SessionKeeper {

    private Session session;
    private String username;
    private HttpSession httpSession;
    private FundStarterBean beanS;


    public SessionKeeper(Session session, String username, HttpSession httpSession, FundStarterBean beanS) {
        this.session = session;
        this.username = username;
        this.httpSession = httpSession;
        this.beanS = beanS;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public HttpSession getHttpSession() {
        return httpSession;
    }

    public void setHttpSession(HttpSession httpSession) {
        this.httpSession = httpSession;
    }

    public FundStarterBean getBeanS() {
        return beanS;
    }

    public void setBeanS(FundStarterBean beanS) {
        this.beanS = beanS;
    }
}
