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
            double value = info.info.funds.usd;

            value += getValueIfNotNull(info.info.funds.btc, getTicker("btc_usd"));
            value += getValueIfNotNull(info.info.funds.ltc, getTicker("ltc_usd"));
            value += getValueIfNotNull(info.info.funds.nmc, getTicker("nmc_usd"));
            value += getValueIfNotNull(info.info.funds.nvc, getTicker("nvc_usd"));
            value += getValueIfNotNull(info.info.funds.eur, getTicker("eur_usd"));
            value += getValueIfNotNull(info.info.funds.rur, getTicker("rur_usd"));
            value += getValueIfNotNull(info.info.funds.ppc, getTicker("ppc_usd")); //Todo: Liste nicht vollständig

            value += getValueIfNotNull(getOnOrder("btc"), getTicker("btc_usd"));
            value += getValueIfNotNull(getOnOrder("ltc"), getTicker("ltc_usd"));
            value += getValueIfNotNull(getOnOrder("nmc"), getTicker("nmc_usd"));
            value += getValueIfNotNull(getOnOrder("nvc"), getTicker("nvc_usd"));
            value += getValueIfNotNull(getOnOrder("eur"), getTicker("eur_usd"));
            value += getValueIfNotNull(getOnOrder("rur"), getTicker("rur_usd"));
            value += getValueIfNotNull(getOnOrder("ppc"), getTicker("ppc_usd"));

            for(int i = 0; i < orders.info.orders.length; i++)
            {
                BTCE.OrderListOrder order = orders.info.orders[i];
                if(order != null && order.order_details.type.equals("buy") && order.order_details.pair.endsWith("usd"))
                    value += (order.order_details.amount * order.order_details.rate);
            }

            return value;
        }
        else return -1d;
    }

    private double getValueIfNotNull(double amount, BTCE.Ticker ticker){
        if(amount >= 0.001d && ticker != null)
            return amount * ticker.last;
        else
            return 0d;
    }

    public double getOnOrder(String currency){
        double value = 0d;
        if(getOpenOrders() != null)
        {
            BTCE.OrderListOrder[] orders = getOpenOrders().info.orders;
            for(int i = 0; i < orders.length; i++){
                if(orders[i].order_details.pair.startsWith(currency) && orders[i].order_details.type.equals("sell"))
                    value += orders[i].order_details.amount;
            }
        }
        return value; //Todo: Für USD optimieren...
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
        void onAccountDataChanged(BTCE.Info info);
        void onHistoryDataChanged(BTCE.TradeHistory history);
        void onOpenOrdersDataChanged(BTCE.OrderList openOrders);
        void onPairDataChanged(String pair, BTCE.Ticker ticker);
        void onCancelOrderCompleted(int id);
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

    private void notifyForCancelOrderCompleted(final int id) {
        //Magic to send a signal to the main thread
        Handler mainHandler = new Handler(parentActivity.getBaseContext().getMainLooper());
        Runnable myRunnable = new Runnable() {
            @Override
            public void run()
            {
                for (BitcoinDataListener listener : listeners)
                    listener.onCancelOrderCompleted(id);
            }
        };
        mainHandler.post(myRunnable);
    }

    public void requestAccountData(){
        tryAsync(new MaxTryRunnable() {
            @Override
            public boolean run() throws Exception {
                Log.i("API DOWNLOAD", "Account Data");
                BTCE.Info tmp = api.getInfo();
                if (tmp.success == 1) {
                    accountInfo = tmp;
                    notifyForAccountDataChange();
                    return true;
                }
                return false;
            }
        }, MAXREQUESTTRIES);
    }

    public void requestOpenOrdersData(){
        tryAsync(new MaxTryRunnable() {
            @Override
            public boolean run() throws Exception {
                Log.i("API DOWNLOAD", "Open Orders");
                BTCE.OrderList tmp = api.getActiveOrders();
                if (tmp.success == 1) {
                    openOrders = tmp;
                    notifyForOpenOrdersDataChange();
                    return true;
                }
                return false;
            }
        }, MAXREQUESTTRIES);
    }

    public void requestHistoryData(){
        tryAsync(new MaxTryRunnable() {
            @Override
            public boolean run() throws Exception {
                Log.i("API DOWNLOAD", "History Data");
                BTCE.TradeHistory tmp = api.getTradeHistory();
                if (tmp.success == 1) {
                    tradeHistory = tmp;
                    notifyForHistoryDataChange();
                    return true;
                }
                return false;
            }
        }, MAXREQUESTTRIES);
    }

    public void requestPairData(final String pair){
        tryAsync(new MaxTryRunnable() {
            @Override
            public boolean run() throws Exception {
                Log.i("API DOWNLOAD", "Pair Data");

                BTCE.Ticker ticker = api.getTicker(pair);

                if (pairTicker.containsKey(pair)) {
                    pairTicker.remove(pair);
                }

                pairTicker.put(pair, ticker);
                notifyForPairDataChange(pair);
                return true;
            }
        }, MAXREQUESTTRIES);
    }

    public void requestCancelOrder(final int id){
        tryAsync(new MaxTryRunnable() {
            @Override
            public boolean run() throws Exception {
                Log.i("API DOWNLOAD", "Cancel Data");

                BTCE.CancelOrder cancelResponse = api.cancelOrder(id);

                if (cancelResponse.success == 1) {
                    notifyForCancelOrderCompleted(id);
                    requestOpenOrdersData();
                    return true;
                }
                return false;
            }
        }, MAXREQUESTTRIES);
    }

    interface MaxTryRunnable{
        boolean run() throws Exception;
    }

    public void tryAsync(final MaxTryRunnable runnable, final int maxTries){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                int tries = 0;
                boolean success = false;
                while (tries < maxTries && success == false){
                    try{
                        tries++;
                        success = runnable.run();
                    }
                    catch (Exception e)
                    {
                        success = false;
                    }
                }
            }
        });
        thread.start();
    }

}
