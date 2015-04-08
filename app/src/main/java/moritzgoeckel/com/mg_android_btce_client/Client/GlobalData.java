package moritzgoeckel.com.mg_android_btce_client.Client;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

public class GlobalData {
    public static AsyncBtcApi API;
    public static Activity MainActivity;

    public static void RegisterForEvents(){
        API.addListener(new AsyncBtcApi.BitcoinDataListener() {
            @Override
            public void onAccountDataChanged(BTCE.Info info) {

            }

            @Override
            public void onHistoryDataChanged(BTCE.TradeHistory history) {

            }

            @Override
            public void onOpenOrdersDataChanged(BTCE.OrderList openOrders) {

            }

            @Override
            public void onPairDataChanged(String pair, BTCE.Ticker ticker) {

            }

            @Override
            public void onCancelOrderCompleted(int id) {
                Toast.makeText(MainActivity, "Order successfully canceled", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onTradeOrderCompleted(BTCE.Trade trade) {
                Toast.makeText(MainActivity, "Order successfully placed", Toast.LENGTH_LONG).show();
            }
        });
    }
}
