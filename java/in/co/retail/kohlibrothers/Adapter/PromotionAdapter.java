package in.co.retail.kohlibrothers.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;

import in.co.retail.kohlibrothers.Model.Apis;
import in.co.retail.kohlibrothers.R;
import in.co.retail.applibrary.LazyLoading.ImageLoader;


/**
 * Created by Niha on 1/30/2016.
 */
public class PromotionAdapter extends RecyclerView.Adapter<PromotionAdapter.PromoViewHolder>
{
    private LayoutInflater mInflater;
    private Context mContext;
    private ImageLoader imageLoader;
    private JSONArray jDetail;
    private Listener mListener;

    public PromotionAdapter(Context context, Listener mListener, JSONArray jArr)
    {
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
        this.imageLoader = new ImageLoader(mContext, R.drawable.kb_logo);
        this.mListener = (Listener) mContext;
        this.jDetail = jArr;
    }
    @Override
    public PromoViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = mInflater.inflate(R.layout.promo_list_row, null);
        PromoViewHolder holder = new PromoViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(PromoViewHolder holder, int position)
    {
        try
        {
            final String promoCode = jDetail.getJSONObject(position).getString("PROMO_CODE");
            final String promoDesc = jDetail.getJSONObject(position).getString("DESCRIPTION");
            imageLoader.DisplayImage(Apis.URL+ "assets/promo/" + promoCode + ".jpg", holder.proImg);
            holder.proName.setText(promoCode);
            holder.proDesc.setText(promoDesc);

            holder.proViewAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    mListener.onViewClick(promoCode);
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return jDetail.length();
    }

    class PromoViewHolder extends RecyclerView.ViewHolder
    {
        ImageView proImg;
        TextView proName, proDesc, proViewAll;

        public PromoViewHolder(View itemView)
        {
            super(itemView);
            proImg = (ImageView) itemView.findViewById(R.id.promoImage);
            proName = (TextView) itemView.findViewById(R.id.promoName);
            proDesc = (TextView) itemView.findViewById(R.id.promoDesc);
            proViewAll = (TextView) itemView.findViewById(R.id.promoViewAll);
        }
    }

    public interface Listener
    {
        void onViewClick(String bannerId);
    }
}
