/**
 * Created by joaosubtil on 09/10/15.
 */

import java.io.*;
import java.net.Socket;
import java.util.Properties;

public class Client {

    public static boolean DEBUG=true;


    public static   int clientPort;
    public static 	int reconnection;
    public static   int threadWait =1000;
    public static 	String firstIP;
    public static 	String secondIP;
    public static   SendToServer th;
    public static   DataInputStream in;
    public static   DataOutputStream out;

    //public static   ObjectInputStream  objIn;


    public static void main(String args[]){
        
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


        try {
            System.out.println("ligado");
            s = new Socket(firstIP,clientPort);
        } catch (IOException e) {
            tries--;
        }
    /*/
        System.out.println("Possible connections:"+reconnection);
        int tries=reconnection*2;
        while(tries !=0){

            System.out.println("tries="+tries);
            Socket sock=null;


                if (sock==null){
                    try {
                        sock = new Socket(firstIP,clientPort);
                        tries=reconnection;
                    } catch (IOException e) {
                        tries--;
                    }

                }
                else if(sock==null){
                    try {
                        sock = new Socket(secondIP,clientPort);
                        tries=reconnection;
                    } catch (IOException e) {
                        tries--;
                    }
                }
                if (sock!=null){
                    try {
                        createChannels(sock);

                    }catch (Exception e){

                        try {
                            System.out.println("waiting for thread");
                            th.join();
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
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

/**/

    }

    public static void createChannels(Socket sock) throws Exception {


        DataInputStream inS=new DataInputStream(sock.getInputStream());
        th=new SendToServer();
        th.setSocket(sock);
        th.start();

        while (true) {

            String data = inS.readUTF();
            System.out.println("->"+data);

        }
    }
}

class SendToServer extends Thread{

    public static Socket s;
    public static ObjectOutputStream objOut=null;

    SendToServer(){}
    public void setSocket(Socket s) { this.s = s; }
    public void closeSocket() throws IOException { this.s.close();  }

    public void run()
    {

        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);
        DataOutputStream out = null;


        try {
            out = new DataOutputStream(s.getOutputStream());
            //objOut = new ObjectOutputStream(out);

        } catch (IOException e) {
            e.printStackTrace();
        }

        while(true){

            String texto;
            try {
                texto = reader.readLine();
                out.writeUTF(texto);
                System.out.println("texto lido:"+texto);

            }catch(Exception e)	{

                System.out.println("Excepção ao enviar");
                e.printStackTrace();

                try {
                    s.close();
                    reader.close();
                    input.close();
                    break;
                } catch (IOException e1) {
                    e1.printStackTrace();
                    System.out.println("erro ao fechar o socket");
                }
            }
        }
    }







}
