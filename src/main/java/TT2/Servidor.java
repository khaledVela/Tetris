package TT2;import javax.swing.*;
import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

public class Servidor extends JFrame {
    ServerSocket ss;
    HashMap clienteColl = new HashMap();

    public Servidor(int puerto) {
        try {
            ss = new ServerSocket(puerto);
            new ClientAccept().start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class ClientAccept extends Thread {

        public void run() {
            while (true) {
                try {
                    Socket s = ss.accept();
                    String i = new DataInputStream(s.getInputStream()).readUTF();
                    if (clienteColl.containsKey(i)) {
                        DataOutputStream dout = new DataOutputStream(s.getOutputStream());
                        dout.writeUTF("Ya estas registrado");

                    } else {
                        clienteColl.put(i, s);
                        DataOutputStream dout = new DataOutputStream((s.getOutputStream()));
                        dout.writeUTF("");
                        new MsgRead(s, i).start();
                        new PrepareClientList().start();
                    }
                } catch (Exception e) {
                }
            }
        }
    }

    class MsgRead extends Thread {
        Socket s;
        String ID;

        MsgRead(Socket s, String ID) {
            this.s = s;
            this.ID = ID;
        }

        public void run() {
            while (!clienteColl.isEmpty()) {
                try {
                    String i = new DataInputStream(s.getInputStream()).readUTF();
                    System.out.println(i);
                    Set k = clienteColl.keySet();
                    Iterator itr = k.iterator();
                    while (itr.hasNext()) {
                        String key = (String) itr.next();
                        if (!key.equalsIgnoreCase(ID)) {
                            try {
                                new DataOutputStream(((Socket) clienteColl.get(key)).getOutputStream()).writeUTF(i);
                            } catch (Exception e) {
                            }
                        }
                    }
                    if(i.contains("-")){
                        String[] nomb = i.split("-");
                        clienteColl.remove(nomb[1]);
                    }
                } catch (Exception e) {

                }
            }
        }

    }

    class PrepareClientList extends Thread {
        public void run() {
            try {
                String ids = "";
                Set k = clienteColl.keySet();
                Iterator itr = k.iterator();
                while (itr.hasNext()) {
                    String key = (String) itr.next();
                    ids += key + ",";
                }
                if (ids.length() != 0) {
                    ids = ids.substring(0, ids.length() - 1);
                }

                itr = k.iterator();

                while (itr.hasNext()) {
                    String key = (String) itr.next();
                    try {
                        new DataOutputStream(((Socket) clienteColl.get(key)).getOutputStream()).writeUTF(":;.,/=" + ids);
                    } catch (Exception e) {
                        clienteColl.remove(key);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public static void main(String[] args) {
        FileReader in = null;
        try {
            in = new FileReader("src/main/java/Imagen/puertos");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Scanner sc = new Scanner(in);
        int num = sc.nextInt();
        int puerto = sc.nextInt();
        Servidor serv = new Servidor(puerto);
        System.out.println(puerto);
    }
}