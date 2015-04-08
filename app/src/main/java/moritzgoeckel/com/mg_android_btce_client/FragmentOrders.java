package moritzgoeckel.com.mg_android_btce_client;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import moritzgoeckel.com.mg_android_btce_client.Client.AsyncBtcApi;
import moritzgoeckel.com.mg_android_btce_client.Client.BTCE;
import moritzgoeckel.com.mg_android_btce_client.Client.GlobalData;
import moritzgoeckel.com.mg_android_btce_client.Data.HistoryListAdapter;
import moritzgoeckel.com.mg_android_btce_client.Data.OrdersListAdapter;

public class FragmentOrders extends ListFragment{
    OrdersListAdapter adapter;
    List<BTCE.OrderListOrder> orderList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = super.onCreateView(inflater, container, savedInstanceState);

        redraw();

        GlobalData.API.addListener(new AsyncBtcApi.BitcoinDataListener() {
            @Override
            public void onAccountDataChanged(BTCE.Info info) {

            }

            @Override
            public void onHistoryDataChanged(BTCE.TradeHistory history) {

            }

            @Override
            public void onOpenOrdersDataChanged(BTCE.OrderList openOrders) {
                redraw();
            }

            @Override
            public void onPairDataChanged(String pair, BTCE.Ticker ticker) {

            }

            @Override
            public void onCancelOrderCompleted(int id) {

            }

            @Override
            public void onTradeOrderCompleted(BTCE.Trade trade) {

            }
        });

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        activity.setTitle("Orders");
    }

    private void redraw() {
        BTCE.OrderList orders = GlobalData.API.getOpenOrders();
        if(orders != null)
        {
            orderList = arrayToList(orders.info.orders);

            adapter = new OrdersListAdapter(GlobalData.MainActivity, orderList);
            setListAdapter(adapter);
        }
    }

    private List<BTCE.OrderListOrder> arrayToList(BTCE.OrderListOrder[] array){
        List<BTCE.OrderListOrder> tmpList = new ArrayList<>();

        for(int i = 0; i < array.length; i++){
            tmpList.add(array[i]);
        }

        return tmpList;
    }
}
