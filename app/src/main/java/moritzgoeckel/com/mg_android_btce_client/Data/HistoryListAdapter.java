package moritzgoeckel.com.mg_android_btce_client.Data;
import java.util.List;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

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
            duration = formatDuration(delta * 1000) + " AGO";
        }

        ((TextView)view.findViewById(R.id.historic_order_item_buy_sell)).setText(item.trade_details.type);
        ((TextView)view.findViewById(R.id.historic_order_item_conditions)).setText(item.trade_details.amount + " for " + item.trade_details.rate);
        ((TextView)view.findViewById(R.id.historic_order_item_pair)).setText(item.trade_details.pair);
        ((TextView)view.findViewById(R.id.historic_order_item_time)).setText(duration);

        return view;
    }

    public static String formatDuration(long millis)
    {
        long days = TimeUnit.MILLISECONDS.toDays(millis);
        millis -= TimeUnit.DAYS.toMillis(days);

        long weeks = days / 7;
        days = days%7;

        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);

        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);

        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

        return(weeks + "W " + days +"D "+ hours + "H");
    }
}