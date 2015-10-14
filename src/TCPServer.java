import java.io.*;
import java.net.*;
import java.rmi.RMISecurityManager;
import java.rmi.registry.LocateRegistry;
import java.util.Properties;

/**
 * Created by joaosubtil on 07/10/15.
 */
public class TCPServer {

    private static  boolean DEBUG = false ;
    public static 	DataServer_I DataServer_Interface;

    public static 	int reconnection;
    private static int maxDropHeartbeats;
    public  static int WAIT; //milisseconds response thread wait
    private static int udpPort;
    private static int clientPort;
    private static int rmiPort;

    private static String rmiName;
    private static String rmiIp;
    private static String firstIP;
    private static String secondIP;



    public static void main(String args[]) {


        //read java properties
        Properties prop = new Properties();
        InputStream input = null;

        try {

            input = new FileInputStream("tcpProp.properties");
            // load a properties file
            prop.load(input);
            // get the property value and print it out
            reconnection =Integer.parseInt(prop.getProperty("reconnection"));
            rmiPort=Integer.parseInt(prop.getProperty("rmiPort"));
            rmiName=prop.getProperty("rmiName");
            rmiIp=prop.getProperty("rmiIp");
            clientPort=Integer.parseInt(prop.getProperty("clientPort"));
            WAIT=Integer.parseInt(prop.getProperty("WAIT"));
            maxDropHeartbeats=Integer.parseInt(prop.getProperty("maxDropHeartbeats"));
            udpPort = Integer.parseInt(prop.getProperty("udpPort"));
            firstIP = prop.getProperty("firstIP");
            secondIP = prop.getProperty("secondIP");

        } catch (Exception ex) {
            if(DEBUG)
                ex.printStackTrace();

            System.out.println("Error reading properties file, default values will be set");
            rmiPort=5000;
            rmiName= "DataServer";
            rmiIp= "localhost";
            clientPort = 6000;
            WAIT = 500;
            maxDropHeartbeats = 10;
            firstIP= "169.254.15.178";
            secondIP= "169.254.107.114";
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    if(DEBUG)
                        e.printStackTrace();
                }
            }
        }
        //-----------------establish connection with RMI-----------------
        try
        {
            System.getProperties().put("java.security.policy", "security.policy");
            System.setSecurityManager(new RMISecurityManager());
            DataServer_Interface = (DataServer_I)LocateRegistry.getRegistry(rmiPort).lookup(rmiName);
        } catch (Exception e){
            if(DEBUG)
                e.printStackTrace();
            System.out.println("Error establishing connection with RMI.");

        }
        //---------------------------------------------------------------------------


        //attempt to connect to primary if it exists
        secundaryServer();
        //if not create thread to be primary and respond
        new PrimaryThread(udpPort).start();

        //accept incoming connections from client
        int connectedUsers = 0;
        try {

            System.out.println("Listenning for clients on port:" + clientPort);
            ServerSocket listenSocket = new ServerSocket(clientPort);
            System.out.println("LISTEN SOCKET=" + listenSocket);
            while (true) {
                Socket clientSocket = listenSocket.accept(); // BLOQUEANTE
                System.out.println("CLIENT_SOCKET (created at accept())=" + clientSocket);
                connectedUsers++;
                new Connection(clientSocket, connectedUsers,DataServer_Interface);
            }
        } catch (Exception e) {
            if(DEBUG)
                e.printStackTrace();
            System.out.println("Error connecting new client:" + e.getMessage());
        }

    }

    public static void secundaryServer() {
        int replyNumber = 0;
        DatagramSocket aSocket = null;

        try {
            System.out.println("Connecting as Secundary Server");
            int tries = maxDropHeartbeats;

            //check my address an ping the other
            String myIP = InetAddress.getLocalHost().getHostAddress();
            String connectIP = myIP.equalsIgnoreCase(firstIP) ? secondIP : firstIP;
            System.out.println("Connecting to " + connectIP);

            byte[] m = new byte[1000];
            aSocket = new DatagramSocket();
            InetAddress aHost = InetAddress.getByName(connectIP);
            System.out.println("Pinging " + connectIP + " in the port:" + udpPort);
            DatagramPacket request = new DatagramPacket(m, m.length, aHost, udpPort);
            // System.out.println(" "+request);
            while (tries > 0) {
                aSocket.send(request);
                aSocket.setSoTimeout(WAIT * 2); //timeout between each request
                //waits to receive
                try {
                    byte[] buffer = new byte[1000];
                    DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
                    aSocket.receive(reply);
                    System.out.println("Received Reply: " + replyNumber);
                    replyNumber++;
                    tries = maxDropHeartbeats;
                    Thread.sleep(WAIT);
                } catch (SocketTimeoutException tme) {
                    //System.err.println(tme);
                    System.out.println("Primary not found,trying again(remaining tries:" + tries + ", time interval=" + WAIT + "ms)");
                    tries--;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    System.out.println("Secundary server function");
                }
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        } finally {
            if (aSocket != null) {
                aSocket.close();
            }
        }

    }

    static class PrimaryThread extends Thread {//will handle backup as primaryTCP

        public int serverPort;

        PrimaryThread(int s) {
            serverPort = s;
        }

        public void run() {
            System.out.println("Primary server in port: " + serverPort);
            DatagramSocket aSocket = null;
            String s;
            try {
                aSocket = new DatagramSocket(serverPort);
                byte[] buffer = new byte[1000];
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                while (true)//receive packet and respond
                {
                    aSocket.receive(request);
                    DatagramPacket reply = new DatagramPacket(request.getData(), request.getLength(), request.getAddress(), request.getPort());
                    aSocket.send(reply);
                }
            } catch (SocketException e) {
                if(DEBUG)
                    e.printStackTrace();
                System.out.println("Socket: " + e.getMessage());

            } catch (IOException e) {
                if(DEBUG)
                    e.printStackTrace();
                System.out.println("IO: " + e.getMessage());
            } finally {
                if (aSocket != null) aSocket.close();
            }
        }
    }


//--------------Class to handle each client-------------------------------------------------------
    static class Connection extends Thread {

    public static 	DataServer_I DataServer_Interface;
    public DataInputStream in;
    public DataOutputStream out;
    public Socket clientSocket;
    public int thread_number;//number of currently connected client
    public ObjectInputStream objIn;
    public ObjectOutputStream objOut;


    public Connection(Socket aClientSocket, int numero, DataServer_I ds) {

        thread_number = numero;
        DataServer_Interface=ds;
        try {
            clientSocket = aClientSocket;
            in = new DataInputStream(clientSocket.getInputStream());
            out = new DataOutputStream(clientSocket.getOutputStream());

           // objIn = new ObjectInputStream(in);
            //objOut = new ObjectOutputStream(out);

            this.start();
        } catch (IOException e) {
            if(DEBUG)
                e.printStackTrace();
            System.out.println("Connection:" + e.getMessage());
        }
    }



     public void restartRmi() throws IOException
        {
            int tries = TCPServer.reconnection;
            while(tries!=0)
            {
                try
                {
                    Thread.sleep(TCPServer.WAIT);
                }catch (InterruptedException e){
                    if(DEBUG)
                        e.printStackTrace();
                }
                try
                {
                    /*System.getProperties().put("java.security.policy", "security.policy");
                    System.setSecurityManager(new RMISecurityManager());
                    */
                    DataServer_Interface = (DataServer_I)LocateRegistry.getRegistry(TCPServer.rmiPort).lookup(TCPServer.rmiName);
                    if(DataServer_Interface.dummyMethod()==0)
                    {
                        System.out.println("RMI back online....");
                        break;
                    }
                }catch (Exception e){
                    if(DEBUG)
                        e.printStackTrace();

                    System.out.println("Exception in restartRmi, dataserver not online yet...");
                }
                tries--;
            }
            if(tries==0){
                // out.writeUTF("EXIT");//shuts down client   <----------------------------------------------------------
                System.out.println("RMI disconnected, shuting down...");
                System.exit(0);
            }
        }
    //=============================

    public void run() {


        try {

            int i=0;
            while (true){

               out.writeUTF("teste nr "+i);
                i++;


                System.out.println(""+in.readUTF());

            }
        } catch (IOException e) {
            if(DEBUG)
                e.printStackTrace();
            System.out.println("client disconnected");
        }
    }




    }








}