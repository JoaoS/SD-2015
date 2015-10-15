/**
 * Created by joaosubtil on 09/10/15.
 */

import java.net.*;
import java.io.*;
import java.util.*;

public class Client {

    public static boolean DEBUG=true;


    public static   int clientPort;
    public static 	int reconnection;
    public static 	String firstIP;
    public static 	String secondIP;
    public static 	Socket sock = null;
    public static   SendToServer th;
    public static   int threadWait;
    public static   ObjectOutputStream objOut=null;
    public static   ObjectInputStream  objIn=null;


    public static void main(String args[]) {
        
        //read properties file
        Properties prop = new Properties();
        InputStream input = null;
        try {
            input = new FileInputStream("tcpProp.properties");
            prop.load(input);
            firstIP=prop.getProperty("firstIP");
            secondIP=prop.getProperty("secondIP");
            reconnection =Integer.parseInt(prop.getProperty("reconnection"));
            clientPort=Integer.parseInt(prop.getProperty("clientPort"));
            threadWait = Integer.parseInt(prop.getProperty("threadWait"));
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("Error reading properties file at Client.");
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        int tries=reconnection*2;
        while(tries !=0){

            if (sock==null){
                try {
                    sock = new Socket(firstIP,clientPort);
                    tries=reconnection;
                } catch (IOException e) {
                    tries-=2;
                }

            }
            else if(sock==null){
                try {
                    sock = new Socket(secondIP,clientPort);
                    tries=reconnection;
                } catch (IOException e) {
                    tries-=2;
                }
            }

            if (sock!=null){
                try {
                    System.out.println("channels");
                    createChannels(sock);

                }catch (IOException e){

                    System.out.println("ligação perdida");
                    try {
                        //th.interrupt();
                        th.join();
                        System.out.println("thread killed");

                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    //fechar o socket actual
                    if(sock!=null){
                        try {
                            sock.close();
                        } catch (Exception e2){
                            e2.printStackTrace();
                            System.out.println("cannot close socket");
                        }
                    }
                    try {
                        Thread.sleep(threadWait);
                    }catch(InterruptedException threadex) {
                        System.out.println("Program error(thread), restart please");
                    }
                    tries--;
                }

            }
            else{
                try {
                    Thread.sleep(threadWait);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
        if(tries == 0){
            System.out.println("Server not found, try again later");
            System.exit(0);

        }


    }
    public static void createChannels(Socket sock) throws IOException
    {
        objOut = new ObjectOutputStream(sock.getOutputStream());
        objIn = new ObjectInputStream(sock.getInputStream());
        Guide g = new Guide();
        th=new SendToServer(sock,g,objIn,objOut);
        th.start();
        
        //reading from server
        while (true) {
            Message reply = null;
            try {
                reply = (Message )objIn.readObject();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            synchronized(g)
            {
            	g.getOperations().add(reply.getOperation());
            }
            System.out.println("->"+ reply.getMessage());
        }
    }
}

class SendToServer extends Thread{

    public static Socket sock;
    public static ObjectOutputStream objOut;
    public static ObjectInputStream objIn;
    public static Guide g;
   
    SendToServer(Socket sock,Guide g,ObjectInputStream objIn,ObjectOutputStream objOut) 
    {
    	this.sock = sock;
    	this.g = g;
    	this.objOut = objOut;
    	this.objIn = objIn;
    }
     
    public void setSocket(Socket sock) { this.sock = sock; }
    public void closeSocket() throws IOException { this.sock.close();  }

    public void run()
    {
    	String currentOp = "";
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);



            try 
            {
                while(true)
                {
                    int check = 0;
                    synchronized(g)
                    {
                        if(!g.getOperations().isEmpty())
                        {
                            currentOp = g.getOperations().poll();
                            check = 1;
                        }
                    }
                    if(check != 0)
                    {
                        switch(currentOp)
                        {
                            case "initial menu":
                                initialMenu();
                                break;
                            default:
                                continue;
                        }
                    }
                }

            }catch(Exception e)	{
                //assumir que o server foi abaixo e matar esta thread
                //comer a excepção
                System.out.println("Error in the sender thread.");
                e.printStackTrace();
            	try {
                    sock.close();
                    reader.close();
                    input.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                    System.out.println("erro ao fechar o socket");
                }
        }






    }
    
    
    public void initialMenu()
    {
    	Scanner sc = new Scanner(System.in);
    	int op = sc.nextInt();
    	if(op  ==1)
    	{
    		login();
    	}
    	else
    	{
    		;
    	}
    }
    
    public void login() 
    {
    	Message reply = new Message();
    	Scanner sc = new Scanner(System.in);
    	System.out.println("Username : ");
    	String username = sc.nextLine();
    	reply.setUsername(username);
    	System.out.println("Password : ");
    	String password = sc.nextLine();
    	reply.setPassword(password);
    	reply.setOperation("login");
    	try
    	{
    		objOut.writeObject(reply);
            objOut.flush();
    	}catch(IOException ioe)
    	{
    		System.out.println("Error sending object to server at login.");
    	}
    }

}
