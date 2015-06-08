package edu.buffalo.cse.cse486586.groupmessenger2;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.SocketTimeoutException;
import java.util.NoSuchElementException;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Created by Nikhil on 3/1/15.
 */

class SendTask extends AsyncTask<Object, Void, Void> {

    SuggestedSequenceNumber[] suggestions = new SuggestedSequenceNumber[5];
    //OrderQ totalOrder;
    PriorityBlockingQueue<MessageObject> totalOrder;
    int c = 0;
    Sockets bMulitcast;

    /**
     * Will send messageObjects to other AVDs and receive SuggestedSequenceNumber objects
     * and calculate the optimal suggested number and then again send out the final number.
     * @param objects : MessageObject and GroupMessengerActivity
     * @return : Void & null
     */
    protected Void doInBackground(Object... objects) {

        //MessageObject mo = (MessageObject)objects[0];
        GroupMessengerActivity gma = (GroupMessengerActivity)objects[1];

        totalOrder = gma.totalOrder;
        bMulitcast = new Sockets();

        //GroupMessengerActivity.this;
        bMulitcast.sendMessage(objects[0]);

        Thread[] senders = new Thread[5];
        for(int i = 0;i < 5;i++) {
            try {
               /*SuggestionReceiverTask srt = new SuggestionReceiverTask();
                srt.delegate = this;
                srt.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, bMulitcast.unicastSockets[i]).get(1000, TimeUnit.MILLISECONDS);*/

                bMulitcast.unicastSockets[i].setSoTimeout(4500);
                senders[i] = new Thread(new SenderThreads(this, i));
                senders[i].start();
            }
            catch(Exception e){ Log.d(SendTask.class.getSimpleName(), "General exception"); }
        }

        for(Thread t : senders) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        int i = 0;
        while(suggestions[i] == null && i < 5)
            i++;

        //MessageObject modifying = totalOrder.findInQ(suggestions[i].mID, suggestions[i].processNum);

        MessageObject modifying = new MessageObject("", 0, 0);
        for(MessageObject temp : totalOrder){
            if(temp.mID == suggestions[i].mID && temp.processNum == suggestions[i].processNum) {

                modifying = temp;
                totalOrder.remove(temp);
                //totalOrder.offer(temp);
                break;
            }
        }

        if(modifying == null)
            throw new NoSuchElementException();

        for (i = 0; i < 5; i++) {

            if(suggestions[i] == null)
                continue;
            else if(modifying.sSN < suggestions[i].suggestedNumber){
                modifying.sSN = suggestions[i].suggestedNumber;
                modifying.sSNProcNum = suggestions[i].sProcessNum;
            }
            else if(modifying.sSN == suggestions[i].suggestedNumber && modifying.sSNProcNum > suggestions[i].sProcessNum)
                modifying.sSNProcNum = suggestions[i].sProcessNum;
            else
                continue;       //Possible source of error;
        }

        Log.d(SendTask.class.getSimpleName(), "Adding");
        //totalOrder.addQ(modifying);
        totalOrder.offer(modifying);
        //gma.updateSeqJ(modifying.sSN); Remove completely

        Object msg = new FinalMessage(modifying.mID, modifying.processNum, modifying.sSN, modifying.sSNProcNum);
        bMulitcast.sendMessage(msg);

        for(i = 0;i < 5;i++) {
            try {
                bMulitcast.unicastSockets[i].close();
            } catch (IOException e) {
                Log.d(SendTask.class.getSimpleName(), "Error in closing Sockets");
            }
        }

        return null;
    }
}