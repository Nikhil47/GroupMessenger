package edu.buffalo.cse.cse486586.groupmessenger2;

import java.io.Serializable;

/**
 * Created by nikhil on 3/3/15.
 */
public class FinalMessage implements Serializable {

    public int mID;
    public int processNum;
    public int sSN;
    public int sSNProcNum;

    public FinalMessage(int mID, int processNum, int sSN, int sSNProcNum){
        this.mID = mID;
        this.processNum = processNum;
        this.sSN = sSN;
        this.sSNProcNum = sSNProcNum;
    }

    public String toString(){
        return "3";
    }
}
