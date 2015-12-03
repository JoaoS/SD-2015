package fundStarter.model;
import fundStarter.DataServer.*;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;


public class FundStarterBean {

    private DataServer_I server;
    private String username;
    private String password;


    public FundStarterBean() {
        try {

            server = (DataServer_I) Naming.lookup("DataServer");
            //System.setSecurityManager(new RMISecurityManager());
            //server = (DataServer_I) LocateRegistry.getRegistry("localhost",5000).lookup("DataServer");
            System.out.println("DEPOIS DO SERVER !");
        }
        catch(NotBoundException |RemoteException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
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
