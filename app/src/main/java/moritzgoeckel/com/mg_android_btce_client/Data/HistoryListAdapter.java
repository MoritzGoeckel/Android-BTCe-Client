package moritzgoeckel.com.mg_android_btce_client.Data;
import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.support.v4.util.TimeUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import javax.xml.datatype.Duration;

import moritzgoeckel.com.mg_android_btce_client.Client.BTCE;
import moritzgoeckel.com.mg_android_btce_client.Client.GlobalData;
import moritzgoeckel.com.mg_android_btce_client.R;

public class HistoryListAdapter extends ArrayAdapter<BTCE.TradeHistoryOrder> {
    private final LayoutInflater mInflater;

    public HistoryListAdapter(Context context, List<BTCE.TradeHistoryOrder> data) {
        super(context, R.layout.historic_order_item);
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        setData(data);
    }

    public void setData(List<BTCE.TradeHistoryOrder> data) {
        clear();
        if (data != null) {
            for (BTCE.TradeHistoryOrder appEntry : data) {
                add(appEntry);
            }
        }
    }

    /**
     * Populate new items in the list.
     */
    @Override public View getView(int position, View convertView, ViewGroup parent) {
        View view;

        if (convertView == null) {
            view = mInflater.inflate(R.layout.historic_order_item, parent, false);
        } else {
            view = convertView;
        }

        BTCE.TradeHistoryOrder item = getItem(position);

        String duration = "";

        BTCE.Info info = GlobalData.API.getAccountInfo();
        if(info != null)
        {
            long delta = info.info.server_time - item.trade_details.timestamp;
            delta = delta / 1000;
            duration = String.format("%d:%02d:%02d", delta/3600, (delta%3600)/60, (delta%60)) + " ago"; //Todo: Validate...
        }

        ((TextView)view.findViewById(R.id.historic_order_item_headline)).setText(item.trade_details.pair + " " + item.trade_details.type + " " + duration);
        ((TextView)view.findViewById(R.id.historic_order_item_subtitle)).setText(item.trade_details.amount +  " for " + item.trade_details.rate);

        return view;
    }
}