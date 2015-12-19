package fundStarter.action;


import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuthService;
import com.opensymphony.xwork2.ActionSupport;
import fundStarter.model.FundStarterBean;
import fundStarter.model.TumblrBean;
import org.apache.struts2.interceptor.SessionAware;
import ws.GenericNotification;
import sun.invoke.empty.Empty;

import java.rmi.RemoteException;
import java.util.Map;

public class ContributeProjectAction extends ActionSupport implements SessionAware
{
    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;
    private String alternativeVotedId;
    private float pledgeValue;

    @Override
    public String execute() throws RemoteException
    {
        String error;
        if(alternativeVotedId.equalsIgnoreCase("There are no alternatives for this project."))
        {
            this.getFundStarterBean().setAlternativeVotedId(0);
        }
        else
        {
            this.getFundStarterBean().setAlternativeVotedId(Long.valueOf(alternativeVotedId));
        }
        this.getFundStarterBean().setPledgeValue(pledgeValue);
        error = this.getFundStarterBean().contributeToProject();
        if(!error.equals("Donation made successfully."))
        {
            session.put("error",error);
        }
        else
        {
            session.put("success",error);

            //send message and update message history

            long idProject=this.getFundStarterBean().getViewDetailsId();
            String contributionMessage="["+this.getFundStarterBean().getUsername()+"]Donated to your project("+idProject+")";
           // String originUser=this.getFundStarterBean().getUsername();
            String projectAdmin=this.getFundStarterBean().getProjectAdmin(idProject);

            //enviar id e valor novo do projecto
            //fazer split na getCurrentValue
            Long projectValue=this.getFundStarterBean().getProjectValue(idProject);
            GenericNotification.donationNotification(contributionMessage,projectAdmin,"UPDT|ID : "+idProject+"|"+projectValue);



        /*
            if(session.get("blogUrl") != null)
            {
                ///tenho de meter like na pagina do projecto do tumbrlrlrlrlrlrlr
                //sacar id do projecto e a reblog key
                //url-api.tumblr.com/v2/user/like	, by post


                String post_id=null;
                post_id=this.getFundStarterBean().getPostIdTumblr(idProject);
                System.out.println("id="+post_id);
                //se nao der erro
                if (!post_id.equals("Error occurred getting post id")){


                    //fazer pedido para saber os dados daquele post no tumbllrlrlrlr

                    //api.tumblr.com/v2/blog/{base-hostname}/posts[/type]?api_key={key}&[optional-params=]
                    //get base hostname
                    String base_hostname=this.getFundStarterBean().getBaseHostName(idProject);
                    String api_key="54j8EOb53ihVMtfuSwvkyoY8i7cth91cWoFOugFT1wgyX6x0t4";

                    //published posts
                    //
                    String protected_resource_url = "http://api.tumblr.com/v2/blog/"+base_hostname+"/posts?api_key="+api_key+"";
                    System.out.println(protected_resource_url);

                    TumblrBean tumblrBean =  (TumblrBean) session.get("tumblrBean");
                    OAuthService service = tumblrBean.getService();
                    OAuthRequest requestOauth = new OAuthRequest(Verb.POST, protected_resource_url, service);

                    service.signRequest(tumblrBean.getAccessToken(), requestOauth);
                    Response response = requestOauth.send();
                    System.out.println("TONY"+response.getBody());

                    /*-------------------------------------------------------------*/


                   /*String protected_resource_url = "url-api.tumblr.com/v2/user/like";
                    TumblrBean tumblrBean =  (TumblrBean) session.get("tumblrBean");
                    OAuthService service = tumblrBean.getService();
                    OAuthRequest requestOauth = new OAuthRequest(Verb.POST, protected_resource_url, service);

                    requestOauth.addBodyParameter("type","text");
                    requestOauth.addBodyParameter("title","Created project " + projectName);
                    requestOauth.addBodyParameter("body",description);

                    service.signRequest(tumblrBean.getAccessToken(), requestOauth);
                    Response response = requestOauth.send();
                    System.out.println(response.getBody());
                    */
/*
                }
        */


            }



        return  SUCCESS;

    }


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

    public String getAlternativeVotedId() {return alternativeVotedId;}

    public void setAlternativeVotedId(String alternativeVotedId) {this.alternativeVotedId = alternativeVotedId;}

    public float getPledgeValue() {return pledgeValue;}

    public void setPledgeValue(float pledgeValue) {this.pledgeValue = pledgeValue;}
}
