package moritzgoeckel.com.mg_android_btce_client.Client;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AsyncBtcApi {

    private Activity parentActivity;
    private BTCE api;
    private List<BitcoinDataListener> listeners = new ArrayList<BitcoinDataListener>();

    private BTCE.Info accountInfo;

    public BTCE.Info getAccountInfo(){
        return accountInfo;
    }

    private BTCE.OrderList openOrders;

    public BTCE.OrderList getOpenOrders(){
        return openOrders;
    }

    private BTCE.TradeHistory tradeHistory;

    public BTCE.TradeHistory getTradeHistory(){
        return tradeHistory;
    }

    HashMap<String, BTCE.Ticker> pairTicker = new HashMap<>();

    public BTCE.Ticker getTicker(String pair){
        return pairTicker.get(pair);
    }

    public AsyncBtcApi(final String key, final String secret, Activity parentActivity){
        this.parentActivity = parentActivity;
        api = new BTCE();


        try {
            api.setAuthKeys(key, secret);
        } catch (BTCE.BTCEException e) {
            e.printStackTrace();
        }
    }

    public void addListener(BitcoinDataListener listener){
        listeners.add(listener);
    }

    public interface BitcoinDataListener {
        public void onAccountDataChanged(BTCE.Info info);
        public void onHistoryDataChanged(BTCE.TradeHistory history);
        public void onOpenOrdersDataChanged(BTCE.OrderList openOrders);
        public void onPairDataChanged(String pair, BTCE.Ticker ticker);
    }

    private void notifyForAccountDataChange() {
        //Magic to send a signal to the main thread
        Handler mainHandler = new Handler(parentActivity.getBaseContext().getMainLooper());
        Runnable myRunnable = new Runnable() {
            @Override
            public void run()
            {
                for (BitcoinDataListener listener : listeners)
                    listener.onAccountDataChanged(getAccountInfo());
            }
        };
        mainHandler.post(myRunnable);
    }

    private void notifyForHistoryDataChange() {
        //Magic to send a signal to the main thread
        Handler mainHandler = new Handler(parentActivity.getBaseContext().getMainLooper());
        Runnable myRunnable = new Runnable() {
            @Override
            public void run()
            {
                for (BitcoinDataListener listener : listeners)
                    listener.onHistoryDataChanged(getTradeHistory());
            }
        };
        mainHandler.post(myRunnable);
    }

    private void notifyForOpenOrdersDataChange() {
        //Magic to send a signal to the main thread
        Handler mainHandler = new Handler(parentActivity.getBaseContext().getMainLooper());
        Runnable myRunnable = new Runnable() {
            @Override
            public void run()
            {
                for (BitcoinDataListener listener : listeners)
                    listener.onOpenOrdersDataChanged(getOpenOrders());
            }
        };
        mainHandler.post(myRunnable);
    }

    private void notifyForPairDataChange(final String pair) {
        //Magic to send a signal to the main thread
        Handler mainHandler = new Handler(parentActivity.getBaseContext().getMainLooper());
        Runnable myRunnable = new Runnable() {
            @Override
            public void run()
            {
                for (BitcoinDataListener listener : listeners)
                    listener.onPairDataChanged(pair, getTicker(pair));
            }
        };
        mainHandler.post(myRunnable);
    }

    private Thread accountDataRequestThread;
    public void requestAccountData(){
        accountDataRequestThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    accountInfo = api.getInfo();
                    notifyForAccountDataChange();
                } catch (Exception e)
                {
                    Log.d("*****", e.getMessage());
                }
            }
        });

        accountDataRequestThread.start();
    }

    private Thread openOrdersRequestThread;
    public void requestOpenOrdersData(){
        openOrdersRequestThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    openOrders = api.getActiveOrders();
                    notifyForOpenOrdersDataChange();
                } catch (Exception e) {Log.d("*****", e.getMessage());}
            }
        });

        openOrdersRequestThread.start();
    }

    private Thread historyDataRequestThread;
    public void requestHistoryData(){
        historyDataRequestThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    tradeHistory = api.getTradeHistory();
                    notifyForHistoryDataChange();
                } catch (Exception e) {Log.d("*****", e.getMessage());}
            }
        });

        historyDataRequestThread.start();
    }

    private Thread pairDataRequestThread;
    public void requestPairData(final String pair){
        pairDataRequestThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    BTCE.Ticker ticker = api.getTicker(pair);

                    if(pairTicker.containsKey(pair)){
                        pairTicker.remove(pair);
                    }

                    pairTicker.put(pair, ticker);
                    notifyForPairDataChange(pair);

                } catch (Exception e) {Log.d("*****", e.getMessage());}
            }
        });

        pairDataRequestThread.start();
    }

}
