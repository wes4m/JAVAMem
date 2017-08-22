package dotnetClient.Connection;

// Imports
import dotnetClient.Main.CommandManager;

public class Client {

    private Sock localSocket;

    public final String bsp = "BSP"; // Begin splitter indicator
    public final String esp = "ESP"; // End   splitter indicator

    private char[] lastFullBuffer;
    private boolean isSplitting = false;

    private final CommandManager commandManager = new CommandManager(this);

    /* Main function */
    public void startClient() {
        localSocket = new Sock(this,"127.0.0.1",556);
        localSocket.setBufferSize(1024);
        localSocket.connect();
    }

    // handle recieved data event from socket
    public void data_recveied(char[] data) {
        // Get buffer
        String bufferString = String.valueOf(data).trim();
        // In splitting process
        if(isSplitting) {
             // end splitting
             if(bufferString.equalsIgnoreCase(esp)) {
                // pass to command manager
                 commandManager.setLastBuff(lastFullBuffer);
                 isSplitting = false;
                 return;
             }

             // Keep adding
             char[] partBuff = new char[data.length + lastFullBuffer.length-1];
             System.arraycopy(lastFullBuffer, 0, partBuff, 0, lastFullBuffer.length);
             System.arraycopy(data, 0, partBuff, lastFullBuffer.length-1, data.length);
             lastFullBuffer = partBuff;
        } else {
            // Not splitting. normal state
            // check splitting
            if(bufferString.equalsIgnoreCase(bsp)) {
                lastFullBuffer = new char[1];
                isSplitting = true;
                return;
            }
            // Pass to commands handler
            commandManager.command(bufferString);
        }
    }

    // handle disonnenction event from socket
    public void disconnected() {
        System.out.println("Disconnected");
        // try reconnecting until connected
        while (!localSocket.connect()) {
            try { Thread.sleep(1500); } catch (Exception ex) { };
            System.out.println("Trying to reconnect.. ");
        }
    }

    public Sock getSocket() {
        return localSocket;
    }
}
