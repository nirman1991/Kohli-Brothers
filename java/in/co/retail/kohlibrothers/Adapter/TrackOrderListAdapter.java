package in.co.retail.kohlibrothers.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import in.co.retail.kohlibrothers.R;
import in.co.retail.kohlibrothers.Utility.AppConstants;

/**
 * Created by nirman on 5/23/2016.
 */
public class TrackOrderListAdapter extends RecyclerView.Adapter<TrackOrderListAdapter.TrackViewHolder>
{
    JSONArray jList;
    private LayoutInflater inflater;
    private Context mContext;
    private OrderListener mOrderListener;
    private AppConstants.ApplicationLayoutModeAction MODE = AppConstants.ApplicationLayoutModeAction.NONE;

    public TrackOrderListAdapter(Context _context, JSONArray _data, AppConstants.ApplicationLayoutModeAction _mode)
    {
        this.mContext = _context;//_activity.getApplicationContext();
        inflater = LayoutInflater.from(mContext);
        this.mOrderListener = (OrderListener) mContext;
        this.jList = _data;
        this.MODE = _mode;
    }

    @Override
    public TrackViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = inflater.inflate(R.layout.track_order_row, parent, false);
        TrackViewHolder holder = new TrackViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final TrackViewHolder holder, final int position)
    {
        try
        {
            final JSONObject jObj = jList.getJSONObject(position);
            System.out.println("LIST "+jObj);
            String Status = jObj.getString("ORDER_STATUS");
            holder.orderId.setText(jObj.getString("ORDER_ID"));
            holder.orderDate.setText(jObj.getString("ORDER_DATE"));
            holder.orderTotal.setText(mContext.getResources().getString(R.string.rupees)+" "+jObj.getString("ORDER_TOTAL"));
            holder.orderTotalDummy.setText(jObj.getString("ORDER_TOTAL"));
            holder.orderDelCharges.setText(mContext.getResources().getString(R.string.rupees)+" "+jObj.getString("DEL_CHARGES"));
            System.out.println("CHARGES "+jObj.getString("DEL_CHARGES"));
            switch (Status)
            {
                case "I":
                case "O":
                    holder.layoutPending.setVisibility(View.VISIBLE);
                    holder.layoutProcessing.setVisibility(View.VISIBLE);
                    holder.layoutDispatch.setVisibility(View.VISIBLE);
                    holder.imgPending.setImageResource(R.drawable.ic_order_checked);
                    break;
                case "L":
                    holder.layoutPending.setVisibility(View.VISIBLE);
                    holder.layoutProcessing.setVisibility(View.VISIBLE);
                    holder.layoutDispatch.setVisibility(View.VISIBLE);
                    holder.imgDispatch.setImageResource(R.drawable.ic_order_checked);
                    break;
                case "Y":
                    holder.layoutDelivered.setVisibility(View.VISIBLE);
                    break;
                case "C":
                    holder.layoutCancel.setVisibility(View.VISIBLE);
                    holder.btnOrderDet.setVisibility(View.GONE);
                    break;
                case "P":
                case "G":
                case "S":
                case "X":
                    holder.layoutPending.setVisibility(View.VISIBLE);
                    holder.layoutProcessing.setVisibility(View.VISIBLE);
                    holder.layoutDispatch.setVisibility(View.VISIBLE);
                    holder.imgProcessing.setImageResource(R.drawable.ic_order_checked);
                    break;
                default:
                    holder.layoutPending.setVisibility(View.VISIBLE);
                    holder.layoutProcessing.setVisibility(View.VISIBLE);
                    holder.layoutDispatch.setVisibility(View.VISIBLE);
                    holder.imgPending.setImageResource(R.drawable.ic_order_checked);
                    break;
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        holder.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                mOrderListener.onOrderCancel(v, holder.orderId.getText().toString(), position);
                holder.layoutPending.setVisibility(View.GONE);
                holder.layoutProcessing.setVisibility(View.GONE);
                holder.layoutDispatch.setVisibility(View.GONE);
                holder.layoutBtnCancel.setVisibility(View.GONE);
                holder.btnOrderDet.setVisibility(View.GONE);
                holder.layoutCancel.setVisibility(View.VISIBLE);
            }
        });

        holder.btnOrderDet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                mOrderListener.onOrderView(v, holder.orderId.getText().toString(), holder.orderTotalDummy.getText().toString(), holder.orderDelCharges.getText().toString(), position);
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return jList.length();
    }

    class TrackViewHolder extends RecyclerView.ViewHolder
    {
        TextView orderStatus, orderId, orderDate, orderTotal, orderTotalDummy,orderDelCharges;
        LinearLayout layoutPending, layoutProcessing, layoutDispatch, layoutDelivered, layoutCancel, layoutBtnCancel;
        ImageView imgPending, imgProcessing, imgDispatch;
        Button btnCancel, btnOrderDet;

        public TrackViewHolder(View itemView)
        {
            super(itemView);
            orderId = (TextView) itemView.findViewById(R.id.orderId);
            orderDate = (TextView) itemView.findViewById(R.id.orderDate);
            orderTotal = (TextView) itemView.findViewById(R.id.orderTotal);
            orderTotalDummy = (TextView) itemView.findViewById(R.id.orderTotalDummy);
            orderDelCharges = (TextView) itemView.findViewById(R.id.orderDelCharges);

            imgPending = (ImageView) itemView.findViewById(R.id.orderPending);
            imgProcessing = (ImageView) itemView.findViewById(R.id.orderProcessing);
            imgDispatch = (ImageView) itemView.findViewById(R.id.orderDispatch);

            layoutPending = (LinearLayout) itemView.findViewById(R.id.pendingLayout);
            layoutProcessing = (LinearLayout) itemView.findViewById(R.id.processingLayout);
            layoutDispatch = (LinearLayout) itemView.findViewById(R.id.dispatchLayout);
            layoutDelivered = (LinearLayout) itemView.findViewById(R.id.deliveredLayout);
            layoutCancel = (LinearLayout) itemView.findViewById(R.id.cancelLayout);
            layoutBtnCancel = (LinearLayout) itemView.findViewById(R.id.btnCancelLayout);

            btnCancel = (Button) itemView.findViewById(R.id.btnCancel);
            btnOrderDet = (Button) itemView.findViewById(R.id.btnOrdDet);
        }
    }

    public interface OrderListener
    {
        void onOrderCancel(View view, String OrderId, int position);
        void onOrderView(View view, String OrderId, String OrderTotal,String OrderDelCharges, int position);
    }
}

