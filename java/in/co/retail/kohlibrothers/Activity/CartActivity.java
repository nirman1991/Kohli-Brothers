package in.co.retail.kohlibrothers.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import in.co.retail.kohlibrothers.Adapter.OrderDetailsAdapter;
import in.co.retail.kohlibrothers.Adapter.ProductCommonAdapter;
import in.co.retail.kohlibrothers.Controller.ActionBarHelper;
import in.co.retail.kohlibrothers.DBAdapter.SQLiteAdapter;
import in.co.retail.kohlibrothers.Model.Apis;
import in.co.retail.kohlibrothers.Model.CartModel;
import in.co.retail.kohlibrothers.Model.LoggedUser;
import in.co.retail.kohlibrothers.Model.ProductCommonModel;
import in.co.retail.kohlibrothers.R;
import in.co.retail.kohlibrothers.Utility.AppConstants;
import in.co.retail.kohlibrothers.Utility.Loading;
import in.co.retail.kohlibrothers.Utility.Utils;
import in.co.retail.kohlibrothers.Webservice.AsyncHttpRequest;
import in.co.retail.applibrary.searchview.MaterialSearchView;

/**
 * Created by nirman on 4/27/2016.
 */
public class CartActivity extends AppCompatActivity implements View.OnClickListener, AsyncHttpRequest.RequestListener,ProductCommonAdapter.Listener
{
    private static String TAG = CartActivity.class.getSimpleName();
    private Context mContext;
    private Toolbar mToolbar;
    private Button mBtnCart, mBtnPrev, mBtnNext;
    private RecyclerView mListView;
    private Loading mLoading;
    private CartModel mCartModel;
    private ProductCommonAdapter mProductAdapter;
    private MaterialSearchView mSearchView;
    private AsyncHttpRequest mAsyncHttpRequest;
    private SQLiteAdapter db;
    private TableLayout mOrderIdLayout, mOrderDetailsLayout, mDeliveryChargesLayout;
    private TextView mOrderIdtxt, mDelChargestxt;
    private OrderDetailsAdapter mOrderDetAdapter;

    private AppConstants.ApplicationLayoutModeAction MODE = AppConstants.ApplicationLayoutModeAction.NONE;

    private String mHeading = "", mOrderId, mDelCharges, mOrderTotal = "0";
    private Integer txtSize;

