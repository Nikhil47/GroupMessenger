package edu.buffalo.cse.cse486586.groupmessenger2;

import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Nikhil on 3/1/15.
 * The purpose of this class is to accept socket connections and spawn new threads to handle
 * the communications. This is to make sure that wasteful socket approach is not used and only
 * one socket is used so that SocketTimeOut Exceptions can be used to monitor AVD stats.
 */

class ServerTask implements Runnable {

    GroupMessengerActivity gma;
    Socket socks;

    public ServerTask(GroupMessengerActivity gma){
        this.gma = gma;
    }

    @Override
    public void run() {
        ServerSocket acceptSkt;

        try {
            acceptSkt = new ServerSocket(10000);

            while (true) {
                socks = acceptSkt.accept();

                new Thread(new ReceiverTask(socks, gma)).start();
            }
        }catch(IOException ioe){ Log.d(ServerTask.class.getSimpleName(), "Error in accepting sockets"); }
    }
}