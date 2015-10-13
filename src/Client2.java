import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Properties;

/**
 * Created by joaosubtil on 12/10/15.
 */
public class Client2 implements Serializable {

    public static boolean DEBUG=true;
    public static int clientPort;
    public static int reconnection;
    public static String firstIP;
    public static String secondIP;
    public static int tries=0;

    public static void main(String args[])  {

        //read properties file
        Properties prop = new Properties();
        InputStream input = null;
        try {
            input = new FileInputStream("tcpProp.properties");
            prop.load(input);
            firstIP = prop.getProperty("firstIP");
            secondIP = prop.getProperty("secondIP");
            reconnection = Integer.parseInt(prop.getProperty("reconnection"));
            clientPort = Integer.parseInt(prop.getProperty("clientPort"));
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("Error reading properties file Client.java");
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        /*/

        tries=reconnection;

        while (tries>0){
            tries--;
        }

        createconnections();

        /*/
        try{
            Socket s=null;
            //create connections
            s = new Socket(firstIP, clientPort);
            System.out.println("Listenning for clients on port:" + clientPort +" to ip="+clientPort);
            DataInputStream in = new DataInputStream( s.getInputStream());
            DataOutputStream out = new DataOutputStream( s.getOutputStream());
            // Criar ObjecOutputStream e ObjectInputStream
            ObjectOutputStream objOut = new ObjectOutputStream(out);
            ObjectInputStream objIn = new ObjectInputStream(in);

            Message m=new Message();
            m.setMensagem("teste2");
            objOut.writeObject(m);
        }

        catch (Exception e){
                if(DEBUG)
                    e.printStackTrace();
        }

        /**/
    }


    public  static void createconnections(){


        //read from  server

        try {
            Socket  s = new Socket(firstIP, clientPort);
            System.out.println("aqui");

            /*/
            DataOutputStream out = new DataOutputStream( s.getOutputStream());
            ObjectOutputStream objOut = new ObjectOutputStream(out);

            Message m=new Message();
            m.setMensagem("teste ao socket de envio");
            objOut.writeObject(m);
           /*/

            //o object input stream precisa sempre que exista um output stream antes porque faz um teste de envio de um byte
            DataInputStream in = new DataInputStream(s.getInputStream());
            ObjectInputStream objIn = new ObjectInputStream(in);
            System.out.println("aqui2");


            while (true){

                try {
                    System.out.println(""+(Message)objIn.readObject());
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

            }
            /**/



        } catch (IOException e) {
            e.printStackTrace();
        }









    }
















    }
