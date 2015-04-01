package moritzgoeckel.com.mg_android_btce_client.Data;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import moritzgoeckel.com.mg_android_btce_client.Client.BTCE;
import moritzgoeckel.com.mg_android_btce_client.R;

public class OrdersListAdapter extends ArrayAdapter<BTCE.OrderListOrder> {
    private final LayoutInflater mInflater;
    public static final int MaxEntries = 200;

    public OrdersListAdapter(Context context, List<BTCE.OrderListOrder> data) {
        super(context, R.layout.order_item);
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        setData(data);
    }

    public void setData(List<BTCE.OrderListOrder> data) {
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
            view = mInflater.inflate(R.layout.order_item, parent, false);
        } else {
            view = convertView;
        }

        final BTCE.OrderListOrder item = getItem(position);

        TextView sellBuyView = ((TextView)view.findViewById(R.id.order_item_buy_sell));
        sellBuyView.setText(item.order_details.type);

        if(item.order_details.type.equals("buy"))
            sellBuyView.setTextColor(getContext().getResources().getColor(R.color.buyColor));
        else
            sellBuyView.setTextColor(getContext().getResources().getColor(R.color.sellColor));

        ((TextView)view.findViewById(R.id.order_item_conditions)).setText(formatD(item.order_details.amount) +  " for " + formatD(item.order_details.rate));
        ((TextView)view.findViewById(R.id.order_item_pair)).setText(item.order_details.pair);
        ((TextView)view.findViewById(R.id.order_item_status)).setText("State: " + item.order_details.status);

        Button cancelOrderBtn = (Button) view.findViewById(R.id.cancel_order_button);
        cancelOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                // Add the buttons
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Toast.makeText(getContext(), item.order_details.rate + " " + item.order_details.pair + " -> Cancel", Toast.LENGTH_SHORT).show();
                        //Todo: Cancel
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(getContext(), "Did not cancel.", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setTitle("Cancel order?");
                builder.setMessage(item.order_details.type + " " + item.order_details.pair + " on " + item.order_details.amount + " for " + item.order_details.rate);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        return view;
    }

    private String formatD(double d)
    {
        return String.valueOf((double)Math.round(d * 100) / 100);
    }
}