package moritzgoeckel.com.mg_android_btce_client;

import android.os.Bundle;
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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
        });

        return rootView;
    }

    private void renderAccountInfo(){

        BTCE.Info info = GlobalData.API.getAccountInfo();
        if(info != null)
            tx_funds.setText(getFundsString(info));

        double volumen = GlobalData.API.getTotalVolume();
        if(volumen != -1d)
            tx_volume.setText(String.valueOf(volumen) + "USD");

        double value = GlobalData.API.getTotalValue();
        if(value != -1d)
            tx_value.setText(String.valueOf(value) + "USD");
    }

    private String getFundsString(BTCE.Info info) {
        String nl = "\n";
        String all = "";
        all += "USD: " + String.valueOf(info.info.funds.usd) + nl; //Todo: Get stuff on order
        all += "EUR: " + String.valueOf(info.info.funds.eur) + nl; //TODO: Get Value in USD
        all += "BTC: " + String.valueOf(info.info.funds.btc) + nl;
        all += "LTC: " + String.valueOf(info.info.funds.ltc) + nl;
        all += "NMC: " + String.valueOf(info.info.funds.nmc) + nl;
        all += "PPC: " + String.valueOf(info.info.funds.ppc) + nl;
        all += "NVC: " + String.valueOf(info.info.funds.nvc) + nl;
        all += "CNC: " + String.valueOf(info.info.funds.cnc) + nl;
        all += "FTC: " + String.valueOf(info.info.funds.ftc) + nl;
        all += "RUR: " + String.valueOf(info.info.funds.rur) + nl;
        all += "TRC: " + String.valueOf(info.info.funds.trc) + nl;
        return all;
    }
}
