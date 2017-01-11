package in.co.retail.kohlibrothers.Adapter;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import in.co.retail.kohlibrothers.DBAdapter.SQLiteAdapter;
import in.co.retail.kohlibrothers.Model.Apis;
import in.co.retail.kohlibrothers.Model.LoggedUser;
import in.co.retail.kohlibrothers.Model.ProductCommonModel;
import in.co.retail.kohlibrothers.R;
import in.co.retail.kohlibrothers.Utility.AppConstants;
import in.co.retail.applibrary.LazyLoading.ImageLoader;


/**
 * Created by Niha on 1/7/2016.
 */
public class ProductCommonAdapter extends RecyclerView.Adapter<ProductCommonAdapter.ProductViewHolder>
{
    List<ProductCommonModel> mProdModelList;
    private LayoutInflater mInflater;
    private Context mContext;
    private ImageLoader imageLoader;
    private Listener mListener;
    private SQLiteAdapter db;
    private AppConstants.ApplicationLayoutModeAction mMode = AppConstants.ApplicationLayoutModeAction.NONE;

    private int selQty = 0;

    public ProductCommonAdapter(Context context, Listener listener, List<ProductCommonModel> data, AppConstants.ApplicationLayoutModeAction _mode)
    {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.mProdModelList = data;
        this.imageLoader = new ImageLoader(mContext, R.drawable.coming_soon);
        this.mListener = listener;
        this.mMode = _mode;
        this.db = new SQLiteAdapter(mContext);
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = mInflater.inflate(R.layout.product_list_row, null);//parent, false);
        ProductViewHolder holder = new ProductViewHolder(view);
        return holder;
    }/**/

    @Override
    public void onBindViewHolder(final ProductViewHolder holder, final int position)
    {
        final ProductCommonModel _current = mProdModelList.get(position);//dataList.get(position);
        // set the controls image
        imageLoader.DisplayImage(Apis.URL + "assets/images/products/" + _current.getProductCode() +"_"+1+ ".jpg", holder.prodImg);
        holder.prodName.setText(_current.getProductName());
        //holder.brandName.setText(_current.getBrandName());

        holder.dfRate.setText(mContext.getResources().getString(R.string.rupees) + " " + _current.getProductDFRate());
        holder.dfRate.setTextColor(mContext.getResources().getColor(android.R.color.holo_red_light));
        holder.dfRate.setTypeface(null, Typeface.BOLD);

        if (Float.valueOf(_current.getProductDFRate()) < Float.valueOf(_current.getProductMRP())) {
            holder.mrp.setText(String.valueOf(mContext.getResources().getString(R.string.rupees) + " " + _current.getProductMRP()));
            holder.mrp.setPaintFlags(holder.mrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        else
            holder.mrp.setVisibility(View.GONE);

        // for wishlist and monthly basket image visibility
        switch (mMode)
        {
            case WISHLIST:
            {
                holder.btnWishlist.setVisibility(View.VISIBLE);
                holder.btnMonthlyBasket.setVisibility(View.GONE);
            }
            break;
            case MONTHLY_BASKET:
            {
                holder.btnWishlist.setVisibility(View.GONE);
                holder.btnMonthlyBasket.setVisibility(View.VISIBLE);
            }
            break;
            default:
            {
                holder.btnWishlist.setVisibility(View.GONE);
                holder.btnMonthlyBasket.setVisibility(View.GONE);
            }
        }

        // get selected qty of a p_code from sqlite database
        db.open();
        String _selQty = db.isItemExist(Integer.valueOf(_current.getProductCode()));
        db.close();

        if(Float.valueOf(_current.getProductBalQty()) > 0) {
            holder.inStockProd.setVisibility(View.VISIBLE);
            holder.outOfStockProd.setVisibility(View.GONE);

            switch(mMode)
            {
                case SEARCH:
                case PRODUCT:
                case WISHLIST:
                case MONTHLY_BASKET:
                {
                    if(Integer.valueOf(_selQty) != 0) {
                        holder.selQty.setText(_selQty);
                        inActWithCartValView(holder, mMode);
                    }
                    else
                    {
                        inActWithOutCartView(holder, mMode);
                    }
                }
                break;
                case CART:
                case REVIEW_ORDER:
                {
                    holder.selQty.setText(_selQty);
                    inActWithCartValView(holder, mMode);
                }
                break;
            }
        }
        else {
            holder.inStockProd.setVisibility(View.GONE);
            holder.outOfStockProd.setVisibility(View.VISIBLE);
        }

        holder.btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selQty = Integer.valueOf(holder.selQty.getText().toString());
                if (Integer.valueOf(holder.selQty.getText().toString()) > 1) {
                    selQty -= 1;
                    holder.selQty.setText(String.valueOf(selQty));
                    itemChangeInCart(_current, holder);
                } else
                    Snackbar.make(holder.selQty, "Can't reduce more", Snackbar.LENGTH_LONG).show();
            }
        });

