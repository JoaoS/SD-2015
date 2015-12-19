package fundStarter.model;

import com.github.scribejava.core.model.Token;
import fundStarter.DataServer.*;
import fundStarter.commons.Alternative;
import fundStarter.commons.Reward;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.Properties;
import java.io.*;

public class FundStarterBean {

    private DataServer_I server;
    private String username;
    private String password;
    private String bi;
    private int age;
    private String email;
    private String idSelected;
    private long viewDetailsId;
    private long alternativeVotedId;
    private float pledgeValue;
    private int tumblrUser;

    private ArrayList<String> oldWebsocketMessages=new ArrayList<String>();


    public FundStarterBean() {

        int rmiPort=0;
        String rmiIp=null,remoteName=null;

        Properties prop = new Properties();
        InputStream input = null;
        try {

            //System.out.println(new File("teste").getAbsolutePath());
            input = new FileInputStream("new.properties");
            prop.load(input);

            rmiPort=Integer.parseInt(prop.getProperty("rmiPort"));
            remoteName=prop.getProperty("rmiName");
            rmiIp=prop.getProperty("rmiIp");
            System.out.println("Properties loaded correctly");

        } catch (IOException ex) {
            System.out.println("Error loading DataServer properties file. The default values were set");
            rmiPort=5000;
            remoteName="DataServer";
            rmiIp="localhost";
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


        try {
            //System.getProperties().put("java.security.policy", "security.policy");
            //System.setSecurityManager(new RMISecurityManager());
            server = (DataServer_I) LocateRegistry.getRegistry(rmiIp,rmiPort).lookup(remoteName);
        }
        catch(NotBoundException |RemoteException e) {
            e.printStackTrace();
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setBi(String bi) {this.bi = bi;}

    public void setAge(int age) {this.age = age;}

    public void setEmail(String email) {this.email = email;}

    public int getTumblrUser() {
        return tumblrUser;
    }

    public void setTumblrUser(int tumblrUser) {
        this.tumblrUser = tumblrUser;
    }

    public String getIdSelected() {
        return idSelected;
    }

    public void setIdSelected(String idSelected) {
        this.idSelected = idSelected;
    }

    public long getViewDetailsId() { return viewDetailsId;}

    public void setViewDetailsId(long viewDetailsId) {this.viewDetailsId = viewDetailsId;}

    public long getAlternativeVotedId() {return alternativeVotedId;}

    public float getPledgeValue() {return pledgeValue;}

    public void setPledgeValue(float pledgeValue) {this.pledgeValue = pledgeValue;}

    public void setAlternativeVotedId(long alternativeVotedId) {
        this.alternativeVotedId = alternativeVotedId;
    }

    public ArrayList<String> getOldWebsocketMessages() { return oldWebsocketMessages;    }

    public void addOldWebsocketMessages(String oldmsg) { this.oldWebsocketMessages.add(oldmsg); }


    public int checkLogin() throws RemoteException  {
        return this.server.checkLogin(this.username,this.password);
    }

    public String checkSignUp(String username,String password,String bi, int age, String email) throws RemoteException  {
        return this.server.checkSignUp(username,password,bi,age,email);
    }

    public boolean addUser(String username,String password,String bi,int age, String email) throws RemoteException
    {
        return this.server.addUser(username,password,bi,age,email);
    }

    public String listProjects(int current) throws RemoteException
    {
        return this.server.listProjects(current);
    }

    public long checkAccountBalance() throws RemoteException
    {
        return this.server.checkAccountBalance(username,tumblrUser);
    }

    public String checkRewards() throws RemoteException
    {
        return this.server.checkRewards(username,tumblrUser);
    }

    public String getNumberProjects() throws RemoteException
    {

        return this.server.getNumberProjects();
    }

    public String showAdminProjects() throws RemoteException
    {
        getAdminProjectIds();
        return this.server.showAdminProjects(username,tumblrUser);
    }
    public String getAdminProjectIds() throws RemoteException
    {
        return this.server.getAdminProjectIds(username,tumblrUser);
    }

    public String addRewards(Long id, Reward r, String username)throws RemoteException
    {
        return  this.server.addReward(id, r,username,tumblrUser);
    }

    public String viewProject() throws RemoteException
    {
        return this.server.viewProject(viewDetailsId);
    }

    public String getAlternativeIdsProject() throws RemoteException
    {
        return this.server.getAlternativeIdsProject(viewDetailsId);
    }

    public String contributeToProject() throws RemoteException
    {
        return this.server.contributeToProject(viewDetailsId,tumblrUser,username,pledgeValue,alternativeVotedId);
    }

    public String addProject(String name, String description, String limitDate, long targetValue, String enterprise, ArrayList<Reward> rewards, ArrayList<Alternative> alternatives) throws  RemoteException {
        return this.server.addProject2(username, tumblrUser ,name, description, limitDate, targetValue, enterprise, rewards, alternatives);

    }

    public String listRewards() throws RemoteException
    {
        String s[]=this.getIdSelected().split(" ");
        Long l=Long.parseLong(s[2]);
       return this.server.listRewardsProject(l);
    }
    public String removeRewards(long idProject,long idReward,String username) throws RemoteException
    {
        return this.server.removeReward(idProject,idReward,username,tumblrUser);
    }

    public String cancelProject(long idProject) throws RemoteException
    {
        return this.server.cancelProject(idProject,username,tumblrUser);
    }

    public String commentProject(String comment) throws RemoteException
    {
        return this.server.commentProject(this.viewDetailsId,tumblrUser,this.username,comment);
    }


    public ArrayList<String> showCommentsProject() throws RemoteException
    {
        return this.server.showCommentsProject2(viewDetailsId,0);
    }

    public ArrayList<String> showCommentsProjectAdmin() throws RemoteException
    {
        String s[]=this.getIdSelected().split(" ");
        Long l=Long.parseLong(s[2]);
        return this.server.showCommentsProject2(l,0);
    }


    public String getMessagesProjectIds() throws RemoteException
    {
        String s[]=this.getIdSelected().split(" ");
        Long l=Long.parseLong(s[2]);
        return this.server.getMessagesProjectIds(l);
    }

    public String replyMessage(String reply, long messageSelected) throws RemoteException
    {
        String s[]=this.getIdSelected().split(" ");
        Long l=Long.parseLong(s[2]);
        return this.server.replyMessage(l,username,messageSelected,reply,tumblrUser);
    }

    public String getRewardsProjectIds() throws RemoteException
    {
        String s[]=this.idSelected.split(" ");
        Long l=Long.parseLong(s[2]);
        return this.server.getRewardsProjectIds(l);
    }

    public String getProjectAdmin(long id) throws RemoteException {
       return this.server.getProjectAdmin(id);

    }

    public  long getProjectValue(long id)throws RemoteException{
        return this.server.getCurrentValue(id);
    }

    public boolean addTumblrUser(String username, String secret, String userToken) throws RemoteException
    {
        return this.server.addTumblrUser(username,secret,userToken);
    }

    public boolean checkTumblrAccount(String username) throws RemoteException
    {
        return this.server.checkTumblrAccount(username);
    }

    public ArrayList<String> getAccessToken(String username,int tumblrUser) throws RemoteException
    {
        return this.server.getAccessToken(username,tumblrUser);
    }

    public String associateAccount(String username,int tumblrUser,String tumblrUsername,String secret, String userToken) throws RemoteException
    {
        return this.server.associateAccount(username,tumblrUser,tumblrUsername,secret,userToken);
    }

    public String checkAssociated(String tumblerUsername) throws RemoteException
    {
        return this.server.checkAssociated(tumblerUsername);
    }

    public boolean updateAccessToken(String secret,String userToken,String oldSecret,String oldUserToken) throws RemoteException
    {
        return this.server.updateAccessToken(secret,userToken,oldSecret,oldUserToken);
    }

    public boolean updateAccessToken(String secret,String userToken,String username,int tumblrUser) throws RemoteException
    {
        return this.server.updateAccessToken(secret,userToken,username,tumblrUser);
    }

    public boolean setPostId(String projectName,String postId,String baseHostName) throws RemoteException
    {
        return this.server.setPostId(projectName,postId,baseHostName);
    }

    public boolean isAssociatedAccount(String username,int tumblrUser) throws RemoteException
    {
        return this.server.isAssociatedAccount(username,tumblrUser);
    }


    public String getPostIdTumblr(long id) throws RemoteException
    {
        return this.server.getPostId(id);
    }

    public String getBaseHostName(long idProject) throws RemoteException
    {
        return this.server.getBaseHostName(idProject);
    }

}
