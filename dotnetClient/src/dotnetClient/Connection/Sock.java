package dotnetClient.Connection;

// Imports
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class Sock implements Runnable {

    private Thread recvThread;
    private Client clientClass;

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;


    private int port;
    private String address;

    private int max_buff_size;

    public Sock(Client clientClass, String address, int port) {
            this.address = address;
            this.port = port;
            this.clientClass = clientClass;
            socket = new Socket();
    }

    public void disconnect() {
        try {
            socket.close();
        } catch (Exception ex) {
            // Error
        }
        recvThread.stop();
    }


    public boolean connect() {
        try {
            // Create socket
            socket = new Socket(address, port);
            // Intalize sender/reciver
            out = new PrintWriter(socket.getOutputStream(), true);
            out.flush();
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // recive thread
            recvThread  = new Thread(this,"recvThread");
            recvThread.start();
            return true;
        } catch  (Exception e) {
            return false;
        }
    }

    public boolean send(String data) {
        if (socket.isConnected()) {
            try {
                if (data.length() > max_buff_size) {
                    // Begin split
                     out.write(clientClass.bsp);
                     out.flush();

                     int i;
                     int sent = 0;
                     for(i = 0;
                             i < (data.length() / max_buff_size);
                             i ++) {

                        out.write(data, (i*max_buff_size), max_buff_size);
                        out.flush();
                        sent += max_buff_size;
                     }

                     // leftover bytes
                     if (((double) data.length() / max_buff_size) > i) {
                         out.write(data, sent, (data.length() - sent));
                         out.flush();
                     }

                     // End split
                     out.write(clientClass.esp);
                     out.flush();
                } else { // not large data
                    out.write(data);
                    out.flush();
                }

                return true;
            } catch (Exception ex) {
                // Error
            }
        }
        return false;
    }

    public boolean send(char[] data) {
        return this.send(String.valueOf(data));
    }

    public boolean isConnected() {
        return s.isConnected();
    }

    public void setBufferSize(int max) {
        max_buff_size = max;
    }

    public int bufferSize() {
        return max_buff_size;
    }

    // threading recieve method
    @Override
    public void run() {
        // Reciving loop
        while (true) {
            try {
                // get data
                char[] buff = new char[max_buff_size];
                int size = in.read(buff);
                // fix
                char[] newBuff = new char[size+1];
                System.arraycopy(buff, 0, newBuff, 0, size);
                // free old buffer
                buff = null;

                try {
                    // connection check
                    socket.sendUrgentData(0);
                    // pass data
                    clientClass.data_recveied(newBuff);

                } catch (Exception ex) {
                    // Disconnected
                    clientClass.disconnected();
                    recvThread.stop(); // exit thread
                }
            } catch (Exception ex) {
                // Error
                // Disconnected
                clientClass.disconnected();
                recvThread.stop(); // exit thread
            }
        }
    }
}
