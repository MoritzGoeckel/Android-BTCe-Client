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

public class FragmentHistory extends ListFragment{

    HistoryListAdapter adapter;
    List<BTCE.TradeHistoryOrder> historyOrdersList;

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
                redraw();
            }

            @Override
            public void onOpenOrdersDataChanged(BTCE.OrderList openOrders) {

            }

            @Override
            public void onPairDataChanged(String pair, BTCE.Ticker ticker) {
                redraw();
            }

            @Override
            public void onCancelOrderCompleted(int id) {
                GlobalData.notifiyUserForCancelOrderCompleted(getActivity());
            }
        });

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        activity.setTitle("History");
    }

    private void redraw() {
        BTCE.TradeHistory history = GlobalData.API.getTradeHistory();
        if(history != null)
        {
            historyOrdersList = arrayToList(history.info.trades);

            adapter = new HistoryListAdapter(GlobalData.MainActivity, historyOrdersList);
            setListAdapter(adapter);
        }
    }

    private List<BTCE.TradeHistoryOrder> arrayToList(BTCE.TradeHistoryOrder[] array){
        List<BTCE.TradeHistoryOrder> tmpList = new ArrayList<>();

        for(int i = 0; i < array.length; i++){
            tmpList.add(array[i]);
        }

        return tmpList;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        BTCE.TradeHistoryOrder item = this.historyOrdersList.get(position);
        Toast.makeText(getActivity(), item.trade_details.rate + " Clicked!", Toast.LENGTH_SHORT).show();
    }
}
