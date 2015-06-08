package edu.buffalo.cse.cse486586.groupmessenger2;

import java.io.Serializable;

/**
 * Created by nikhil on 3/1/15.
 * This is a temporary object which will be sent as Step 2 of the order determination process.
 */
public class SuggestedSequenceNumber implements Serializable {
    public int mID;
    public int suggestedNumber;
    public int processNum;      //process number which created the original message
    public int sProcessNum;     //Suggesting process' number

    public SuggestedSequenceNumber(int mID, int suggestedNumber, int procNum, int num){
        this.mID = mID;
        this.suggestedNumber = suggestedNumber;
        this.processNum = procNum;
        this.sProcessNum = num;
    }

    public String toString(){
        return new String("2");
    }
}
