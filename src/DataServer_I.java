import java.rmi.*;

public interface DataServer_I extends Remote {
    public void connectDb() throws java.rmi.RemoteException, InstantiationException, IllegalAccessException;
    public int dummyMethod() throws RemoteException;
    public int checkLogin(String username,String password) throws RemoteException;
    public String checkSignUp(String username,String password,String bi, int age, String email) throws RemoteException;
    public boolean addUser(String username,String password,String bi,int age, String email) throws RemoteException;
    public long checkAccountBalance(String userName) throws RemoteException;
}
