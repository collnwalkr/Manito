package manitosecurity.ensc40.com.manitosecurity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by Collin on 2/9/2015.
 */
public class SetUpNetwork extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent bt_service = new Intent(this, BTChatService.class);
        startService(bt_service);
    }
}