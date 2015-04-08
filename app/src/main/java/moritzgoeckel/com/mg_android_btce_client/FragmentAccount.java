package moritzgoeckel.com.mg_android_btce_client;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import moritzgoeckel.com.mg_android_btce_client.Client.AsyncBtcApi;
import moritzgoeckel.com.mg_android_btce_client.Client.BTCE;
import moritzgoeckel.com.mg_android_btce_client.Client.GlobalData;

public class FragmentAccount extends android.support.v4.app.Fragment{

    @InjectView(R.id.account_value) TextView tx_value;
    @InjectView(R.id.account_volume) TextView tx_volume;
    @InjectView(R.id.account_funds) TextView tx_funds;

    //Todo: Möglichkeit zur Eingabe der Keys

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_account, container, false);
        ButterKnife.inject(this, rootView);

        //Render current data
        if(GlobalData.API.getAccountInfo() != null) {
            renderAccountInfo();
        }

        //Listen to changes to render data
        GlobalData.API.addListener(new AsyncBtcApi.BitcoinDataListener() {
            @Override
            public void onAccountDataChanged(BTCE.Info info) {
                renderAccountInfo();
            }

            @Override
            public void onHistoryDataChanged(BTCE.TradeHistory history) {
                renderAccountInfo();
            }

            @Override
            public void onOpenOrdersDataChanged(BTCE.OrderList openOrders) {
                renderAccountInfo();
            }

            @Override
            public void onPairDataChanged(String pair, BTCE.Ticker ticker) {
                renderAccountInfo();
            }

            @Override
            public void onCancelOrderCompleted(int id) {

            }

            @Override
            public void onTradeOrderCompleted(BTCE.Trade trade) {

            }
        });

        //Log.e("**MG**", "Account Fragment Created!");

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        activity.setTitle("Account");
    }

    private void renderAccountInfo(){

        //Todo: Redesign account page

        BTCE.Info info = GlobalData.API.getAccountInfo();
        if(info != null)
            tx_funds.setText(getFundsString(info));

        double volumen = GlobalData.API.getTotalVolume();
        if(volumen != -1d)
            tx_volume.setText(formatD(volumen) + " USD");

        double value = GlobalData.API.getTotalValue();
        if(value != -1d)
            tx_value.setText(formatD(value) + " USD");
    }

    private String getFundsString(BTCE.Info info) {
        String nl = "\n";
        String all = "";
        String seperator1 = " + ";
        all += "USD: " + formatD(info.info.funds.usd) + nl; //Todo: Get on order
        all += "EUR: " + formatD(info.info.funds.eur) +seperator1+ formatD(GlobalData.API.getOnOrder("eur")) + nl; //TODO: Get Value in USD
        all += "BTC: " + formatD(info.info.funds.btc) +seperator1+ formatD(GlobalData.API.getOnOrder("btc")) + nl;
        all += "LTC: " + formatD(info.info.funds.ltc) +seperator1+ formatD(GlobalData.API.getOnOrder("ltc")) + nl;
        all += "NMC: " + formatD(info.info.funds.nmc) +seperator1+ formatD(GlobalData.API.getOnOrder("nmc")) + nl;
        all += "PPC: " + formatD(info.info.funds.ppc) +seperator1+ formatD(GlobalData.API.getOnOrder("ppc")) + nl;
        all += "NVC: " + formatD(info.info.funds.nvc) +seperator1+ formatD(GlobalData.API.getOnOrder("nvc")) + nl;
        all += "CNC: " + formatD(info.info.funds.cnc) +seperator1+ formatD(GlobalData.API.getOnOrder("cnc")) + nl;
        all += "FTC: " + formatD(info.info.funds.ftc) +seperator1+ formatD(GlobalData.API.getOnOrder("ftc")) + nl;
        all += "RUR: " + formatD(info.info.funds.rur) +seperator1+ formatD(GlobalData.API.getOnOrder("rur")) + nl;
        all += "TRC: " + formatD(info.info.funds.trc) +seperator1+ formatD(GlobalData.API.getOnOrder("trc")) + nl;
        return all;
    }

    private String formatD(double d)
    {
        return String.valueOf((double)Math.round(d * 100) / 100);
    }
}
