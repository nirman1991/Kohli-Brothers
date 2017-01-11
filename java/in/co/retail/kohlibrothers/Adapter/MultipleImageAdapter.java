package in.co.retail.kohlibrothers.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import java.util.List;

import in.co.retail.kohlibrothers.Model.MultipleImageModel;
import in.co.retail.kohlibrothers.R;
import in.co.retail.applibrary.LazyLoading.ImageLoader;

public class MultipleImageAdapter extends ArrayAdapter<MultipleImageModel>
{
    private LayoutInflater mLayoutInflater;
    private ImageLoader imageLoader;
    private Context mContext;
    private OnImageClickListener mImageClickListener;
    private int mResId;

    public MultipleImageAdapter(Context _context, int _resourceId, List<MultipleImageModel> _listModel) {
        super(_context, _resourceId, _listModel);
        // TODO Auto-generated constructor stub
        this.mContext = _context;
        this.mResId = _resourceId;
        this.mImageClickListener = (OnImageClickListener) mContext;
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
        final MultipleImageModel _hlvm = getItem(_position);
        System.out.print("\nPOSITION : " + _position + " ");
        final String ImageName = _hlvm.getImageName().toString();
        //System.out.print("P_NAME : " + ImageName);
        if (mLayoutInflater != null && _hlvm != null)
        {
            System.out.print("VIEW TO BE RETURN \n");

            // Inflate the view since it does not exist
            _convertView = mLayoutInflater.inflate(mResId, _parent, false);

            ImageView horizonImg = (ImageView) _convertView.findViewById(R.id.horizonMultipleImg);

            // set the controls image
            System.out.print("DATA "+_hlvm.getImageName().toString() + "\n");
            imageLoader.DisplayImage(_hlvm.getImageName().toString(), horizonImg);

            horizonImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mImageClickListener.onItemClick(ImageName,_position);
                }
            });/**/
        }
        else
        {
            System.out.print("NULL NOTHING TO RETURN");
        }/**/
        return _convertView;
    }

    public interface OnImageClickListener
    {
        void onItemClick( String imageName, int position);
    }
}