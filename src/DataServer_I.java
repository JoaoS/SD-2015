import java.rmi.*;

public interface DataServer_I extends Remote {
  public void connectDb() throws java.rmi.RemoteException, InstantiationException, IllegalAccessException;
  public int dummyMethod() throws RemoteException;
}
