package moritzgoeckel.com.mg_android_btce_client.Data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import moritzgoeckel.com.mg_android_btce_client.Client.BTCE;
import moritzgoeckel.com.mg_android_btce_client.R;

public class OrdersListAdapter extends ArrayAdapter<BTCE.OrderListOrder> {
    private final LayoutInflater mInflater;

    public OrdersListAdapter(Context context, List<BTCE.OrderListOrder> data) {
        super(context, R.layout.order_item);
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        setData(data);
    }

    public void setData(List<BTCE.OrderListOrder> data) {
        clear();
        if (data != null) {
            for (BTCE.OrderListOrder appEntry : data) {
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
            view = mInflater.inflate(R.layout.order_item, parent, false);
        } else {
            view = convertView;
        }

        BTCE.OrderListOrder item = getItem(position);
        ((TextView)view.findViewById(R.id.order_item_headline)).setText(item.order_details.pair + " " + item.order_details.type);
        ((TextView)view.findViewById(R.id.order_item_subtitle)).setText(item.order_details.amount +  " for " + item.order_details.rate);

        return view;
    }
}