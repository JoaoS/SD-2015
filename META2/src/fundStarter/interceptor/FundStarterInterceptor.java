package fundStarter.interceptor;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;
import java.util.Map;


public class FundStarterInterceptor implements Interceptor {

    private static final long serialVersionUID = 189237412378L;

    @Override
    public void destroy() {

    }

    @Override
    public void init() {

    }

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        Map<String, Object> session = invocation.getInvocationContext().getSession();

        if( session.get("loggedin") != null)
        {
            if((boolean)session.get("loggedin") == true)
                return invocation.invoke();
            else
                return Action.INPUT;
        }
        else
        {
            return Action.INPUT;
        }
    }

}