    boolean searchStatus = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_common);
        System.out.println("TAG : " + TAG);
        mListView = (RecyclerView) findViewById(R.id.mListItem);
        mBtnCart = (Button) findViewById(R.id.btnCart);

        initData(getIntent());
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        this.mContext = this;

        mOrderIdLayout = (TableLayout) findViewById(R.id.orderIdLayout);
        mOrderDetailsLayout = (TableLayout) findViewById(R.id.detailsLayout);
        mDeliveryChargesLayout = (TableLayout) findViewById(R.id.deliveryLayout);

        mOrderIdtxt = (TextView) findViewById(R.id.orderId);
        mDelChargestxt = (TextView) findViewById(R.id.delCharges);

        mBtnNext = (Button) findViewById(R.id.btnNext);
        mBtnPrev = (Button) findViewById(R.id.btnPrev);

        mBtnPrev.setVisibility(View.GONE);
        mBtnNext.setVisibility(View.GONE);
        /* for materialized search view */
        mSearchView = (MaterialSearchView) findViewById(R.id.search_view);
        initSearchData();

        mBtnCart.setText(" CHECKOUT >> ");
        mBtnCart.setTextSize(txtSize);
        mBtnCart.setOnClickListener(this);

        ActionBarHelper.init(this, mToolbar, true, true, mHeading, false);

        setData();
    }

    private void initData(Intent intent) {
        if (intent != null)
        {
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int _dpi = metrics.densityDpi;

            switch(_dpi)
            {
                case DisplayMetrics.DENSITY_MEDIUM:
                    txtSize = 20;
                    break;
                case DisplayMetrics.DENSITY_HIGH:
                    txtSize = 12;
                    break;
                case DisplayMetrics.DENSITY_XHIGH:
                    txtSize = 15;
                    break;
                case DisplayMetrics.DENSITY_XXHIGH:
                    txtSize = 15;
                    break;
                case DisplayMetrics.DENSITY_XXXHIGH:
                    txtSize = 15;
                    break;
            }

            MODE = AppConstants.ApplicationLayoutModeAction.valueOf(intent.getExtras().getString("MODE", AppConstants.ApplicationLayoutModeAction.NONE.name()));
            switch(MODE)
            {
                case CART:
                case SINGLE_PRODUCT:
                {
                    System.out.println("CART OVERVIEW");
                    mHeading = "CART OVERVIEW";
                    ((TextView) findViewById(R.id.totalValue)).setTypeface(null, Typeface.BOLD);
                }
                break;
                case REVIEW_ORDER:
                {
                    System.out.println("REVIEW ORDER");
                    mHeading = "REVIEW ORDER";

                    ((TextView) findViewById(R.id.totalSaving)).setTypeface(null, Typeface.BOLD);
                    ((TextView) findViewById(R.id.totalValue)).setTypeface(null, Typeface.BOLD);
                    mBtnCart.setVisibility(View.GONE);
                }
                break;
                case ORDER_DETAILS:
                {
                    System.out.println("CASH MEMO");
                    mHeading = "CASH MEMO";

                    ((TextView) findViewById(R.id.totalSaving)).setTypeface(null, Typeface.BOLD);
                    ((TextView) findViewById(R.id.totalValue)).setTypeface(null, Typeface.BOLD);
                    mBtnCart.setVisibility(View.GONE);
                }
                break;
            }
        }
    }

    private void setData()
    {
        MODE = AppConstants.ApplicationLayoutModeAction.valueOf(getIntent().getExtras().getString("MODE", AppConstants.ApplicationLayoutModeAction.NONE.name()));
        switch(MODE)
        {
            case CART:
            case REVIEW_ORDER:
            case SINGLE_PRODUCT:
            {
                ((TextView) findViewById(R.id.txtHeading)).setText(mHeading);
                db = new SQLiteAdapter(mContext);
                db.open();
                mCartModel = db.getCartItem();
                db.close();
                //
                if (mCartModel != null)
                {
                    ((TextView) findViewById(R.id.totalValue)).setText(mContext.getResources().getString(R.string.totalValue) + " " + mCartModel.getmTotal());
                    ((TextView) findViewById(R.id.totalSaving)).setText(mContext.getResources().getString(R.string.totalSavings) + " " + mCartModel.getmDiscount());
                    List<ProductCommonModel> _prodModelList = mCartModel.getProductItemList();
                    mProductAdapter = new ProductCommonAdapter(mContext, this, _prodModelList, MODE);
                    mListView.setAdapter(mProductAdapter);
                    mListView.setLayoutManager(new LinearLayoutManager(this));
                } else {
                    finish();
                    ((TextView) findViewById(R.id.totalValue)).setText(mContext.getResources().getString(R.string.totalValue) + " 0");
                    ((TextView) findViewById(R.id.totalSaving)).setText(mContext.getResources().getString(R.string.totalSavings) + " 0");
                }
            }
            break;
            case  ORDER_DETAILS:
            {
                ((TextView) findViewById(R.id.txtHeading)).setText(mHeading);
                mOrderIdLayout.setVisibility(View.VISIBLE);
                mOrderDetailsLayout.setVisibility(View.VISIBLE);
                mDeliveryChargesLayout.setVisibility(View.VISIBLE);

                mOrderId = getIntent().getExtras().getString("ORDER_ID");
                mOrderIdtxt.setText(mOrderId);

                mDelCharges = getIntent().getExtras().getString("DEL_CHARGES");
                mDelChargestxt.setText(mDelCharges);

                JSONArray jArr = new JSONArray();
                JSONObject jObj =  new JSONObject();
                try
                {
                    jObj.put("ORDER_ID",mOrderId);
                    jArr.put(jObj);
                    callApi(Apis.TRACK_ORDER_URL, jArr, Apis.TRACK_ORDER_CODE);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                ((TextView) findViewById(R.id.totalValue)).setText(mContext.getResources().getString(R.string.totalValue) + " " +getIntent().getExtras().getString("ORDER_TOTAL"));
            }
        }
    }

    private void initSearchData()
    {
        mSearchView.setVoiceSearch(true);
        mSearchView.setCursorDrawable(R.drawable.color_cursor_white);

        mSearchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchStatus = true;
                Bundle bn = new Bundle();
                bn.putString("MODE", AppConstants.ApplicationLayoutModeAction.SEARCH.name());
                bn.putString("KEY", query);
                bn.putString("TYPE", "OR");
                bn.putBoolean("STATUS", true);
                if (MODE.equals(AppConstants.ApplicationLayoutModeAction.SEARCH))
                    finish();
                Intent intentSearch = new Intent(CartActivity.this, ProductCommonActivity.class).putExtras(bn);
                startActivityForResult(intentSearch, 1);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                System.out.println("TEXT CHANGE : " + newText + "\nLENGTH : " + newText.length());
                searchStatus = false;
                JSONArray jArr = new JSONArray();
                JSONObject jObj = new JSONObject();
                try {
                    jObj.put("P_NAME", "ENTER UPTO 3 CHARACTERS TO SEARCH");
                    jObj.put("P_CODE", "0");
                    jObj.put("BR_CODE", "0");
                    jObj.put("URL", "");
                    jArr.put(jObj);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mSearchView.setSuggestions(jArr);
                if (newText.length() >= 3) {
                    jArr = new JSONArray();
                    jObj = new JSONObject();
                    try {
                        jObj.put("TYPE", "OR");
                        jObj.put("KEY", newText);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    jArr.put(jObj);
                    callApi(Apis.SEARCH_LIST_URL, jArr, Apis.SEARCH_LIST_CODE);
                }
                return false;
            }

            @Override
            public boolean onQueryTextClick(String text) {
                //System.out.println("TEXT CHANGE : " + text);
                String productCode = "";
                try {
                    JSONObject jObj = new JSONObject(text);
                    productCode = jObj.getString("P_CODE");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Bundle bn = new Bundle();
                bn.putString("P_CODE", productCode);
                bn.putString("MODE", AppConstants.ApplicationLayoutModeAction.NONE.name());
                bn.putInt("POS", -1);
                Intent intentSearch = new Intent(CartActivity.this, SingleProductActivity.class).putExtras(bn);
                startActivityForResult(intentSearch, 1);
                return false;
            }
        });

        mSearchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //Do some magic
                System.out.println("onSearchViewShown.");
            }

            @Override
            public void onSearchViewClosed() {
                //Do some magic
                System.out.println("onSearchViewClosed.");
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
            {
                System.out.println("MODE "+MODE.name());
                switch (MODE)
                {
                    case SINGLE_PRODUCT:
                        System.out.println("single product");
                        Bundle bn = new Bundle();
                        bn.putString("MODE", AppConstants.ApplicationLayoutModeAction.CART.name());
                        bn.putString("P_CODE", getIntent().getExtras().getString("P_CODE"));
                        bn.putInt("POS", getIntent().getExtras().getInt("POS"));
                        Intent intentSingleProd = new Intent(CartActivity.this,SingleProductActivity.class).putExtras(bn);
                        startActivity(intentSingleProd);
                        finish();
                    default:
                        Intent intent=new Intent();
                        setResult(0,intent);
                        finish();
                        break;
                }

            }
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mSearchView.isSearchOpen()) {
            mSearchView.closeSearch();
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        mSearchView.setMenuItem(item);

        menu.findItem(R.id.action_overflow).setVisible(false);
        menu.findItem(R.id.action_cart).setVisible(false);

        return true;
    }

    /**
     * Dispatch incoming result to the correct fragment.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        setData();
    }

    private void callApi(String _requestUrl, JSONArray _jArr, int _requestCode)
    {
        mAsyncHttpRequest = new AsyncHttpRequest(mContext, _requestUrl, _jArr, _requestCode, AsyncHttpRequest.Type.POST);
        mAsyncHttpRequest.setRequestListener(this);
        mAsyncHttpRequest.execute();
    }

    @Override
    public void onClick(View v)
    {
        if (v.getId() == R.id.btnCart) // checkout button
        {
            if(LoggedUser.customer.getCustName1().equals(""))
            {
                Bundle bn = new Bundle();
                bn.putInt("TYPE", 2);
                bn.putBoolean("EXIT", false);
                Intent intent = new Intent(CartActivity.this, UserInfoActivity.class).putExtras(bn);
                startActivity(intent);
            }
            else
            {
                Bundle bn = new Bundle();
                bn.putString("ADD_TYPE", "PRIMARY");
                Intent intent = new Intent(CartActivity.this, DeliverySpecsActivity.class).putExtras(bn);
                startActivity(intent);
            }
        }
    }

    @Override
    public void onItemChangeInCart(JSONArray jArr) {
        if(jArr != null)
        {
            try
            {
                ((TextView) findViewById(R.id.totalValue)).setText(mContext.getResources().getString(R.string.totalValue) + " " + jArr.getJSONObject(0).getString("TOTAL_AMOUNT"));
                ((TextView) findViewById(R.id.totalSaving)).setText(mContext.getResources().getString(R.string.totalSavings) + " " + jArr.getJSONObject(0).getString("TOTAL_SAVING"));
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }

        }
        else
        {
            ((TextView) findViewById(R.id.totalValue)).setText(mContext.getResources().getString(R.string.totalValue) + " 0");
            ((TextView) findViewById(R.id.totalSaving)).setText(mContext.getResources().getString(R.string.totalSavings) + " 0");/**/
        }
    }

    @Override
    public void onItemChangeInPrefList(JSONArray jArr, int position) {

    }

    @Override
    public void onProductImageClick(String productCode, int position) {

    }

    @Override
    public void onRequestCompleted(String response, int requestCode)
    {
        try
        {
            if(!response.equals("[]") && requestCode != 0) {
                JSONArray jArray = new JSONArray(response.trim());
                switch(requestCode) {
                    case Apis.SEARCH_LIST_CODE: {
                        JSONArray jTemp = new JSONArray();
                        if (!jArray.getJSONObject(0).has("ERROR")) {
                            if (jArray.getJSONObject(0).has("COUNT")) {
                                JSONArray jArr = jArray.getJSONObject(0).getJSONArray("LIST");
                                for (int i = 0; i < jArr.length(); i++) {
                                    String url = Apis.URL + "assets/products/" + jArr.getJSONObject(i).getString("P_CODE") + ".jpg";
                                    JSONObject jObj = new JSONObject();
                                    jObj.put("P_NAME", jArr.getJSONObject(i).getString("P_NAME"));
                                    jObj.put("P_CODE", jArr.getJSONObject(i).getString("P_CODE"));
                                    jObj.put("BR_CODE", jArr.getJSONObject(i).getString("BR_CODE"));
                                    jObj.put("URL", url);
                                    jTemp.put(jObj);
                                }
                            }
                        } else {
                            JSONObject jObj = new JSONObject();
                            jObj.put("P_NAME", jArray.getJSONObject(0).getString("ERROR"));
                            jObj.put("P_CODE", "0");
                            jObj.put("BR_CODE", "0");
                            jObj.put("URL", "");
                            jTemp.put(jObj);
                        }
                        this.mSearchView.setSuggestions(jTemp);
                        mLoading.dismiss();
                    }
                    break;
                    case Apis.TRACK_ORDER_CODE: {
                        JSONArray jTemp = new JSONArray();
                        if (!jArray.getJSONObject(0).has("ERROR"))
                        {
                            JSONArray jArr = jArray.getJSONObject(0).getJSONArray("ORDER_DETAILS");
                            mOrderDetAdapter = new OrderDetailsAdapter(mContext, jArr, MODE);
                            mListView.setAdapter(mOrderDetAdapter);
                            mListView.setLayoutManager(new LinearLayoutManager(this));
                        }
                        mLoading.dismiss();
                    }
                    break;
                }
            }

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestError(Exception e, int requestCode) {
        Utils.showToast(this, "Please check your Internet Connection");
        mLoading.hide();
    }

    @Override
    public void onRequestStarted(int requestCode) {
        mLoading = new Loading(this);
        mLoading.show();
    }
}
