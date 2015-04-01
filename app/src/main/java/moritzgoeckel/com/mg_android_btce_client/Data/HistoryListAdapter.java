package moritzgoeckel.com.mg_android_btce_client.Data;
import java.util.List;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import moritzgoeckel.com.mg_android_btce_client.Client.BTCE;
import moritzgoeckel.com.mg_android_btce_client.Client.GlobalData;
import moritzgoeckel.com.mg_android_btce_client.R;

public class HistoryListAdapter extends ArrayAdapter<BTCE.TradeHistoryOrder> {
    public static final int MaxEntries = 200;
    private final LayoutInflater mInflater;

    public HistoryListAdapter(Context context, List<BTCE.TradeHistoryOrder> data) {
        super(context, R.layout.historic_order_item);
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        setData(data);
    }

    public void setData(List<BTCE.TradeHistoryOrder> data) {
        clear();
        if (data != null) {
            for (int i = 0; i < data.size() && i < MaxEntries; i++) {
                add(data.get(i));
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

        initTextViews(view, item);

        markGoodAndBadTrades(view, item);

        return view;
    }

    private void initTextViews(View view, BTCE.TradeHistoryOrder item) {
        String duration = "";

        BTCE.Info info = GlobalData.API.getAccountInfo();
        if(info != null)
        {
            long delta = info.info.server_time - item.trade_details.timestamp;
            duration = formatDuration(delta * 1000) + " AGO";
        }

        TextView sellBuyView = ((TextView)view.findViewById(R.id.historic_order_item_buy_sell));
        sellBuyView.setText(item.trade_details.type);

        if(item.trade_details.type.equals("buy"))
            sellBuyView.setTextColor(getContext().getResources().getColor(R.color.buyColor));
        else
            sellBuyView.setTextColor(getContext().getResources().getColor(R.color.sellColor));

        ((TextView)view.findViewById(R.id.historic_order_item_conditions)).setText(formatD(item.trade_details.amount) + " for " + formatD(item.trade_details.rate));
        ((TextView)view.findViewById(R.id.historic_order_item_pair)).setText(item.trade_details.pair);
        ((TextView)view.findViewById(R.id.historic_order_item_time)).setText(duration);
    }

    private void markGoodAndBadTrades(View view, BTCE.TradeHistoryOrder item) {
        //Good trades with green background
        int review = 0;
        BTCE.Ticker ticker = GlobalData.API.getTicker(item.trade_details.pair);
        if(ticker != null)
        {
            if(item.trade_details.type.equals("buy"))
                if(item.trade_details.rate < ticker.last)
                    review = 1;
                else if(item.trade_details.rate > ticker.last)
                    review = -1;

            if(item.trade_details.type.equals("sell"))
                if(item.trade_details.rate > ticker.last)
                    review = 1;
                else if(item.trade_details.rate < ticker.last)
                    review = -1;
        }

        LinearLayout layout = (LinearLayout) view.findViewById(R.id.historic_order_layout);
        if(review == 1)
            layout.setBackgroundColor(getContext().getResources().getColor(R.color.goodColor));
        if(review == -1)
            layout.setBackgroundColor(getContext().getResources().getColor(R.color.badColor));
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

    private String formatD(double d)
    {
        return String.valueOf((double)Math.round(d * 100) / 100);
    }
}