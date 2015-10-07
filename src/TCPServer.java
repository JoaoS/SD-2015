import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.Properties;

/**
 * Created by joaosubtil on 07/10/15.
 */
public class TCPServer {

    private static int maxDropHeartbeats =5;
    public  static int WAIT=500; //milisseconds response thread wait
    private static int udpPort;

    private static String firstIP;
    private static String secondIP;



    public static void main(String args[]){




        //read java properties
        Properties prop = new Properties();
        InputStream input = null;

        try {

            input = new FileInputStream("tcpProp.properties");
            // load a properties file
            prop.load(input);

            // get the property value and print it out
           /* clientPort=Integer.parseInt(prop.getProperty("clientPort"));

            rmiPort=Integer.parseInt(prop.getProperty("rmiPort"));
            rmiName=prop.getProperty("rmiName");
            rmiIp=prop.getProperty("rmiIp");*/
            udpPort=Integer.parseInt(prop.getProperty("udpPort"));
            firstIP=prop.getProperty("firstIP");
            secondIP=prop.getProperty("secondIP");

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





        try{
            System.out.println("teste "+InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        //attempt
        secundaryServer();


    }

    public static void secundaryServer()
    {

        DatagramSocket aSocket = null;

        try {
            System.out.println(" Connecting as Secundary Server");
            int tries=maxDropHeartbeats;
            byte [] m=new byte[1000];
            aSocket = new DatagramSocket();


            //check my address an ping the other
            String myIP=InetAddress.getLocalHost().getHostAddress();
            String connectIP=myIP.equalsIgnoreCase(firstIP) ? secondIP : firstIP;
            //System.out.println("vou ligar a "+connectIP);


            InetAddress aHost = InetAddress.getByName(connectIP);
            System.out.println("Pinging "+connectIP+" in the port "+udpPort);
            DatagramPacket request = new DatagramPacket(m,m.length,aHost,udpPort);
            // System.out.println(" "+request);
            while(tries >0)
            {
                aSocket.send(request);
                aSocket.setSoTimeout(WAIT*2);
                //waits to receive
                try{
                    byte [] buffer = new byte[1000];
                    DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
                    aSocket.receive(reply);
                    //System.out.println("Received: " + new String(reply.getData(), 0, reply.getLength()));
                    tries=5;
                    Thread.sleep(WAIT);
                }catch(SocketTimeoutException tme){
                    //System.err.println(tme);
                    System.out.println("Primary not found,trying again");
                    tries--;
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }catch (IOException e1) {
            e1.printStackTrace();
        }finally{
            if(aSocket != null){
                aSocket.close();
            }
        }

    }












}
