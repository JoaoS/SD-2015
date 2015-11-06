import java.rmi.*;
import java.util.ArrayList;

public interface DataServer_I extends Remote {
    public void connectDb() throws java.rmi.RemoteException, InstantiationException, IllegalAccessException;
    public int dummyMethod() throws RemoteException;
    public int checkLogin(String username,String password) throws RemoteException;
    public String checkSignUp(String username,String password,String bi, int age, String email) throws RemoteException;
    public boolean addUser(String username,String password,String bi,int age, String email) throws RemoteException;
    public long checkAccountBalance(String userName) throws RemoteException;
    public boolean addProject(String username,String name,String description,String limitDate,long targetValue, String enterprise,ArrayList<Reward> rewards, ArrayList<Alternative> alternatives) throws RemoteException;
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
}