import java.io.*;
import java.net.Socket;
import java.util.Properties;

/**
 * Created by joaosubtil on 12/10/15.
 */
//public class Client2 {
//
//    public static boolean DEBUG=true;
//    public static int clientPort;
//    public static int reconnection;
//    public static String firstIP;
//    public static String secondIP;
//    public static Socket s = null;
//    public static ObjectOutputStream objOut=null;
//    public static ObjectInputStream  objIn=null;
//
//    public static void main(String args[])  {
//
//        //read properties file
//        Properties prop = new Properties();
//        InputStream input = null;
//        try {
//            input = new FileInputStream("tcpProp.properties");
//            prop.load(input);
//            firstIP = prop.getProperty("firstIP");
//            secondIP = prop.getProperty("secondIP");
//            reconnection = Integer.parseInt(prop.getProperty("reconnection"));
//            clientPort = Integer.parseInt(prop.getProperty("clientPort"));
//        } catch (IOException ex) {
//            ex.printStackTrace();
//            System.out.println("Error reading properties file Client.java");
//        } finally {
//            if (input != null) {
//                try {
//                    input.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//
//
//        try{
//            //create connections
//            s = new Socket(firstIP, clientPort);
//            System.out.println("Listenning for clients on port:" + clientPort +" to ip="+clientPort);
//
//            DataInputStream in = new DataInputStream( s.getInputStream());
//            DataOutputStream out = new DataOutputStream( s.getOutputStream());
//            // Criar ObjecOutputStream e ObjectInputStream
//            objOut = new ObjectOutputStream(out);
//            objIn = new ObjectInputStream(in);
//
//        }catch (Exception e){
//                if(DEBUG)
//                    e.printStackTrace();
//        }
//
//
//
//        Message m=new Message();
//        m.setMensagem("vai tomar um banho seu viado");
//        try {
//            objOut.writeObject(m);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//
//    }
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//    }
