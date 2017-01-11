package in.co.retail.kohlibrothers.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import in.co.retail.kohlibrothers.R;
import in.co.retail.kohlibrothers.Utility.AppConstants;

/**
 * Created by nirman on 5/31/2016.
 */
public class OrderDetailsAdapter extends RecyclerView.Adapter<OrderDetailsAdapter.OrdersViewHolder>
{
    JSONArray jList;
    private LayoutInflater inflater;
    private Context mContext;
    private AppConstants.ApplicationLayoutModeAction MODE = AppConstants.ApplicationLayoutModeAction.NONE;

    public OrderDetailsAdapter(Context _context, JSONArray _data, AppConstants.ApplicationLayoutModeAction _mode)
    {
        this.mContext = _context;//_activity.getApplicationContext();
        inflater = LayoutInflater.from(mContext);
        this.jList = _data;
        this.MODE = _mode;
    }

    @Override
    public OrdersViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = inflater.inflate(R.layout.order_det_row, parent, false);
        OrdersViewHolder holder = new OrdersViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final OrdersViewHolder holder, final int position)
    {
        try
        {
            final JSONObject jObj = jList.getJSONObject(position);
            System.out.println("LIST "+jObj);
            holder.pName.setText(jObj.getString("P_NAME"));
            holder.qty.setText(jObj.getString("QTY"));
            holder.price.setText(mContext.getResources().getString(R.string.rupees)+" "+jObj.getString("PROD_RATE"));
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount()
    {
        return jList.length();
    }

    class OrdersViewHolder extends RecyclerView.ViewHolder
    {
        TextView pName, qty, price;

        public OrdersViewHolder(View itemView)
        {
            super(itemView);
            pName = (TextView) itemView.findViewById(R.id.pName);
            qty = (TextView) itemView.findViewById(R.id.qty);
            price = (TextView) itemView.findViewById(R.id.price);
        }
    }
}
