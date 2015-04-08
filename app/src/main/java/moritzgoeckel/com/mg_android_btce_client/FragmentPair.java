package moritzgoeckel.com.mg_android_btce_client;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import moritzgoeckel.com.mg_android_btce_client.Client.AsyncBtcApi;
import moritzgoeckel.com.mg_android_btce_client.Client.BTCE;
import moritzgoeckel.com.mg_android_btce_client.Client.GlobalData;
import moritzgoeckel.com.mg_android_btce_client.Data.OrdersListAdapter;

public class FragmentPair extends android.support.v4.app.Fragment{

    @InjectView(R.id.pair_buy_amount)
    EditText pair_buy_amount;

    @InjectView(R.id.pair_buy_amount_seekBar)
    SeekBar pair_buy_amount_seekBar;

    @InjectView(R.id.pair_buy_price)
    EditText pair_buy_price;

    @InjectView(R.id.pair_buy_price_seekBar)
    SeekBar pair_buy_price_seekBar;


    @InjectView(R.id.pair_sell_amount)
    EditText pair_sell_amount;

    @InjectView(R.id.pair_sell_amount_seekBar)
    SeekBar pair_sell_amount_seekBar;

    @InjectView(R.id.pair_sell_price)
    EditText pair_sell_price;

    @InjectView(R.id.pair_sell_price_seekBar)
    SeekBar pair_sell_price_seekBar;


    @InjectView(R.id.pair_heigh)
    TextView pair_high;

    @InjectView(R.id.pair_last)
    TextView pair_last;

    @InjectView(R.id.pair_low)
    TextView pair_low;

    @InjectView(R.id.pair_avg)
    TextView pair_avg;


    @InjectView(R.id.pair_buy_btn)
    Button pair_buy_btn;

    @InjectView(R.id.pair_sell_btn)
    Button pair_sell_btn;


    @InjectView(R.id.pair_open_orders_list)
    ListView pair_open_orders_list;


    @InjectView(R.id.pair_one_funds)
    TextView pair_one_funds;

