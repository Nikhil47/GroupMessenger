package edu.buffalo.cse.cse486586.groupmessenger2;

import android.util.Log;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Created by Nikhil on 2/28/15.
 * The main object which will be sent only once by the origin and the rest of the processes
 * will keep it in their Total Order Queue first and then in FIFO Order Queue. responseNo
 * variable is used to keep track of how many processes have given their suggestions.
 **/

 public class MessageObject implements Serializable, Comparable<MessageObject> {
    public String message;  //Contains the message to be sent
    public int mID;         //Contains the process local sequence number (FIFO order)
    public int processNum;  //Contains the process number which created this object
    public int sSN;         //Contains the Suggested Sequence Number
    public int sSNProcNum;  //Contains the process number which suggested the SSN
    public int responseNo;  //Contains the number of AVDs which have responded
    public Boolean status;  //Contains the status: finalized or not
    public Boolean deliver; //Contains the status: Deliverable or Undeliverable

    public MessageObject(String m, int seq, int pn){
        this.message = m;
        this.mID = seq;
        this.processNum = pn;
        this.responseNo = 0;
        this.deliver = false;
    }

    //Methods required to update the sSN, sSNProcNum & status
    public String toString(){  return "1"; }

    /**
     * This method uses the defined metric for ordering the messages in Total as well as FiFo in a priority
     * queue. Valid if the value of ssn doesn't rise above 1000.
     * @param another
     * @return
     */
    @Override
    public int compareTo(MessageObject another) {
        //return Float.valueOf((float)(sSN) + (float) (sSNProcNum % 5554) / 10)
        //        .compareTo((float) (another.sSN) + (float) (another.sSNProcNum % 5554) / 10);

        if(sSN > another.sSN)
            return 1;
        else if(sSN < another.sSN)
            return -1;
        else {
            if(!status && another.status)
                return -1;
            else if(status && !another.status)
                return 1;
            else {
                if(sSNProcNum > another.sSNProcNum)
                    return 1;
                else if(sSNProcNum < another.sSNProcNum)
                    return -1;
                else
                    return 0;
            }
        }

    }

    public boolean equals(MessageObject another){
        return (((float) (sSN) + (float) (sSNProcNum % 5554) / 10) == (float) (another.sSN) + (float) (another.sSNProcNum % 5554) / 10);
    }
}