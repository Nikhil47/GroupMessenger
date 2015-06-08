package edu.buffalo.cse.cse486586.groupmessenger2;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.util.Log;

import java.util.LinkedList;

/**
 * Created by nikhil on 3/5/15.
 */
public class OrderQ extends LinkedList<MessageObject> {

    int position = 0;
    public int expMIDs[] = new int[5];    //Contains the expected sequence number
    Uri mUri = buildUri("content", "edu.buffalo.cse.cse486586.groupmessenger2.provider");
    private int key = 0;
    private ContentValues cv = new ContentValues();

    public OrderQ(){
        super();
        setExpMIDs();
    }

    /**
     * Returns the reference of the MessageObject to be modified.
     * http://stackoverflow.com/questions/25197269/does-getint-index-method-of-list-return-a-reference-to-the-original-object-or
     * @param ID pid: Message ID and process ID which created this message
     * @return : MessageObject reference
     */
    public synchronized MessageObject findInQ(int ID, int pid){

        int temp = 0;

        while(temp < this.size()){
            if(this.get(temp).mID == ID && this.get(temp).processNum == pid)
                return this.remove(temp);
            temp++;
        }

        return null;
    }

    /**
     * inserts in sorted order the MessageObjects to the totalOrder linked list
     * @param mo
     */
    public synchronized void addQ(MessageObject mo){

        int temp = 0;
        MessageObject moTemp, exTemp = new MessageObject("", 0, 0);

        if(this.size() == 0) {
            this.addFirst(mo);
            position++;
        }

        else{
            while(temp < this.size() && mo.compareTo(moTemp = this.get(temp)) >= 0){
                if(mo.processNum == moTemp.processNum && mo.mID < moTemp.mID){
                    //get the node and switch its proposed numbers

                    moTemp = this.remove(temp);

                    exTemp.sSN = mo.sSN;
                    exTemp.sSNProcNum = mo.sSNProcNum;

                    mo.sSN = moTemp.sSN;
                    mo.sSNProcNum = moTemp.sSNProcNum;

                    moTemp.sSN = exTemp.sSN;
                    moTemp.sSNProcNum = exTemp.sSNProcNum;

                    this.add(temp, mo);
                    mo = moTemp;
                    Log.d(OrderQ.class.getSimpleName(), "Fifoing");
                }
                if(mo.processNum == moTemp.processNum && mo.mID == moTemp.mID){
                    this.remove(temp);
                    this.add(temp, mo);
                    Log.d(OrderQ.class.getSimpleName(), "Replaced");
                    break;
                }

                Log.d("Element: ", "" + mo.mID + " " + mo.processNum + " " + mo.sSN + " " + mo.sSNProcNum);
                temp++;
            }

            this.add(temp, mo);
            position = temp;        //Holds the position of newly inserted element; chances are that this element will
            //be accessed multiple times. Therefore saving position in a reference.
        }
    }

    public synchronized void makeDeliverable(ContentResolver cr){
        int temp = 0;

        while(temp < this.size()){
            MessageObject mo = this.get(temp);

            //If the process is known to be crashed then make the message deliverable
            if(expMIDs[(mo.processNum % 5554) / 2] == -9 && mo.status)
                mo.deliver = false;

            else if((mo.mID == (expMIDs[(mo.processNum % 5554) / 2] + 1) && mo.status)){
                expMIDs[(mo.processNum % 5554) / 2]++;
                mo.deliver = true;
            }

            if(mo.deliver){
                String value = mo.message;
                cv.put("key", Integer.toString(key));
                cv.put("value", value.trim());
                cr.insert(mUri, cv);
                key++;
                this.remove(temp);
                Log.d(OrderQ.class.getSimpleName(), "Elements Left: " + this.size());
                continue;
            }
            temp++;
        }
    }

    private void setExpMIDs(){
        java.util.Arrays.fill(expMIDs, -1);
    }

    private Uri buildUri(String scheme, String authority) {
        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.authority(authority);
        uriBuilder.scheme(scheme);
        return uriBuilder.build();
    }
}