package moritzgoeckel.com.mg_android_btce_client.Client;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

public class GlobalData {
    public static AsyncBtcApi API;
    public static Activity MainActivity;
    public static void notifiyUserForCancelOrderCompleted(Context context){
        Toast.makeText(context, "Order successfully canceled", Toast.LENGTH_LONG).show();
    }
}
