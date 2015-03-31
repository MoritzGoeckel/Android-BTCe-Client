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

    public final int MAXREQUESTTRIES = 3;

    public BTCE.Ticker getTicker(String pair){
        if(pairTicker.containsKey(pair))
            return pairTicker.get(pair);
        else
            return null;
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

    public double getTotalValue() {
        BTCE.Info info = getAccountInfo();
        BTCE.OrderList orders = getOpenOrders();

        if(info != null && orders != null)
        {
            double value = 0d;

            value += getValueIfNotNull(info.info.funds.btc, getTicker("btc_usd"));
            value += getValueIfNotNull(info.info.funds.ltc, getTicker("ltc_usd"));
            value += getValueIfNotNull(info.info.funds.nmc, getTicker("nmc_usd"));
            value += getValueIfNotNull(info.info.funds.nvc, getTicker("nvc_usd"));
            value += getValueIfNotNull(info.info.funds.eur, getTicker("eur_usd"));
            value += getValueIfNotNull(info.info.funds.rur, getTicker("rur_usd"));
            value += getValueIfNotNull(info.info.funds.ppc, getTicker("ppc_usd")); //Todo: Liste nicht vollständig

            for(int i = 0; i < orders.info.orders.length; i++)
            {
                Log.e("pair ", orders.info.orders[i].order_details.pair);

                if(getTicker(orders.info.orders[i].order_details.pair) != null)
                    value += (orders.info.orders[i].order_details.amount * getTicker(orders.info.orders[i].order_details.pair).last);
            }

            return value;
        }
        else return -1d;
    }

    private double getValueIfNotNull(double amount, BTCE.Ticker ticker){
        Log.println(10, "********","Amount: " + amount + " Ticker: " + (ticker != null));

        if(amount >= 0.01d && ticker != null)
            return amount * ticker.last;
        else
            return 0d;
    }

    public double getTotalVolume() {
        BTCE.TradeHistory history = getTradeHistory();
        BTCE.Info info = getAccountInfo();

        if(history == null || info == null)
            return -1d;
        else
        {
            double volume = 0d;
            for(int i = 0; i < history.info.trades.length; i++){
                BTCE.TradeHistoryOrder trade = history.info.trades[i];

                //In the last 30 days
                if(trade.trade_details.timestamp > (info.info.server_time - (30 * 24 * 60 * 60)))
                    volume += (trade.trade_details.amount * trade.trade_details.rate);
            }
            return  volume;
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

                boolean success = false;
                int tries = 0;
                while (tries < MAXREQUESTTRIES && success == false) {
                    try {
                        BTCE.Info tmp = api.getInfo();
                        if (tmp.success == 1) {
                            accountInfo = tmp;
                            notifyForAccountDataChange();
                            success = true;
                        }
                    } catch (Exception e) {
                        Log.d("*****", e.getMessage());
                    }
                    tries++;
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

                boolean success = false;
                int tries = 0;
                while (tries < MAXREQUESTTRIES && success == false)
                {
                    try {
                        BTCE.OrderList tmp = api.getActiveOrders();
                        if (tmp.success == 1) {
                            openOrders = tmp;
                            notifyForOpenOrdersDataChange();
                            success = true;
                        }
                    } catch (Exception e) {
                        Log.d("*****", e.getMessage());
                    }
                    tries++;
                }
            }
        });

        openOrdersRequestThread.start();
    }

    private Thread historyDataRequestThread;
    public void requestHistoryData(){
        historyDataRequestThread = new Thread(new Runnable() {
            @Override
            public void run() {
                boolean success = false;
                int tries = 0;
                while (tries < MAXREQUESTTRIES && success == false) {
                    try {

                        BTCE.TradeHistory tmp = api.getTradeHistory();
                        if (tmp.success == 1) {
                            tradeHistory = tmp;
                            notifyForHistoryDataChange();
                            success = true;
                        }

                    } catch (Exception e) {
                        Log.d("*****", e.getMessage());
                    }
                    tries++;
                }
            }
        });

        historyDataRequestThread.start();
    }

    private Thread pairDataRequestThread;
    public void requestPairData(final String pair){
        pairDataRequestThread = new Thread(new Runnable() {
            @Override
            public void run() {
                boolean success = false;
                int tries = 0;
                while (tries < MAXREQUESTTRIES && success == false) {
                    try {

                        BTCE.Ticker ticker = api.getTicker(pair);

                        if (pairTicker.containsKey(pair)) {
                            pairTicker.remove(pair);
                        }

                        pairTicker.put(pair, ticker);
                        notifyForPairDataChange(pair);
                        success = true;

                    } catch (Exception e) {
                        Log.d("*****", e.getMessage());
                    }
                    tries++;
                }
            }
        });

        pairDataRequestThread.start();
    }

}
