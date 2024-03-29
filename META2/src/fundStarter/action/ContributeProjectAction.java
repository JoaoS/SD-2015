package fundStarter.action;


import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuthService;
import com.opensymphony.xwork2.ActionSupport;
import fundStarter.model.FundStarterBean;
import fundStarter.model.TumblrBean;
import org.apache.struts2.interceptor.SessionAware;
import org.json.JSONObject;
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


                    /*fazer pedido para saber os dados daquele post no tumbllrlrlrlr*/

                    //api.tumblr.com/v2/blog/{base-hostname}/posts[/type]?api_key={key}&[optional-params=]
                    //get base hostname
                    String post_id2=this.getFundStarterBean().getPostIdTumblr(idProject);
                    String base_hostname=this.getFundStarterBean().getBaseHostName(idProject);
                    String api_key="54j8EOb53ihVMtfuSwvkyoY8i7cth91cWoFOugFT1wgyX6x0t4";

                    String protected_resource_url = "http://api.tumblr.com/v2/blog/"+base_hostname+"/posts?api_key="+api_key+"&id="+post_id2;
                    TumblrBean tumblrBean =  (TumblrBean) session.get("tumblrBean");
                    OAuthService service = tumblrBean.getService();
                    OAuthRequest requestOauth = new OAuthRequest(Verb.POST, protected_resource_url, service);

                    service.signRequest(tumblrBean.getAccessToken(), requestOauth);
                    Response response = requestOauth.send();
                    System.out.println("likes?="+response.getBody());

                    JSONObject obj = null;
                    try {
                        obj = new JSONObject(response.getBody());
                        String rkey = obj.getJSONObject("response").getJSONArray("posts").getJSONObject(0).get("reblog_key").toString();
                        System.out.println("reblog key="+rkey);


                        //fazer like no post
                        String url = "http://api.tumblr.com/v2/user/like";
                        TumblrBean b2 =  (TumblrBean)session.get("tumblrBean");
                        OAuthService s2 = b2.getService();
                        OAuthRequest r2 = new OAuthRequest(Verb.POST, url, s2);

                        r2.addBodyParameter("id",""+post_id2);
                        r2.addBodyParameter("reblog_key",rkey);

                        s2.signRequest(b2.getAccessToken(), r2);
                        Response response2 = r2.send();

                        System.out.println(response2.getBody());



                    } catch (Exception e) {
                        System.out.println("Exception with json");
                        e.printStackTrace();
                    }
                    
                }


            }

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