        holder.btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selQty = Integer.valueOf(holder.selQty.getText().toString());
                if (Integer.valueOf(holder.selQty.getText().toString()) < Integer.valueOf(_current.getProductBalQty())) {
                    selQty += 1;
                    holder.selQty.setText(String.valueOf(selQty));
                    itemChangeInCart(_current, holder);
                } else
                    Snackbar.make(holder.selQty, "Can't exceed Maximun Balance Qty. " + _current.getProductBalQty(), Snackbar.LENGTH_LONG).show();
            }
        });

        holder.btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.selQty.getText().toString().equals(""))
                    Snackbar.make(holder.selQty, "Empty Quantity can't be added to Cart. ", Snackbar.LENGTH_LONG).show();
                else if(Integer.valueOf(holder.selQty.getText().toString()) == 0)
                    Snackbar.make(holder.selQty, "Zero Quantity can't be added to Cart. ", Snackbar.LENGTH_LONG).show();
                else if (Integer.valueOf(holder.selQty.getText().toString()) > Integer.valueOf(_current.getProductBalQty())) {
                    holder.selQty.setText("1");
                    Snackbar.make(holder.selQty, "Can't exceed Maximun Balance Qty. " + _current.getProductBalQty(), Snackbar.LENGTH_LONG).show();
                } else
                    itemChangeInCart(_current, holder);
            }
        });

        holder.btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.open();
                if (db.deleteCartRow(_current.getProductCode()) > 0) {
                    JSONArray jArr = db.getTotal();
                    db.close();
                    System.out.println("PRODUCT REMOVED FROM CART SUCCESSFULLY. ");
                    holder.selQty.setText("1");
                    switch (mMode) {
                        case PRODUCT:
                        case MONTHLY_BASKET:
                        case SEARCH:
                        {
                            inActWithOutCartView(holder, mMode);
                            mListener.onItemChangeInCart(jArr);
                        }
                        break;
                        case CART:
                        case REVIEW_ORDER: {
                            if (removeItem(position))
                                Snackbar.make(holder.selQty, "PRODUCT REMOVED FROM CART SUCCESSFULLY. ", Snackbar.LENGTH_LONG).show();
                            else
                                Snackbar.make(holder.selQty, "PRODUCT COULDN'T BE REMOVED FROM CART. TRY AGAIN !!! ", Snackbar.LENGTH_LONG).show();
                            mListener.onItemChangeInCart(jArr);
                        }
                        break;
                        case WISHLIST:
                        {
                            inActWithOutCartView(holder, mMode);
                        }
                        break;
                    }
                }
            }
        });

        holder.btnWishlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeInPrefList(_current.getProductCode(), position);
            }
        });

        holder.btnMonthlyBasket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeInPrefList(_current.getProductCode(), position);
            }
        });

        //for image onClick event
        holder.prodImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onProductImageClick(_current.getProductCode(), position);
            }
        });

        //for image onClick event
        holder.prodName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onProductImageClick(_current.getProductCode(), position);
            }
        });
    }

    private void changeInPrefList(String productCode, int position)
    {
        JSONArray jArr = new JSONArray();
        JSONObject jObj = new JSONObject();
        try {
            jObj.put("TYPE", "REMOVE");
            jObj.put("CUSTID", LoggedUser.customer.getCustId());
            jObj.put("PREF_LIST_ID", mMode == AppConstants.ApplicationLayoutModeAction.WISHLIST ? 0 : 1);
            jObj.put("P_CODE", productCode);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        jArr.put(jObj);
        mListener.onItemChangeInPrefList(jArr, position);
    }

    public boolean removeItem(int position) {
        if (mProdModelList.size() >= position + 1) {
            //removing datalist
            mProdModelList.remove(position);

            //removing views
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, getItemCount());

            return true;
        }
        return false;
    }

    public void refreshPosition(int pos)
    {
        notifyItemChanged(pos);
        /*for(int i = 0; i < mProdModelList.size(); i++)
        {
            notifyItemChanged(i);
        }/**/
    }

    public void refreshAll()
    {
        for(int i = 0; i < mProdModelList.size(); i++)
        {
            notifyItemChanged(i);
        }/**/
    }

    private void inActWithCartValView(ProductViewHolder _holder, AppConstants.ApplicationLayoutModeAction _mode)
    {
        _holder.selQty.setEnabled(false);
        _holder.btnMinus.setVisibility(View.VISIBLE);
        _holder.btnPlus.setVisibility(View.VISIBLE);
        _holder.btnAddToCart.setVisibility(View.GONE);
        _holder.btnRemove.setVisibility(View.VISIBLE);
    }

    private void inActWithOutCartView(ProductViewHolder _holder, AppConstants.ApplicationLayoutModeAction _mode)
    {
        _holder.selQty.setEnabled(true);
        _holder.btnMinus.setVisibility(View.GONE);
        _holder.btnPlus.setVisibility(View.GONE);
        _holder.btnAddToCart.setVisibility(View.VISIBLE);
        _holder.btnRemove.setVisibility(View.GONE);
    }

    private void itemChangeInCart(ProductCommonModel _current, ProductViewHolder _holder) {
        ProductCommonModel _prodModel = new ProductCommonModel();
        _prodModel.setProductBrCode(_current.getProductBrCode());
        _prodModel.setProductCode(_current.getProductCode());
        _prodModel.setProductName(_current.getProductName());
        _prodModel.setProductMRP(_current.getProductMRP());
        _prodModel.setProductDFRate(_current.getProductDFRate());
        _prodModel.setProductBalQty(_current.getProductBalQty());
        _prodModel.setSelectedQty(String.valueOf(_holder.selQty.getText()));

        // save to database
        if (Integer.valueOf(db.isItemExist(Integer.valueOf(_current.getProductCode()))) > 0) {
            if (db.updateIndex(_prodModel)) {
                inActWithCartValView(_holder, mMode);
                Snackbar.make(_holder.selQty, "PRODUCT SUCCESSFULLY UPDATED IN CART. ", Snackbar.LENGTH_LONG).show();
            } else
                Snackbar.make(_holder.selQty, "PRODUCT FAILED TO BE UPDATED IN CART. ", Snackbar.LENGTH_LONG).show();
        } else {
            if (db.saveCart(_prodModel)) {
                inActWithCartValView(_holder, mMode);
                Snackbar.make(_holder.selQty, "PRODUCT ADDED TO CART SUCCESSFULLY. ", Snackbar.LENGTH_LONG).show();
            } else
                Snackbar.make(_holder.selQty, "PRODUCT FAILED TO BE ADDED IN CART. ", Snackbar.LENGTH_LONG).show();
        }
        JSONArray jArr = db.getTotal();
        db.close();

        switch (mMode) {
            // No action for Review Order And WishList
            case PRODUCT:
            case CART:
            case MONTHLY_BASKET:
            case REVIEW_ORDER:
            case SEARCH: {
                mListener.onItemChangeInCart(jArr);
            }
            break;
        }
    }

    @Override
    public int getItemCount()
    {
        return mProdModelList.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder
    {
        ImageView prodImg;
        TextView brandName, prodName, dfRate, mrp;

        LinearLayout inStockProd, outOfStockProd;

        EditText selQty;
        ImageView btnMinus, btnPlus, btnAddToCart, btnRemove, btnMonthlyBasket, btnWishlist;

        public ProductViewHolder(View itemView)
        {
            super(itemView);
            prodImg = (ImageView) itemView.findViewById(R.id.productImage);
            prodName = (TextView) itemView.findViewById(R.id.productName);
            brandName = (TextView) itemView.findViewById(R.id.brandName);
            dfRate = (TextView) itemView.findViewById(R.id.dfRate);
            mrp = (TextView) itemView.findViewById(R.id.mrp);

            inStockProd = (LinearLayout) itemView.findViewById(R.id.inStockProd);
            outOfStockProd = (LinearLayout) itemView.findViewById(R.id.outOfStockProd);

            selQty = (EditText) itemView.findViewById(R.id.selQty);

            btnMinus = (ImageView) itemView.findViewById(R.id.btnMinus);
            btnPlus = (ImageView) itemView.findViewById(R.id.btnPlus);
            btnAddToCart = (ImageView) itemView.findViewById(R.id.btnAddToCart);
            btnRemove = (ImageView) itemView.findViewById(R.id.btnRemove);
            btnMonthlyBasket = (ImageView) itemView.findViewById(R.id.btnMonthlyBasket);
            btnWishlist = (ImageView) itemView.findViewById(R.id.btnWishlist);
        }
    }

    public interface Listener
    {
        void onItemChangeInCart(JSONArray jArr);
        void onItemChangeInPrefList(JSONArray jArr, int position);
        void onProductImageClick(String productCode, int position);
    }
}