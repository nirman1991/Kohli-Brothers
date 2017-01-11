package in.co.retail.kohlibrothers.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.co.retail.kohlibrothers.Adapter.HorizontalListViewAdapter;
import in.co.retail.kohlibrothers.Adapter.MultipleImageAdapter;
import in.co.retail.kohlibrothers.Adapter.ProductCommonAdapter;
import in.co.retail.kohlibrothers.DBAdapter.SQLiteAdapter;
import in.co.retail.kohlibrothers.Model.Apis;
import in.co.retail.kohlibrothers.Model.LoggedUser;
import in.co.retail.kohlibrothers.Model.MultipleImageModel;
import in.co.retail.kohlibrothers.Model.ProductCommonModel;
import in.co.retail.kohlibrothers.R;
import in.co.retail.kohlibrothers.Utility.AppConstants;
import in.co.retail.kohlibrothers.Utility.Utils;
import in.co.retail.kohlibrothers.Webservice.AsyncHttpRequest;
import in.co.retail.applibrary.LazyLoading.ImageLoader;
import in.co.retail.applibrary.horizontallistview.HorizontalListView;

/**
 * Created by nirman on 4/23/2016.
 */
public class SingleProductActivity extends AppCompatActivity implements AsyncHttpRequest.RequestListener, View.OnClickListener, ProductCommonAdapter.Listener, HorizontalListViewAdapter.Listener, MultipleImageAdapter.OnImageClickListener
{
    private static String TAG = SingleProductActivity.class.getSimpleName();
    private Context mContext;
    private ImageView mProdImg, mBtnWishlist, mBtnMB, mBtnMinus, mBtnPlus;
    private TextView mMrp, mProdName, mBrandName, mDfRate, mDesign, mFit, mFabric, mSizeSelect, mColorSelect, mEscapeField, mBuyNow;
    private EditText mSelQty;
    private TableRow mDesignRow, mFabricRow, mFitRow;
    private AsyncHttpRequest mAsyncHttpRequest;
    private LinearLayout mColorSelectorLayout, mSizeSelectorLayout;



    private HorizontalListView mMultipleImageList;
    private MultipleImageAdapter mMultiImageAdapter;

    private HorizontalListView mHlvImages;
    private ImageLoader mImageLoader;

    private AppConstants.ApplicationLayoutModeAction MODE = AppConstants.ApplicationLayoutModeAction.NONE;
    private AppConstants.ApplicationLayoutModeAction mPrefListMode = AppConstants.ApplicationLayoutModeAction.NONE;

    private String mProdCode;
    private int mPos = -1, mPosition;

