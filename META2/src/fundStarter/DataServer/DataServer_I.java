package fundStarter.DataServer;

import fundStarter.commons.Alternative;
import fundStarter.commons.Reward;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface DataServer_I extends Remote {
    public void connectDb() throws java.rmi.RemoteException, InstantiationException, IllegalAccessException;
    public int dummyMethod() throws RemoteException;
    public int checkLogin(String username,String password) throws RemoteException;
    public String checkSignUp(String username,String password,String bi, int age, String email) throws RemoteException;
    public boolean addUser(String username,String password,String bi,int age, String email) throws RemoteException;
    public long checkAccountBalance(String userName) throws RemoteException;
    public boolean addProject(String username, String name, String description, String limitDate, long targetValue, String enterprise, ArrayList<Reward> rewards, ArrayList<Alternative> alternatives) throws RemoteException;
    public String checkProject(String name) throws RemoteException;
    public String listProjects(int current) throws RemoteException;
    public String viewProject(long idProject) throws RemoteException;
    public String contributeToProject(long idProject,String userName,float pledgeValue,long alternativeChoosen) throws RemoteException;
    public String checkRewards(String username) throws RemoteException;
    public String commentProject(long idProject,String username,String comment) throws RemoteException;
    public String showCommentsProject(long idProject,int mode) throws RemoteException;
    public String showAdminProjects(String username) throws RemoteException;
    public String addReward(long idProject,Reward r,String username) throws RemoteException;
    public String listRewardsProject(long idProject) throws RemoteException;
    public String removeReward(long idProject,long idReward,String username) throws RemoteException;
    public String cancelProject(long idProject) throws RemoteException;
    public String replyMessage(long idProject,String username ,long idMessage, String reply) throws RemoteException;
    public boolean checkProjectsDate() throws RemoteException;
    public String cancelProject(long idProject,String username) throws RemoteException;
    public String getAdminProjectIds(String username,int tumblrUser) throws RemoteException;
    public String getAlternativeIdsProject(long idProject) throws RemoteException;
    public ArrayList<String> showCommentsProject2(long idProject,int mode) throws RemoteException;
    public String getMessagesProjectIds(long idProject) throws RemoteException;
    public String getNumberProjects() throws RemoteException;
    public String getRewardsProjectIds(long idProject) throws RemoteException;
    public String getProjectAdmin(long idProject) throws RemoteException;
    public long getCurrentValue(long idProject) throws RemoteException;
    public boolean addTumblrUser(String username, String secret, String userToken) throws RemoteException;
    public long checkAccountBalance(String username,int tumblrUser) throws RemoteException;
    public String checkRewards(String username,int tumblrUser) throws RemoteException;
    public String addProject2(String username,int tumblrUser,String name, String description, String limitDate, long targetValue, String enterprise, ArrayList<Reward> rewards, ArrayList<Alternative> alternatives) throws RemoteException;
    public String contributeToProject(long idProject,int tumblrUser,String username,float pledgeValue,long alternativeChoosen) throws RemoteException;
    public String commentProject(long idProject,int tumblrUser,String username,String comment) throws RemoteException;
    public String showAdminProjects(String username, int tumblrUser) throws RemoteException;
    public String addReward(long idProject,Reward r,String username,int tumblrUser) throws RemoteException;
    public String removeReward(long idProject,long idReward,String username,int tumblrUser) throws RemoteException;
    public String cancelProject(long idProject,String username,int tumblrUser) throws RemoteException;
    public String replyMessage(long idProject,String username ,long idMessage, String reply,int tumblrUser) throws RemoteException;
    public boolean checkTumblrAccount(String username) throws  RemoteException;
    public ArrayList<String> getAccessToken(String username,int tumblrUser) throws RemoteException;
    public String associateAccount(String username,int tumblrUser,String tumblrUsername,String secret, String userToken) throws RemoteException;
    public String checkAssociated(String tumblrUsername) throws RemoteException;
    public boolean updateAccessToken(String secret,String userToken,String oldSecret, String oldUserToken) throws RemoteException;
    public boolean updateAccessToken(String secret,String userToken,String username,int tumblrUser) throws RemoteException;
    public boolean setPostId(String projectName,String postId) throws RemoteException;
    public String getPostId(String projectName) throws RemoteException;
    public boolean isAssociatedAccount(String username,int tumblrUser) throws RemoteException;
}
