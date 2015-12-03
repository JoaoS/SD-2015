package fundStarter.model;
import fundStarter.DataServer.*;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;


public class FundStarterBean {

    private DataServer_I server;
    private String username;
    private String password;


    public FundStarterBean() {
        try {
            server = (DataServer_I) LocateRegistry.getRegistry("localhost",5000).lookup("DataServer");
        }
        catch(NotBoundException |RemoteException e) {
            e.printStackTrace();
        }
    }


    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int checkLogin() throws RemoteException  {
        return this.server.checkLogin(this.username,this.password);
    }


}