    JSONArray mTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_single_product);
        System.out.println("TAG : " + TAG);
        this.mContext = this;

        mProdImg = (ImageView)findViewById(R.id.productImage);
        mBtnWishlist = (ImageView)findViewById(R.id.btnWishlist);
        mBtnMB = (ImageView)findViewById(R.id.btnMonthlyBasket);
        mBtnMinus = (ImageView)findViewById(R.id.btnMinus);
        mBtnPlus = (ImageView)findViewById(R.id.btnPlus);

        mSelQty = (EditText)findViewById(R.id.selQty);

        mHlvImages = (HorizontalListView)findViewById(R.id.hlvProducts);
        mImageLoader = new ImageLoader(mContext, R.drawable.coming_soon);

        mMrp= (TextView)findViewById(R.id.mrp);
        mProdName = (TextView)findViewById(R.id.productName);
        mBrandName = (TextView)findViewById(R.id.brandName);
        mDfRate = (TextView)findViewById(R.id.dfRate);
        mDesign = (TextView)findViewById(R.id.designTxt);
        mFit = (TextView)findViewById(R.id.fitTxt);
        mFabric = (TextView)findViewById(R.id.fabricTxt);
        mSizeSelect = (TextView)findViewById(R.id.sizeSelector);
        mColorSelect = (TextView)findViewById(R.id.colorSelector);
        mEscapeField = (TextView)findViewById(R.id.escapeField);
        mBuyNow = (TextView)findViewById(R.id.btnBuyNow);

        mDesignRow = (TableRow)findViewById(R.id.designRow);
        mFitRow = (TableRow)findViewById(R.id.fitRow);
        mFabricRow = (TableRow)findViewById(R.id.fabricRow);

        mColorSelectorLayout = (LinearLayout)findViewById(R.id.colorSelectorLayout);
        mSizeSelectorLayout = (LinearLayout)findViewById(R.id.sizeSelectorLayout);

        mMultipleImageList = (HorizontalListView)findViewById(R.id.multiImageView);

        mBtnWishlist.setOnClickListener(this);
        mBtnMB.setOnClickListener(this);
        mBtnMinus.setOnClickListener(this);
        mBtnPlus.setOnClickListener(this);
        mBuyNow.setOnClickListener(this);



        initData(getIntent());
        dynamicTotal();
    }

    private void initData(Intent _intent)
    {
        if(_intent != null)
        {
            MODE = AppConstants.ApplicationLayoutModeAction.valueOf(getIntent().getExtras().getString("MODE"));
            mProdCode = getIntent().getExtras().getString("P_CODE");
            mPos = getIntent().getExtras().getInt("POS");
        }
        else
        {
            MODE = AppConstants.ApplicationLayoutModeAction.NONE;
            mProdCode = "0";
            mPos = -1;
        }
        try
        {
            JSONArray jArr = new JSONArray();
            JSONObject jObj = new JSONObject();

            jObj.put("CUSTID", LoggedUser.customer.getCustId());
            jObj.put("P_CODE", mProdCode);
            jArr.put(jObj);
            callApi(Apis.SINGLE_PRODUCT_URL, jArr, Apis.SINGLE_PRODUCT_CODE);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void dynamicTotal() {
        try
        {
            mImageLoader.DisplayImage(Apis.URL + "assets/images/products/" + mProdCode +"_"+1+ ".jpg",mProdImg);
            SQLiteAdapter db = new SQLiteAdapter(mContext);
            db.open();
            mTotal = db.getTotal();
            System.out.println("TOTAL " + mTotal.getJSONObject(0).getString("TOTAL_AMOUNT"));
            String Total = mTotal.getJSONObject(0).getString("TOTAL_AMOUNT");
            ((TextView) findViewById(R.id.total_amt)).setText(mContext.getResources().getString(R.string.totalValue) + " " + Total);
            db.close();

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    private void callApi(String _requestUrl, JSONArray _jArr, int _requestCode)
    {
        System.out.println(_jArr.toString());
        mAsyncHttpRequest = new AsyncHttpRequest(mContext, _requestUrl, _jArr, _requestCode, AsyncHttpRequest.Type.POST);
        mAsyncHttpRequest.setRequestListener(this);
        mAsyncHttpRequest.execute();
    }

    private void itemChangeInCart() {
        ProductCommonModel _prodModel = new ProductCommonModel();
        _prodModel.setProductBrCode(mProdImg.getTag().toString());
        _prodModel.setProductCode(mProdCode);
        _prodModel.setProductName(mProdName.getText().toString());
        _prodModel.setProductMRP(mMrp.getTag().toString());
        _prodModel.setProductDFRate(mDfRate.getTag().toString());
        _prodModel.setProductBalQty(mSelQty.getTag().toString());
        _prodModel.setSelectedQty(String.valueOf(mSelQty.getText().toString()));

        // save to database
        SQLiteAdapter db = new SQLiteAdapter(mContext);
        if (Integer.valueOf(db.isItemExist(Integer.valueOf(mProdCode))) > 0)
        {
            if (db.updateIndex(_prodModel))
            {
                Snackbar.make(mSelQty, "PRODUCT SUCCESSFULLY UPDATED IN CART. ", Snackbar.LENGTH_LONG).show();
                dynamicTotal();
            }
            else
                Snackbar.make(mSelQty, "PRODUCT FAILED TO BE UPDATED IN CART. ", Snackbar.LENGTH_LONG).show();
        } else {
            if (db.saveCart(_prodModel))
            {
                Snackbar.make(mSelQty, "PRODUCT ADDED TO CART SUCCESSFULLY. ", Snackbar.LENGTH_LONG).show();
                dynamicTotal();

            } else
                Snackbar.make(mSelQty, "PRODUCT FAILED TO BE ADDED IN CART. ", Snackbar.LENGTH_LONG).show();
        }
        db.close();
    }

    private void deleteItemFromCart()
    {
        // remove from database
        SQLiteAdapter db = new SQLiteAdapter(mContext);
        db.open();
        if (db.deleteCartRow(mProdCode) > 0) {
            db.close();
            Snackbar.make(mSelQty, "PRODUCT REMOVED FROM CART SUCCESSFULLY. ", Snackbar.LENGTH_LONG).show();
            dynamicTotal();
            withOutCatVisi();
        }
        else
            Snackbar.make(mSelQty, "FAILED TO REMOVE THE PRODUCT FROM CART. ", Snackbar.LENGTH_LONG).show();
    }

    private void withCartVisi()
    {
        mSelQty.setEnabled(false);
        mBtnMinus.setVisibility(View.VISIBLE);
        mBtnPlus.setVisibility(View.VISIBLE);
    }

    private void withOutCatVisi()
    {
        mSelQty.setText("1");
        mSelQty.setEnabled(true);
        mBtnMinus.setVisibility(View.GONE);
        mBtnPlus.setVisibility(View.GONE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        switch (id) {
            case android.R.id.home:
            {
                boolean status = true;
                Bundle bn = new Bundle();
                bn.putBoolean("status", status);
                bn.putInt("pos", mPos);
                Intent intent = new Intent(SingleProductActivity.this,ProductCommonActivity.class).putExtras(bn);
                startActivity(intent);
                //setResult(0,intent);

                finish();//finishing activity
            }
            break;
            default:
                System.out.println("Default block clicked on option menu click");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemChangeInCart(JSONArray jArr)
    {
        System.out.println("ITEM ADDED");
        if (jArr != null)
        {
            try
            {
                ((TextView) findViewById(R.id.total_amt)).setText(mContext.getResources().getString(R.string.totalSavings) + " " + jArr.getJSONObject(0).getString("TOTAL_SAVING"));
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            ((TextView) findViewById(R.id.total_amt)).setText(mContext.getResources().getString(R.string.totalValue) + " 0");
            //((TextView) findViewById(R.id.totalSaving)).setText(mContext.getResources().getString(R.string.totalSavings) + " 0");
        }
    }

    @Override
    public void onItemChangeInPrefList(JSONArray jArr, int position) {
        if (jArr != null) {
            mPosition = position;
            callApi(Apis.ADD_REMOVE_PREF_LIST_URL, jArr, Apis.ADD_REMOVE_PREF_LIST_CODE);
        }
    }

    @Override
    public void onProductImageClick(String productCode, int position) {

    }

    @Override
    public void onItemClick(String imageName, int position)
    {
        mImageLoader.DisplayImage(imageName,mProdImg);
    }



    private List<MultipleImageModel> getImages()
    {
        List<MultipleImageModel> mHorizontalData = new ArrayList();
        MultipleImageModel horizontalItem;
        for(int i = 1; i < 6 ; i++)
        {
            horizontalItem = new MultipleImageModel();
            try
            {
                horizontalItem.setImageName(Apis.URL + "assets/images/products/" + mProdCode +"_"+i+ ".jpg");
            } catch (Exception e) {
                e.printStackTrace();
            }
            mHorizontalData.add(horizontalItem);
        }
        return mHorizontalData;
    }

    public void left(View v)
    {
        System.out.println("LEFT FUNCTION");
        //mHorizontalScrollView.scrollBy(-400, 0);
        //mMultipleImageList.scrollTo(-50,0);
        mMultipleImageList.setSelection(0);
    }

    public void right(View v)
    {
        System.out.println("RIGHT FUNCTION");
        //mHorizontalScrollView.scrollBy(400, 0);
        //mMultipleImageList.scrollTo(50,0);
        mMultipleImageList.setSelection(1);
    }

    @Override
    public void onClick(View v)
    {
        int id = v.getId();
        switch (id)
        {
            case R.id.btnWishlist:
            {
                mPrefListMode = AppConstants.ApplicationLayoutModeAction.WISHLIST;
                JSONArray jArr = new JSONArray();
                JSONObject jObj = new JSONObject();
                try
                {
                    System.out.println("WISHLIST "+mBtnWishlist.getTag().toString());
                    if (Boolean.valueOf(mBtnWishlist.getTag().toString()))
                        jObj.put("TYPE", "REMOVE");
                    else
                        jObj.put("TYPE", "ADD");
                    jObj.put("CUSTID", LoggedUser.customer.getCustId());
                    jObj.put("PREF_LIST_ID", 0);
                    jObj.put("P_CODE", mProdCode);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                jArr.put(jObj);
                callApi(Apis.ADD_REMOVE_PREF_LIST_URL, jArr, Apis.ADD_REMOVE_PREF_LIST_CODE);

            }
            break;
            case R.id.btnMinus:
            {
                int selQty = Integer.valueOf(mSelQty.getText().toString());
                if (selQty > 1) {
                    selQty -= 1;
                    mSelQty.setText(String.valueOf(selQty));
                    itemChangeInCart();
                } else
                    Snackbar.make(mSelQty, "Can't reduce more", Snackbar.LENGTH_LONG).show();
            }
            break;
            case R.id.btnPlus:
            {
                int selQty = Integer.valueOf(mSelQty.getText().toString());
                if (selQty < Integer.valueOf(mSelQty.getTag().toString())) {
                    selQty += 1;
                    mSelQty.setText(String.valueOf(selQty));
                    itemChangeInCart();
                } else
                    Snackbar.make(mSelQty, "Can't exceed Maximun Balance Qty. (" + mSelQty.getTag().toString() + ")", Snackbar.LENGTH_LONG).show();
            }
            break;
            case R.id.btnBuyNow:
            {
                itemChangeInCart();
                Bundle bn = new Bundle();
                bn.putString("MODE", AppConstants.ApplicationLayoutModeAction.SINGLE_PRODUCT.name());
                bn.putString("P_CODE", mProdCode);
                bn.putInt("POS", mPos);
                Intent intentCart = new Intent(SingleProductActivity.this,CartActivity.class).putExtras(bn);
                startActivity(intentCart);
                finish();
            }
            break;
            case R.id.btnRemove:
            {
                deleteItemFromCart();
            }
            break;
        }
    }

    @Override
    public void onRequestCompleted(String response, int requestCode)
    {
        try {
            if (!response.equals("[]") && requestCode != 0)
            {
                JSONArray jArray = new JSONArray(response.trim());
                switch (requestCode)
                {
                    case Apis.SINGLE_PRODUCT_CODE:
                    {
                        if (jArray.getJSONObject(0).getBoolean("STATUS"))
                        {
                            JSONArray jArr = jArray.getJSONObject(0).getJSONArray("DETAILS");
                            System.out.println("BRAND "+jArr.getJSONObject(0).getString("BRAND_NAME"));
                            mProdImg.setTag(jArr.getJSONObject(0).getString("BR_CODE"));
                            mProdName.setText(jArr.getJSONObject(0).getString("P_NAME"));
                            mBrandName.setText(jArr.getJSONObject(0).getString("BRAND_NAME"));

                            if(jArr.getJSONObject(0).getString("DESIGN").toString() != "null")
                            {
                                mDesignRow.setVisibility(View.VISIBLE);
                                mDesign.setText(jArr.getJSONObject(0).getString("DESIGN"));
                            }
                            if(jArr.getJSONObject(0).getString("FIT") != "null")
                            {
                                mFitRow.setVisibility(View.VISIBLE);
                                mFit.setText(jArr.getJSONObject(0).getString("FIT"));
                            }
                            if(jArr.getJSONObject(0).getString("FABRIC") != "null")
                            {
                                mFabricRow.setVisibility(View.VISIBLE);
                                mFabric.setText(jArr.getJSONObject(0).getString("FABRIC"));
                            }
                            if(jArr.getJSONObject(0).getString("SIZ") != "null" && jArr.getJSONObject(0).getString("COLOR") != "null")
                            {
                                mSizeSelectorLayout.setVisibility(View.VISIBLE);
                                mColorSelectorLayout.setVisibility(View.VISIBLE);
                                mEscapeField.setVisibility(View.VISIBLE);
                                mSizeSelect.setText(jArr.getJSONObject(0).getString("SIZ"));
                                mColorSelect.setText(jArr.getJSONObject(0).getString("COLOR"));
                            }
                            else if(jArr.getJSONObject(0).getString("SIZ") != "null")
                            {
                                mSizeSelectorLayout.setVisibility(View.VISIBLE);
                                mSizeSelect.setText(jArr.getJSONObject(0).getString("SIZ"));
                            }
                            else if(jArr.getJSONObject(0).getString("COLOR") != "null")
                            {
                                mColorSelectorLayout.setVisibility(View.VISIBLE);
                                mColorSelect.setText(jArr.getJSONObject(0).getString("COLOR"));
                            }

                            mDfRate.setText(mContext.getResources().getString(R.string.rupees) + " " + jArr.getJSONObject(0).getString("DF_SALE_RATE"));
                            mDfRate.setTag(jArr.getJSONObject(0).getString("DF_SALE_RATE"));
                            mDfRate.setTextColor(mContext.getResources().getColor(android.R.color.holo_red_light));

                            mMrp.setTag(jArr.getJSONObject(0).getString("MRP"));
                            if (Float.valueOf(jArr.getJSONObject(0).getString("DF_SALE_RATE")) < Float.valueOf(jArr.getJSONObject(0).getString("MRP")))
                            {
                                mMrp.setText(String.valueOf(mContext.getResources().getString(R.string.rupees) + " " + jArr.getJSONObject(0).getString("MRP")));
                                mMrp.setPaintFlags(mMrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                            }
                            else
                                mMrp.setVisibility(View.GONE);

                            //for wishlist
                            if (jArr.getJSONObject(0).getString("WISHLIST").equals("1")) {
                                mBtnWishlist.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_filled_wishlist));//(mContext.getResources().getDrawable(R.drawable.ic_filled_wishlist));
                                mBtnWishlist.setTag(true);
                            }
                            else {
                                mBtnWishlist.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_empty_wishlist));
                                mBtnWishlist.setTag(false);
                            }

                            //for MonthlyBasket
                            if (jArr.getJSONObject(0).getString("MB").equals("1"))
                            {
                                mBtnMB.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_filled_shopping_basket));//(mContext.getResources().getDrawable(R.drawable.ic_filled_wishlist));
                                mBtnMB.setTag(true);
                            }
                            else {
                                mBtnMB.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_empty_shopping_basket));
                                mBtnMB.setTag(false);
                            }

                            // get selected qty of a p_code from sqlite database
                            SQLiteAdapter db = new SQLiteAdapter(mContext);
                            db.open();
                            String _selQty = db.isItemExist(Integer.valueOf(jArr.getJSONObject(0).getString("P_CODE")));
                            db.close();

                            System.out.println("QTY "+Float.valueOf(jArr.getJSONObject(0).getString("BAL_QTY")));
                            if(Float.valueOf(jArr.getJSONObject(0).getString("BAL_QTY")) > 0) {
                                ((LinearLayout) findViewById(R.id.inStockProd)).setVisibility(View.VISIBLE);
                                ((LinearLayout) findViewById(R.id.outOfStockProd)).setVisibility(View.GONE);
                                System.out.println("SELECTED QTY : " + _selQty);
                                if(Integer.valueOf(_selQty) != 0) {
                                    mSelQty.setText(_selQty);
                                    mSelQty.setTag(jArr.getJSONObject(0).getString("BAL_QTY"));
                                    withCartVisi();
                                }
                                else
                                {
                                    mSelQty.setTag(jArr.getJSONObject(0).getString("BAL_QTY"));
                                    withOutCatVisi();
                                }
                            }
                            else
                            {
                                ((LinearLayout) findViewById(R.id.outOfStockProd)).setVisibility(View.VISIBLE);
                                ((LinearLayout) findViewById(R.id.inStockProd)).setVisibility(View.GONE);
                            }

                            /*new*/

                            //mListView.setAdapter(mProdImgAdp);
                            //mListView.setLayoutManager(new LinearLayoutManager(this));
                            mMultiImageAdapter = new MultipleImageAdapter(this, R.layout.horizontal_multiple_image, getImages());
                            for (int j =0; j<getImages().size(); j++)
                            {
                                System.out.println("ADDING ADAPTER"+getImages().get(j));
                            }

                            mHlvImages.setAdapter(mMultiImageAdapter);
                            //mMultiImageAdapter = new MultipleImageAdapter(this, R.layout.horizontal_multiple_image, getImages());
                            //System.out.println("HLV ADAPTER");
                            //mMultipleImageList.setAdapter(mMultiImageAdapter);
                        }
                        else
                            Snackbar.make(mSelQty, "PRODUCT DETAILS NOT FETCHED. TRY AGAIN !!!", Snackbar.LENGTH_SHORT).show();

                    }
                    break;
                    case Apis.ADD_REMOVE_PREF_LIST_CODE:
                    {
                        if (!jArray.getJSONObject(0).has("ERROR")) {
                            if (jArray.getJSONObject(0).getBoolean("STATUS")) {
                                System.out.println("SELECTED PREF LIST MODE : " + mPrefListMode.name());

                                switch(mPrefListMode)
                                {
                                    case WISHLIST: {
                                        System.out.println("WISHLIST "+mBtnWishlist.getTag().toString());
                                        if (!Boolean.valueOf(mBtnWishlist.getTag().toString())) {
                                            //mStatus = true;
                                            mBtnWishlist.setTag(true);
                                            mBtnWishlist.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_filled_wishlist));
                                            Snackbar.make(mBtnWishlist, "PRODUCT SUCCESSFULLY ADDED TO WISHLIST", Snackbar.LENGTH_SHORT).show();
                                        } else {
                                            //mStatus = false;
                                            mBtnWishlist.setTag(false);
                                            mBtnWishlist.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_empty_wishlist));
                                            Snackbar.make(mBtnWishlist, "PRODUCT SUCCESSFULLY REMOVED", Snackbar.LENGTH_SHORT).show();
                                        }
                                    }
                                    break;
                                    case MONTHLY_BASKET:{
                                        if (!Boolean.valueOf(mBtnMB.getTag().toString())) {
                                            //mStatus = true;
                                            mBtnMB.setTag(true);
                                            mBtnMB.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_filled_shopping_basket));
                                            Snackbar.make(mBtnMB, "PRODUCT SUCCESSFULLY ADDED TO MONTHLY BASKET", Snackbar.LENGTH_SHORT).show();
                                        } else {
                                            //mStatus = false;
                                            mBtnMB.setTag(false);
                                            mBtnMB.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_empty_shopping_basket));
                                            Snackbar.make(mBtnMB, "PRODUCT SUCCESSFULLY REMOVED", Snackbar.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            }
                            else
                                Snackbar.make(mBtnMB, "NO PRODUCTS FETCHED. TRY AGAIN !!!", Snackbar.LENGTH_SHORT).show();
                        }
                        else
                            Snackbar.make(mBtnMB, jArray.getJSONObject(0).getString("ERROR"), Snackbar.LENGTH_SHORT).show();
                    }
                    break;
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestError(Exception e, int requestCode)
    {
        Utils.showToast(this,"Please check your Internet Connection");
    }

    @Override
    public void onRequestStarted(int requestCode)
    {

    }


    @Override
    public void onHorizontalListClick(String productCode, int pos) {

    }
}
