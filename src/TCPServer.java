import java.io.*;
import java.net.*;
import java.util.Properties;
import  java.util.ArrayList;
/**
 * Created by joaosubtil on 07/10/15.
 */
public class TCPServer {

    private static int maxDropHeartbeats = 5;
    public  static int WAIT = 500; //milisseconds response thread wait
    private static int udpPort;
    private static int clientPort;

    private static String firstIP;
    private static String secondIP;

    public static ArrayList<Connection> lista = new ArrayList<Connection>();


    public static void main(String args[]) {


        //read java properties
        Properties prop = new Properties();
        InputStream input = null;

        try {

            input = new FileInputStream("tcpProp.properties");
            // load a properties file
            prop.load(input);

            // get the property value and print it out
            clientPort=Integer.parseInt(prop.getProperty("clientPort"));

            /*rmiPort=Integer.parseInt(prop.getProperty("rmiPort"));
            rmiName=prop.getProperty("rmiName");
            rmiIp=prop.getProperty("rmiIp");*/
            udpPort = Integer.parseInt(prop.getProperty("udpPort"));
            firstIP = prop.getProperty("firstIP");
            secondIP = prop.getProperty("secondIP");

        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("Error reading properties file");
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


        //attempt
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
                lista.add(new Connection(clientSocket, connectedUsers));
            }
        } catch (Exception e) {
            System.out.println("Listen:" + e.getMessage());
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
                aSocket.setSoTimeout(WAIT * 2);
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
                System.out.println("Socket: " + e.getMessage());
            } catch (IOException e) {
                System.out.println("IO: " + e.getMessage());
            } finally {
                if (aSocket != null) aSocket.close();
            }
        }
    }



    static class Connection extends Thread {

    public DataInputStream in;
    public DataOutputStream out;
    public Socket clientSocket;
    public int thread_number;


    public Connection(Socket aClientSocket, int numero) {
        thread_number = numero;
        try {
            clientSocket = aClientSocket;
            in = new DataInputStream(clientSocket.getInputStream());
            out = new DataOutputStream(clientSocket.getOutputStream());
            this.start();
        } catch (IOException e) {
            System.out.println("Connection:" + e.getMessage());
        }
    }

    //=============================
    public void run() {


        try {
            out.writeUTF("menu inicial");

            while (true){
                System.out.println(in.readUTF());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


        /*
        try {
            while (true) {
               // menuInicial();
                System.out.println("menu inicial");

            }
        } catch (EOFException e) {
            System.out.println("Client disconnected :");
        } catch (IOException e) {
            System.out.println("IO:" + e);
        } catch (Exception e) {
            System.out.println("some sort of error");
            e.printStackTrace();
        }


        */
    }




        public void menuInicial() throws IOException
        {

            System.out.println("menu inicial");

        }

    }








}
