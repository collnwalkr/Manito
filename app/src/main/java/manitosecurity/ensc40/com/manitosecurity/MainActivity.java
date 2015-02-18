package manitosecurity.ensc40.com.manitosecurity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

public class MainActivity extends Activity {

    private ImageButton imageState;
    private SharedPreferences settings;
    SharedPreferences.Editor editor;
    String TAG = "MAINTAG";

    Handler.Callback realCallback = null;
    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (realCallback != null) {
                realCallback.handleMessage(msg);
            }
        };
    };
    public Handler getHandler() {
        return handler;
    }
    public void setCallBack(Handler.Callback callback) {
        this.realCallback = callback;
    }

    /**
     * Member object for the chat services
     */
    private BTChat mChatService = null;
    private StringBuffer mOutStringBuffer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //GET SHARED PREFERENCES and SET UP EDITOR
        settings = PreferenceManager.getDefaultSharedPreferences(this);

        editor = settings.edit();

        //Set up finished
        //if(!settings.getBoolean("setUp", false)) {setUpFinished();}
        mChatService = new BTChat(this, mHandler);
        mOutStringBuffer = new StringBuffer("");
        setUpUI();
    }

    @Override
    protected void onResume(){
        super.onResume();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent settings = new Intent(getApplicationContext(), SetUpBT.class);
                startActivity(settings);
                return true;
            case R.id.developer_setting:
                Intent developer = new Intent(getApplicationContext(), SetUpBlueTooth.class);
                startActivity(developer);
                return true;
            case R.id.wifi_detection:
                Intent wifi = new Intent(getApplicationContext(), SetUpWifi.class);
                startActivity(wifi);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setUpFinished(){
        editor.putBoolean("setUp", true).commit();
    }

    private void setUpUI(){
        imageState = (ImageButton) findViewById(R.id.stateImage);


        if (!settings.getBoolean("armState", false)){		//if setting is off, button should be off
            imageState.setImageResource(R.drawable.button_off);
        }

        imageState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = settings.edit();
                if(settings.getBoolean("armState", false) == true){			//if armed -> disarm
                    imageState.setImageResource(R.drawable.button_off);
                    editor.putBoolean("armState", false).commit();
                    sendMessage("D");
                }

                else{   //disarm -> arm
                    imageState.setImageResource(R.drawable.button_on);
                    editor.putBoolean("armState", true).commit();
                    sendMessage("A");
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
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
                case Constants.MESSAGE_TOAST:
                    if (null != this) {
                        Toast.makeText(getApplicationContext(), msg.getData().getString(Constants.TOAST),
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

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

            // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer.setLength(0);
        }
    }

}
