package edu.buffalo.cse.cse486586.groupmessenger2;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * GroupMessengerActivity is the main Activity for the assignment.
 * 
 * @author stevko
 *
 */
public class GroupMessengerActivity extends Activity {

    int counterJ = -1;
    public AtomicInteger seqJ = new AtomicInteger(-1);
    public AtomicInteger key = new AtomicInteger(-1);
    String portStr;
    //OrderQ totalOrder = new OrderQ();
    PriorityBlockingQueue<MessageObject> totalOrder = new PriorityBlockingQueue<MessageObject>();
    Object lock = new Object();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_messenger);

        /*
         * TODO: Use the TextView to display your messages. Though there is no grading component
         * on how you display the messages, if you implement it, it'll make your debugging easier.
         */
        TextView tv = (TextView) findViewById(R.id.textView1);
        final EditText et = (EditText) findViewById(R.id.editText1);
        tv.setMovementMethod(new ScrollingMovementMethod());
        
        /*
         * Registers OnPTestClickListener for "button1" in the layout, which is the "PTest" button.
         * OnPTestClickListener demonstrates how to access a ContentProvider.
         */
        findViewById(R.id.button1).setOnClickListener(
                new OnPTestClickListener(tv, getContentResolver()));
        
        /*
         * TODO: You need to register and implement an OnClickListener for the "Send" button.
         * In your implementation you need to get the message from the input box (EditText)
         * and send it to other AVDs.
         */

        TelephonyManager tel = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        portStr = tel.getLine1Number().substring(tel.getLine1Number().length() - 4);
        //final String myPort = String.valueOf((Integer.parseInt(portStr) * 2));

        new Thread(new ServerTask(GroupMessengerActivity.this)).start();    //Accepts connections

        findViewById(R.id.button4).setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        new SendTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                                new MessageObject(et.getText().toString().trim(), ++counterJ, Integer.parseInt(portStr)), GroupMessengerActivity.this);
                        et.setText("");
                    }
                });

        findViewById(R.id.editText1).setOnKeyListener(
                new View.OnKeyListener() {
                    public boolean onKey(View v, int keyCode, KeyEvent event) {

                        if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER)
                            findViewById(R.id.button4).performClick();

                        return false;
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_group_messenger, menu);
        return true;
    }


    public int incrementSeqJ () {
        synchronized (GroupMessengerActivity.class){
            return seqJ.incrementAndGet();
        }

    }

    public void updateSeqJ(int seqJ) {
        synchronized (GroupMessengerActivity.class) {
            if (this.seqJ.get() < seqJ)
                this.seqJ.set(seqJ);
        }
    }

    public synchronized void deliver(){
        while(totalOrder.size() != 0 && totalOrder.peek().status){
            MessageObject mo = totalOrder.remove();
            ContentResolver cr = getContentResolver();
            ContentValues cv = new ContentValues();
            Uri mUri = buildUri("content", "edu.buffalo.cse.cse486586.groupmessenger2.provider");

            String value = mo.message;
            cv.put("key", Integer.toString(key.incrementAndGet()));
            cv.put("value", value.trim());
            cr.insert(mUri, cv);

            //this.remove(temp);
            //Log.d(OrderQ.class.getSimpleName(), "Elements Left: " + this.size());
        }
    }

    private Uri buildUri(String scheme, String authority) {
        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.authority(authority);
        uriBuilder.scheme(scheme);
        return uriBuilder.build();
    }
}