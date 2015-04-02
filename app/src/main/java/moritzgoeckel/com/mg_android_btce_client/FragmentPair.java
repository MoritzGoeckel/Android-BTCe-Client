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
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.InjectView;
import moritzgoeckel.com.mg_android_btce_client.Client.AsyncBtcApi;
import moritzgoeckel.com.mg_android_btce_client.Client.BTCE;
import moritzgoeckel.com.mg_android_btce_client.Client.GlobalData;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_pair, container, false);

        ButterKnife.inject(this, rootView);

        //Init chart? Todo:Chart?
        //webView.loadUrl("https://bitcoincharts.com/charts/chart.png?m=btceUSD&v=1&t=S&noheader=1&height=80&width=750&r=2"); //Todo: allgemein / woher die Charts bei den alts?

        render();

        initButtonListeners();

        initBuySeekbars();

        initSellSeekbars();

        GlobalData.API.addListener(new AsyncBtcApi.BitcoinDataListener() {
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
                render();
            }

            @Override
            public void onCancelOrderCompleted(int id) {

            }
        });

        return rootView;
    }

    private void initSellSeekbars() {
        pair_sell_price_seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                BTCE.Ticker ticker = GlobalData.API.getTicker("btc_usd");
                if(ticker != null) {
                    double min = ticker.last;
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
        pair_buy_price_seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                BTCE.Ticker ticker = GlobalData.API.getTicker("btc_usd");
                if (ticker != null) {
                    double min = ticker.low;
                    double max = ticker.high;

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
                if(account != null && pair_buy_price.getText() != null) {
                    try
                    {
                        double price = Double.valueOf(pair_buy_price.getText().toString());

                        double min = 0d;
                        double max = account.info.funds.usd / price;

                        double amount = ((max - min) * (progress / 100d)) + min;

                        pair_buy_amount.setText(formatD(amount));
                    }
                    catch (Exception e)
                    {

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
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                // Add the buttons
                builder.setPositiveButton("Buy!", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(getActivity(), "Submitting buy order ...", Toast.LENGTH_LONG).show();
                        //Todo: do the buy
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Cancel
                    }
                });
                builder.setTitle("Buy?");
                builder.setMessage("Buy at " + pair_buy_price.getText().toString() + " USD " + pair_buy_amount.getText().toString() + " BTC");
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        pair_sell_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                // Add the buttons
                builder.setPositiveButton("Sell!", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(getActivity(), "Submitting sell order ...", Toast.LENGTH_LONG).show();
                        //Todo: do the sell
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Cancel
                    }
                });
                builder.setTitle("Sell?");
                builder.setMessage("Sell " + pair_buy_amount.getText().toString() + " BTC at " + pair_buy_price.getText().toString() + " USD");
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
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
