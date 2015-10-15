/**
 * Created by joaosubtil on 09/10/15.
 */

import java.net.*;
import java.io.*;
import java.util.*;

public class Client {

    public static boolean DEBUG=false;


    public static   int clientPort;
    public static 	int reconnection;
    public static 	String firstIP;
    public static 	String secondIP;
    public static 	Socket sock = null;
    public static   SendToServer th;
    public static   int threadWait;
    public static   ObjectOutputStream objOut=null;
    public static   ObjectInputStream  objIn=null;
    public static   Guide guide;
    public static   Message loginData=null;



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

        guide=new Guide();
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
                    createChannels(sock);

                }catch (IOException e){

                    if (DEBUG){
                        System.out.println("Connection lost");
                    }

                    try {

                        //th.interrupt();
                        th.join();
                        if(DEBUG){
                            System.out.println("thread killed");
                        }

                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }

                    if(sock!=null){
                        try {
                            sock.close();
                            sock=null;

                            if(DEBUG){
                                System.out.println("closed socket");
                            }

                        } catch (Exception e2){
                            if(DEBUG){
                                e2.printStackTrace();
                                System.out.println("cannot close socket");
                            }
                        }
                    }

                    try {
                        Thread.sleep(threadWait);
                    }catch(InterruptedException e2) {
                        if (DEBUG){
                            e2.printStackTrace();
                            System.out.println("Program error(thread), restart please");
                        }
                    }
                    tries--;
                }

            }
            //
            try {
                Thread.sleep(threadWait);
            }catch(InterruptedException e2) {
                if (DEBUG){
                    e2.printStackTrace();
                    System.out.println("Program error(thread), restart please");
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
        objOut.flush();
        objIn = new ObjectInputStream(sock.getInputStream());
        th=null;
        th=new SendToServer(sock,objOut);
        th.start();


        if(DEBUG)
             System.out.println("Aqui->Reader thread");

        //reading from server
        while (true) {
            Message reply = null;
            try {
                reply = (Message )objIn.readObject();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            synchronized(guide)
            {
                guide.getOperations().add(reply.getOperation());
            }
            if(!reply.getMessage().equalsIgnoreCase(""))
                System.out.println("Server->"+ reply.getMessage());
        }
    }
}

class SendToServer extends Thread{

    public static Socket sock;
    public static ObjectOutputStream objOut;
    BufferedReader reader;


    SendToServer(Socket sock,ObjectOutputStream objOut){
    	this.sock = sock;
    	this.objOut = objOut;
    }
    public void setSocket(Socket sock) { this.sock = sock; }
    public void closeSocket() throws IOException { this.sock.close();  }

    public void run()
    {
    	String currentOp = "";
        InputStreamReader input = new InputStreamReader(System.in);
        reader = new BufferedReader(input);

        try
        {
            while(true)
            {
                int check = 0;
                synchronized(Client.guide)
                {
                    if(!Client.guide.getOperations().isEmpty())
                    {
                        currentOp = Client.guide.getOperations().poll();
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
        }catch (Exception e){
            if (Client.DEBUG) {
                e.printStackTrace();
                System.out.println("exiting");
            }
        }

        if (Client.DEBUG)
            System.out.println("thread dead");


    }
    
    
    public void initialMenu() throws IOException {
    	int op = Integer.parseInt(reader.readLine());
        System.out.println("op="+op);
        if(op  == 1)//todo testar aqui se a sessao já foi iniciada
    	{
    		login();
    	}
    	else
    	{
    		;
    	}
    }
    
    public void login() throws IOException {

        Message reply = new Message();

        if(Client.loginData!=null){
            reply=Client.loginData;
        }
        else {
            Scanner sc = new Scanner(System.in);
            System.out.println("Username : ");
            String username = sc.nextLine();
            reply.setUsername(username);
            System.out.println("Password : ");
            String password = sc.nextLine();
            reply.setPassword(password);
            reply.setOperation("login");
            Client.loginData=reply;
        }

    	objOut.writeObject(reply);
        objOut.flush();

    }

}
