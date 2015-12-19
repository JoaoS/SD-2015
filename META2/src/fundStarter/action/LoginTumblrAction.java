package fundStarter.action;

import com.github.scribejava.apis.TumblrApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.Token;
import com.github.scribejava.core.oauth.OAuthService;
import com.opensymphony.xwork2.ActionSupport;
import fundStarter.model.TumblrBean;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.interceptor.SessionAware;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created by joaogoncalves on 17/12/15.
 */
public class LoginTumblrAction extends ActionSupport implements SessionAware {
    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;
    private String url;

    @Override
    public String execute() {
        HttpServletRequest request = ServletActionContext.getRequest();
        String  parameter = request.getParameter("login");
        OAuthService service;
        if(parameter.equals("1"))
        {
            service = new ServiceBuilder()
                    .provider(TumblrApi.class)
                    .apiKey("54j8EOb53ihVMtfuSwvkyoY8i7cth91cWoFOugFT1wgyX6x0t4")
                    .apiSecret("A1ZLlO8VPDHkZizbrJP94aRYrFYnVjoifZX3WnkQ8E001X314k")
                    .callback("http://localhost:8080/tumblrCallbackAction")
                    .build();
        }
        else
        {
            service = new ServiceBuilder()
                    .provider(TumblrApi.class)
                    .apiKey("54j8EOb53ihVMtfuSwvkyoY8i7cth91cWoFOugFT1wgyX6x0t4")
                    .apiSecret("A1ZLlO8VPDHkZizbrJP94aRYrFYnVjoifZX3WnkQ8E001X314k")
                    .callback("http://localhost:8080/associateCallbackAction")
                    .build();
        }
        Token requestToken = service.getRequestToken();
        url = service.getAuthorizationUrl(requestToken);
        this.setTumblrBean(new TumblrBean(service));
        this.setTumblrBeanRequestToken(requestToken);
        return "redirect";
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setTumblrBean(TumblrBean tumblrBean) {
        if(session.containsKey("tumblrBean"))
            session.remove("tumblrBean");
        this.session.put("tumblrBean", tumblrBean);
    }

    public void setTumblrBeanRequestToken(Token requestToken) {
        TumblrBean tumblrBean = (TumblrBean) this.session.get("tumblrBean");
        tumblrBean.setRequestToken(requestToken);
    }


    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }
}
