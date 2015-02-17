package manitosecurity.ensc40.com.manitosecurity;

import android.app.Application;
import android.os.Handler;

/**
 * Created by Collin on 2/11/2015.
 */
public class ManitoApplication extends Application {
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

}
