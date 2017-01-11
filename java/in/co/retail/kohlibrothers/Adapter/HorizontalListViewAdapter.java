package in.co.retail.kohlibrothers.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import in.co.retail.kohlibrothers.Model.Apis;
import in.co.retail.kohlibrothers.Model.ProductCommonModel;
import in.co.retail.kohlibrothers.R;
import in.co.retail.applibrary.LazyLoading.ImageLoader;


/**
 * Created by Niha on 11/25/2015.
 */
public class HorizontalListViewAdapter extends ArrayAdapter<ProductCommonModel>
{
    private LayoutInflater mLayoutInflater;
    private ImageLoader imageLoader;
    private Context mContext;
    private int mResId;
    private Listener mListener;

    public HorizontalListViewAdapter(Context _context, int _resourceId, List<ProductCommonModel> _listModel) {
        super(_context, _resourceId, _listModel);
        // TODO Auto-generated constructor stub
        this.mContext = _context;
        this.mResId = _resourceId;
        this.mListener = (Listener) mContext;
        this.imageLoader = new ImageLoader(getContext(), R.drawable.coming_soon);
        this.mLayoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /* (non-Javadoc)
     * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
     */
    @Override
    public View getView(final int _position, View _convertView, ViewGroup _parent)
    {
        // TODO Auto-generated method stub
        final ProductCommonModel _hlvm = getItem(_position);
        System.out.print("\nPOSITION : " + _position + " ");
        System.out.print("P_NAME : " + _hlvm.getProductName() + " ");
        System.out.print("P_CODE : " + _hlvm.getProductCode() + "\n");
        if (mLayoutInflater != null && _hlvm != null)
        {
            System.out.print("VIEW TO BE RETURN "+"\n");

            // Inflate the view since it does not exist
            _convertView = mLayoutInflater.inflate(mResId, _parent, false);

            TextView lblText = (TextView) _convertView.findViewById(R.id.lblText);
            TextView lblDfSalesRate = (TextView) _convertView.findViewById(R.id.lblDfSalesRate);
            ImageView horizonImgCommon = (ImageView) _convertView.findViewById(R.id.horizonImgCommon);

            // set the controls image
            imageLoader.DisplayImage(Apis.URL + "assets/images/products/" + _hlvm.getProductCode() + "_"+1+".jpg", horizonImgCommon);
            System.out.print("DATA "+Apis.URL + "assets/images/products/" + _hlvm.getProductCode() + "_"+1+ ".jpg");

            lblText.setText(_hlvm.getProductName());

            lblDfSalesRate.setText(mContext.getResources().getString(R.string.rupees) + " " + _hlvm.getProductDFRate());
            lblDfSalesRate.setTextColor(mContext.getResources().getColor(android.R.color.holo_red_light));

            //for image onClick event
            horizonImgCommon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onHorizontalListClick(_hlvm.getProductCode(), _position);
                }
            });
        }
        else
        {
            System.out.print("NULL NOTHING TO RETURN");
        }
        return _convertView;
    }

    public interface Listener
    {
        void onHorizontalListClick(String productCode, int pos);
    }
}