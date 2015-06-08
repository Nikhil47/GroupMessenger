package edu.buffalo.cse.cse486586.groupmessenger2;

import android.util.Log;

import java.io.ObjectInputStream;
import java.net.SocketTimeoutException;

/**
 * Created by nikhil on 3/9/15.
 */
public class SenderThreads implements Runnable {

    int x;
    SendTask st;

    public SenderThreads(SendTask st, int i) {
        this.x = i;
        this.st = st;
    }

    @Override
    public void run() {
        try {
            ObjectInputStream recvStream = new ObjectInputStream(st.bMulitcast.unicastSockets[x].getInputStream());
            Object recv;

            recv = recvStream.readObject();

            if (Integer.parseInt(recv.toString()) == 2) {
                SuggestedSequenceNumber ssn = (SuggestedSequenceNumber) recv;
                st.suggestions[x] = ssn;
            }
        }
        catch (SocketTimeoutException ste){
            st.suggestions[x] = null;
            //totalOrder.expMIDs[i] = -9;
            Log.d(SenderThreads.class.getSimpleName(), "Socket timed out");
        }
        catch(Exception e){    Log.d(SendTask.class.getSimpleName(), "Thread Read Exception"); }
    }
}
