package edu.buffalo.cse.cse486586.groupmessenger2;

import android.util.Log;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by Nikhil on 2/28/15.
 */

public final class Sockets {

    public Socket[] unicastSockets = new Socket[5];
    private ObjectOutputStream[] out = new ObjectOutputStream[5];

    public Sockets(){

        String[] ports = new String[]{"11108", "11112", "11116", "11120", "11124"};

        for(int i = 0;i < 5;i++){
            try {
                unicastSockets[i] = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                        Integer.parseInt(ports[i]));
                unicastSockets[i].setSoTimeout(500);
                out[i] = new ObjectOutputStream(unicastSockets[i].getOutputStream());
            }
            catch(IOException e){
                Log.e(Sockets.class.getSimpleName(), "Sockets exception " + ports[i]);
            }
            catch(Exception e){
                Log.e(Sockets.class.getSimpleName(), "Unidentified Exception");
            }
        }
    }

    public void sendMessage(Object msg){

        for(int i = 0;i < 5;i++) {

            try {
                out[i].writeObject(msg);
            }
            catch(IOException ioe){
                Log.e(Sockets.class.getSimpleName(), "IOException");
            }
            catch(Exception e) {
                Log.e(Sockets.class.getSimpleName(), "SendMessage Sockets unidentified exception");
            }
        }

        return;
    }
}