package edu.buffalo.cse.cse486586.groupmessenger2;

import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Created by Nikhil on 3/6/15.
 */
public class ReceiverTask implements Runnable {

    public Socket socks;
    Object recObj;
    //OrderQ totalOrder;
    GroupMessengerActivity gma;
    MessageObject updateUI, mo;
    PriorityBlockingQueue<MessageObject> totalOrder;

    public ReceiverTask(Socket socks, GroupMessengerActivity gma){
        this.socks = socks;
        this.gma = gma;
    }

    /**
     * Used only and only to update the UI. No touching ContentProviders with this.
     * @param values
     */

     public void publishProgress(Object... values) {

        GroupMessengerActivity gma = (GroupMessengerActivity) values[0];
        MessageObject mo = (MessageObject) values[1];

        TextView tv = (TextView) gma.findViewById(R.id.textView1);
        tv.append(mo.message + "\n");
    }

    @Override
    public void run() {
        try {

            socks.setSoTimeout(4500);
            totalOrder = gma.totalOrder;

            ObjectInputStream ois = new ObjectInputStream(socks.getInputStream());   //Get reading stream
            ObjectOutputStream sendSSN = new ObjectOutputStream(socks.getOutputStream());

            int i = 0;
            while (i < 2) {
                recObj = ois.readObject();
                //Determine the type of message object received and do actions according to toString values. Use Switch cases.
                switch (Integer.parseInt(recObj.toString())) {

                    //Receive MessageObject and send SuggestedSequenceNumber object to sending AVD
                    case 1: {
                        mo = (MessageObject) recObj;
                        updateUI = mo;

                        int seqJ;
                        /*if(mo.processNum == Integer.parseInt(gma.portStr))
                            seqJ = gma.getSeqJ();
                        else*/
                            seqJ = gma.incrementSeqJ();

                        mo.status = false;
                        mo.sSN = seqJ;
                        mo.sSNProcNum = Integer.parseInt(gma.portStr);

                        Log.d(ReceiverTask.class.getSimpleName(), "Case 1 Adding");
                        //totalOrder.addQ(mo);
                        totalOrder.add(mo);

                        Object msg = new SuggestedSequenceNumber(mo.mID, seqJ, mo.processNum, mo.sSNProcNum);
                        sendSSN.writeObject(msg);

                        break;
                    }

                    case 3: {
                        FinalMessage fm = (FinalMessage) recObj;

                        //MessageObject mo = totalOrder.findInQ(fm.mID, fm.processNum);

                        for(MessageObject temp : totalOrder){
                            if(temp.mID == fm.mID && temp.processNum == fm.processNum) {

                                temp.sSN = fm.sSN;
                                temp.sSNProcNum = fm.sSNProcNum;
                                temp.status = true;

                                totalOrder.remove(temp);
                                totalOrder.offer(temp);
                                break;
                            }
                        }

                        /*if (mo == null)
                            throw new NoSuchElementException();
                        else {
                            mo.sSN = fm.sSN;
                            mo.sSNProcNum = fm.sSNProcNum;
                            mo.status = true;
                        }*/

                        gma.updateSeqJ(fm.sSN);

                        Log.d(ReceiverTask.class.getSimpleName(), "Case 2 Adding");
                        //totalOrder.addQ(mo);

                        /*if(totalOrder.size() == 25)
                            totalOrder.makeDeliverable(gma.getContentResolver());
                        */

                        /*while(totalOrder.size() != 0 && totalOrder.peek().status){
                            MessageObject mo = totalOrder.remove();
                            ContentResolver cr = gma.getContentResolver();
                            ContentValues cv = new ContentValues();
                            Uri mUri = buildUri("content", "edu.buffalo.cse.cse486586.groupmessenger2.provider");

                            String value = mo.message;
                            cv.put("key", Integer.toString(gma.key.incrementAndGet()));
                            cv.put("value", value.trim());
                            cr.insert(mUri, cv);

                            //this.remove(temp);
                            //Log.d(OrderQ.class.getSimpleName(), "Elements Left: " + this.size());
                        }*/

                        gma.deliver();
                        gma.runOnUiThread(
                                new Runnable() {
                                    public void run() {
                                        publishProgress(gma, ReceiverTask.this.updateUI);
                                    }
                                });

                        ois.close();
                        sendSSN.close();
                        socks.close();
                        break;
                    }
                }
                i++;
            }
        }catch(SocketTimeoutException ste){
            Log.d(ReceiverTask.class.getSimpleName(), "Socket Timed Out");

            for(MessageObject temp : totalOrder){
                if(temp.mID == mo.mID && temp.processNum == mo.processNum){
                    totalOrder.remove(temp);
                }
            }
        }
        catch(ClassNotFoundException cnfe){ Log.d(ReceiverTask.class.getSimpleName(), "MessageObject not found"); }
        catch(IOException e){ Log.d(ReceiverTask.class.getSimpleName(), e.toString()); }
    }
}