package app.taxipizza.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;


public class NetworkAlert {

    public interface NetworkAlertListener {
        public void onConnected(String data);

        public void onDisconnected();

    }

    private NetworkAlertListener listener;

    private Context context;

    public NetworkAlert(Context context) {
        this.listener = null;
        this.context = context;
    }

    void checkNetworkConnection(Context context) {
        boolean state = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager != null) {
            NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
            if(info != null) {
                for(int i=0; i< info.length; i++) {
                    if(info[i].getState() == NetworkInfo.State.CONNECTED)
                        state = true;
                }
            }
        }
        if(listener != null) {
            if(state)
                listener.onConnected("Data listener");
            else
                listener.onDisconnected();
        }
    }

    public void setNetworkAlertListner(NetworkAlertListener listner) {
        this.listener = listner;
        checkNetworkConnection(context);
    }

}
