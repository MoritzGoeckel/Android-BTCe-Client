package moritzgoeckel.com.mg_android_btce_client;

import android.app.Activity;
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

    @InjectView(R.id.account_value) TextView value;
    @InjectView(R.id.account_usd) TextView usd;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_account, container, false);
        ButterKnife.inject(this, rootView);

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        //Render current data
        if(GlobalData.API.getAccountInfo() != null) {
            renderAccountInfo(GlobalData.API.getAccountInfo());
        }

        //Listen to changes to render data
        GlobalData.API.addListener(new AsyncBtcApi.BitcoinDataListener() {
            @Override
            public void onAccountDataChanged(BTCE.Info info) {
                renderAccountInfo(info);
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
        });
    }

    private void renderAccountInfo(BTCE.Info info){
        value.setText(String.valueOf(info.info.funds.usd));
        usd.setText(String.valueOf(info.info.server_time));
    }
}
