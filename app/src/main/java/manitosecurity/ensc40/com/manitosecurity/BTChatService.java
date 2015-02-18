package manitosecurity.ensc40.com.manitosecurity;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;


/**
 * Created by Collin on 12/4/14.
 */
public class BTChatService extends Service {
    public BTChat mChatService = null;
    private final IBinder mBinder = new LocalBinder();
    private static BTChatService mInstance = null;
    private static final String TAG = "BTCHATService";
    private Intent mintent;

    public static BTChatService getInstance(){
        if(mInstance == null)
        {
            mInstance = new BTChatService();
        }
        return mInstance;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "BTChatService broadcast intent");
        Intent mintent = new Intent(getApplicationContext(), SetUpBlueTooth.class);
        mintent.setAction("manitosecurity.ensc40.GOT_BT");

        if(mChatService == null){
            mChatService = new BTChat(getApplicationContext(), mHandler);
        }
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        //mHandler = ((ManitoApplication) getApplication()).getHandler();
        Log.d(TAG, "BINDING");
        return mBinder;
    }

    public class LocalBinder extends Binder {
        public BTChatService getService() {
            Log.d(TAG, "GETSERVICE");
            return BTChatService.this;
        }
    }

    /**
     * Sends a message.
     *
     * @param message A string of text to send.
     */
    private void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (mChatService.getState() != BTChat.STATE_CONNECTED) {
            Toast.makeText(this, "Not Connected", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BTChat to write
            byte[] send = message.getBytes();
            mChatService.write(send);
        }
    }

    /**
     * The Handler that gets information back from the BTChat
     */
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BTChat.STATE_CONNECTED:
                            Log.d(TAG, "SENDING BROADCAST");
                            sendBroadcast(mintent);
                            break;
                        case BTChat.STATE_CONNECTING:
                            break;
                        case BTChat.STATE_LISTEN:
                        case BTChat.STATE_NONE:
                            break;
                    }
                    break;
                case Constants.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    break;
                case Constants.MESSAGE_TOAST:
                    if (null != this) {
                        Toast.makeText(getApplicationContext(), msg.getData().getString(Constants.TOAST),
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };
}