    @InjectView(R.id.pair_two_funds)
    TextView pair_two_funds;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_pair, container, false);

        ButterKnife.inject(this, rootView);

        //Init chart? Todo:Chart?
        //webView.loadUrl("https://bitcoincharts.com/charts/chart.png?m=btceUSD&v=1&t=S&noheader=1&height=80&width=750&r=2");

        //Todo: allgemein implementieren

        render();
        renderAccountInfo();

        initButtonListeners();

        initBuySeekbars();

        initSellSeekbars();

        GlobalData.API.addListener(new AsyncBtcApi.BitcoinDataListener() {
            @Override
            public void onAccountDataChanged(BTCE.Info info) {
                render();
                renderOpenOrderList();
                renderAccountInfo();
            }

            @Override
            public void onHistoryDataChanged(BTCE.TradeHistory history) {

            }

            @Override
            public void onOpenOrdersDataChanged(BTCE.OrderList openOrders) {
                renderOpenOrderList();
            }

            @Override
            public void onPairDataChanged(String pair, BTCE.Ticker ticker) {
                render();
            }

            @Override
            public void onCancelOrderCompleted(int id) {

            }

            @Override
            public void onTradeOrderCompleted(BTCE.Trade trade) {

            }
        });

        renderOpenOrderList();

        return rootView;
    }

    private void renderOpenOrderList() {
        BTCE.OrderList o = GlobalData.API.getOpenOrders();
        if(o != null){
            List<BTCE.OrderListOrder> orderList = arrayToListAndFilter(o.info.orders);
            pair_open_orders_list.setAdapter(new OrdersListAdapter(getActivity(), orderList));
        }
    }

    private List<BTCE.OrderListOrder> arrayToListAndFilter(BTCE.OrderListOrder[] array){
        List<BTCE.OrderListOrder> tmpList = new ArrayList<>();

        for(int i = 0; i < array.length; i++){
            if(array[i].order_details.pair.contains("btc"))
                tmpList.add(array[i]);
        }

        return tmpList;
    }

    private void initSellSeekbars() {
        pair_sell_price_seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                BTCE.Ticker ticker = GlobalData.API.getTicker("btc_usd");
                if(ticker != null) {
                    double min = ticker.sell;
                    double max = ticker.high;

                    double price = ((max - min) * (progress / 100d)) + min;

                    pair_sell_price.setText(formatD(price));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        pair_sell_amount_seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                BTCE.Info account = GlobalData.API.getAccountInfo();
                if (account != null && pair_buy_price.getText() != null) {
                    try {
                        double min = 0d;
                        double max = account.info.funds.btc;

                        double amount = ((max - min) * (progress / 100d)) + min;

                        pair_sell_amount.setText(formatD(amount));
                    } catch (Exception e) {

                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void initBuySeekbars() {
        pair_buy_price_seekBar.setProgress(pair_buy_amount_seekBar.getMax());

        pair_buy_price_seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                BTCE.Ticker ticker = GlobalData.API.getTicker("btc_usd");
                if (ticker != null) {
                    double min = ticker.low;
                    double max = ticker.buy;

                    double price = ((max - min) * (progress / 100d)) + min;

                    pair_buy_price.setText(formatD(price));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        pair_buy_amount_seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                BTCE.Info account = GlobalData.API.getAccountInfo();
                if (account != null && pair_buy_price.getText() != null) {
                    try {
                        double price = Double.valueOf(pair_buy_price.getText().toString());

                        double min = 0d;
                        double max = account.info.funds.usd / price;

                        double amount = ((max - min) * (progress / 100d)) + min;

                        pair_buy_amount.setText(formatD(amount));
                    } catch (Exception e) {

                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void initButtonListeners() {
        pair_buy_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                    final double rate = Double.valueOf(pair_buy_price.getText().toString());
                    final double amount = Double.valueOf(pair_buy_amount.getText().toString());

                    builder.setTitle("Buy?");
                    builder.setMessage("Buy at " + rate + " USD " + amount + " BTC");

                    // Add the buttons
                    builder.setPositiveButton("Buy!", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Toast.makeText(getActivity(), "Submitting buy order ...", Toast.LENGTH_LONG).show();
                            GlobalData.API.requestTradeOrder("btc_usd", "buy", rate, amount);
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //Cancel
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }catch (Exception e){}
            }
        });

        pair_sell_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                    final double rate = Double.valueOf(pair_sell_price.getText().toString());
                    final double amount = Double.valueOf(pair_sell_amount.getText().toString());

                    builder.setTitle("Sell?");
                    builder.setMessage("Sell " + amount + " BTC at " + rate + " USD");

                    // Add the buttons
                    builder.setPositiveButton("Sell!", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Toast.makeText(getActivity(), "Submitting sell order ...", Toast.LENGTH_LONG).show();
                            GlobalData.API.requestTradeOrder("btc_usd", "sell", rate, amount);
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //Cancel
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }catch (Exception e){}
            }
        });
    }

    public void renderAccountInfo(){
        BTCE.Info info = GlobalData.API.getAccountInfo();
        if(info != null) {
            pair_one_funds.setText("BTC: " + formatD(info.info.funds.btc) + "  ");
            pair_two_funds.setText("USD: " + formatD(info.info.funds.usd) + "  ");
        }
    }

    public void render(){
        BTCE.Ticker ticker = GlobalData.API.getTicker("btc_usd");
        if(ticker != null){
            pair_last.setText("Last: " + formatD(ticker.last) + "  ");
            pair_high.setText("High: " + formatD(ticker.high) + "  ");
            pair_low.setText("Low: " + formatD(ticker.low) + "  "); //Todo: AVG BUY SELL VOL VOL_CUR?
            pair_avg.setText("Avg: " + formatD(ticker.avg) + "  ");

            pair_buy_price.setText(formatD(ticker.buy));

            pair_sell_price.setText(formatD(ticker.sell));
        }
    }

    private String formatD(double d)
    {
        return String.valueOf((double)Math.round(d * 100) / 100);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        activity.setTitle("Pair");
    }
}
