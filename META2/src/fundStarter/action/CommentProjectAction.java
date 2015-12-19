package fundStarter.action;


import com.opensymphony.xwork2.ActionSupport;
import fundStarter.model.FundStarterBean;
import org.apache.struts2.interceptor.SessionAware;
import ws.GenericNotification;

import java.rmi.RemoteException;
import java.util.Map;

public class CommentProjectAction extends ActionSupport implements SessionAware
{
    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;
    private String username = null, password = null;
    private String error;
    private String comment = null;

    @Override
    public String execute() throws RemoteException
    {
        String msg = (String)session.get("username");
        msg = msg + ":" + comment;
        String error = this.getFundStarterBean().commentProject(msg);
        if(!error.equals("Commented with success"))
        {
            session.put("error",error);
        }

        //send message and update message history
        System.out.println("user is commenting ="+this.getFundStarterBean().getUsername());
        long idProject=this.getFundStarterBean().getViewDetailsId();
        String contributionMessage="["+this.getFundStarterBean().getUsername()+"] commented your project("+idProject+")";
        // String originUser=this.getFundStarterBean().getUsername();
        String projectAdmin=this.getFundStarterBean().getProjectAdmin(idProject);

        GenericNotification.commentNotification(contributionMessage,projectAdmin);




        return SUCCESS;
    }


    public String getUsername() {return username;}

    public void setUsername(String username) {this.username = username;}

    public String getPassword() {return password;}

    public void setPassword(String password) {this.password = password;}

    public String getComment() {return comment;}

    public void setComment(String comment) {this.comment = comment;}

    public FundStarterBean getFundStarterBean() {
        if(!session.containsKey("fundStarterBean"))
            this.setFundStarterBean(new FundStarterBean());
        return (FundStarterBean) session.get("fundStarterBean");
    }

    public void setFundStarterBean(FundStarterBean fundStarterBean) {
        this.session.put("fundStarterBean", fundStarterBean);
    }

    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }

}
