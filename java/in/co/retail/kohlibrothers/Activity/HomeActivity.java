package in.co.retail.kohlibrothers.Activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.speech.RecognizerIntent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import in.co.retail.kohlibrothers.Adapter.HorizontalListViewAdapter;
import in.co.retail.kohlibrothers.Controller.ActionBarHelper;

import in.co.retail.kohlibrothers.DBAdapter.SQLiteAdapter;
import in.co.retail.kohlibrothers.Model.Apis;
import in.co.retail.kohlibrothers.Model.CatTypeNavItem;
import in.co.retail.kohlibrothers.Model.CategoryItem;
import in.co.retail.kohlibrothers.Model.LoggedUser;
import in.co.retail.kohlibrothers.Model.ProductCommonModel;
import in.co.retail.kohlibrothers.R;
import in.co.retail.kohlibrothers.Utility.AppConstants;
import in.co.retail.kohlibrothers.Utility.Loading;
import in.co.retail.kohlibrothers.Utility.Utils;
import in.co.retail.kohlibrothers.Webservice.AsyncHttpRequest;
import in.co.retail.applibrary.horizontallistview.HorizontalListView;
import in.co.retail.applibrary.imageslider.Animations.DescriptionAnimation;
import in.co.retail.applibrary.imageslider.Layout.SliderLayout;
import in.co.retail.applibrary.imageslider.SliderTypes.BaseSliderView;
import in.co.retail.applibrary.imageslider.SliderTypes.TextSliderView;
import in.co.retail.applibrary.searchview.MaterialSearchView;

/**
 * Created by nirman on 4/20/2016.
 */
public class HomeActivity extends AppCompatActivity implements AsyncHttpRequest.RequestListener, View.OnClickListener, CatTypeFragmentDrawer.CatTypeFragmentListener, BaseSliderView.OnSliderClickListener, HorizontalListViewAdapter.Listener
{
    private static String TAG = HomeActivity.class.getSimpleName();
    private Context mContext;
    private Toolbar mToolbar;
    private CatTypeFragmentDrawer mFragmentDrawer;
    private Loading mLoading;
    private SliderLayout mSliderLayout;
    private HorizontalListView mHorizontalProductListView,mHorizontalSchoolListView,mHorizontalCosmeticsListView,mHorizontalDecorListView;
    private HorizontalListViewAdapter mHlvAdapter;

    private AsyncHttpRequest mAsyncHttpRequest;

    private MaterialSearchView mSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        System.out.println("TAG : " + TAG);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        ActionBarHelper.init(this, mToolbar, true, false, "HOME", false);
        this.mContext = this;

        mFragmentDrawer = (CatTypeFragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);

        //System.out.println("GET CHARGES 1 "+ LoggedUser.customer.getDelCharges1());
        //System.out.println("GET CHARGES 2 "+ LoggedUser.customer.getDelCharges2());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);
        mSliderLayout = (SliderLayout) findViewById(R.id.slider);
        mHorizontalProductListView = (HorizontalListView)findViewById(R.id.hlvProducts);
        mHorizontalSchoolListView = (HorizontalListView)findViewById(R.id.hlvSchoolProducts);
        mHorizontalCosmeticsListView = (HorizontalListView)findViewById(R.id.hlvCosmeticsProducts);
        mHorizontalDecorListView = (HorizontalListView)findViewById(R.id.hlvHomeProducts);

        ((TextView) findViewById(R.id.new_arrivals_products)).setOnClickListener(this);
        ((TextView) findViewById(R.id.school_products)).setOnClickListener(this);
        ((TextView) findViewById(R.id.cosmetics_products)).setOnClickListener(this);
        ((TextView) findViewById(R.id.home_products)).setOnClickListener(this);
        /* for materialized search view */
        mSearchView = (MaterialSearchView) findViewById(R.id.search_view);
        initSearchData();
        initSetData();/**/
    }

    private void initSearchData()
    {
        mSearchView.setVoiceSearch(true);
        mSearchView.setCursorDrawable(R.drawable.color_cursor_white);

        mSearchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                /*Snackbar.make(findViewById(R.id.toolbar), "Query: " + query, Snackbar.LENGTH_LONG)
                        .show();/**/
                Bundle bn = new Bundle();
                bn.putString("MODE", AppConstants.ApplicationLayoutModeAction.SEARCH.name());
                bn.putString("KEY", query);
                bn.putString("TYPE", "OR");
                bn.putBoolean("STATUS", true);
                Intent intentSearch = new Intent(HomeActivity.this, ProductCommonActivity.class).putExtras(bn);
                startActivity(intentSearch);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                System.out.println("TEXT CHANGE : " + newText + "\nLENGTH : " + newText.length());

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
                if (newText.length() == 3) {
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
                Intent intentSearch = new Intent(HomeActivity.this, SingleProductActivity.class).putExtras(bn);
                startActivity(intentSearch);
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

    private void initSetData()
    {
        callApi(Apis.BANNER_URL, new JSONArray(), Apis.BANNER_CODE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        mSearchView.setMenuItem(item);

        return true;
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MaterialSearchView.REQUEST_VOICE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches != null && matches.size() > 0) {
                String searchWrd = matches.get(0);
                if (!TextUtils.isEmpty(searchWrd)) {
                    mSearchView.setQuery(searchWrd, false);
                }
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();
        switch (id)
        {
          case R.id.action_cart:
            {
                SQLiteAdapter db = new SQLiteAdapter(mContext);
                db.open();
                int cart_count = db.getCartSize();
                db.close();
                if(cart_count > 0)
                {
                    Bundle bn = new Bundle();
                    bn.putString("MODE", AppConstants.ApplicationLayoutModeAction.CART.name());
                    Intent intentCart = new Intent(HomeActivity.this, CartActivity.class).putExtras(bn);
                    startActivity(intentCart);
                }
                else
                {
                    Snackbar.make(mHorizontalProductListView, "No Item in app_cart.", Snackbar.LENGTH_LONG).show();
                }
            }
            break;
            case R.id.action_userInfo:
            {
                Bundle bn = new Bundle();
                bn.putInt("TYPE", -1);
                bn.putBoolean("EXIT", false);
                Intent intentUserInfo = new Intent(HomeActivity.this, UserInfoActivity.class).putExtras(bn);
                startActivity(intentUserInfo);
            }
            break;
            case R.id.action_track_order:
            {
                Bundle bn = new Bundle();
                bn.putString("MODE", AppConstants.ApplicationLayoutModeAction.TRACK_ORDER.name());
                Intent intentWishList = new Intent(HomeActivity.this, ProductCommonActivity.class).putExtras(bn);
                startActivity(intentWishList);
            }
            break;
            case R.id.action_wishlist:
            {
                System.out.println("WISHLIST CLICKED");
                Bundle bn = new Bundle();
                bn.putString("MODE", AppConstants.ApplicationLayoutModeAction.WISHLIST.name());
                Intent intentWishList = new Intent(HomeActivity.this, ProductCommonActivity.class).putExtras(bn);
                startActivity(intentWishList);
            }
            break;
            case R.id.action_faq: {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                browserIntent.setData(Uri.parse(Apis.URL + "faq"));
                startActivity(browserIntent);
            }
            break;
            case R.id.action_contact_us: {
               Snackbar.make(mSearchView, "Coming Soon", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
            break;
            case R.id.action_about_us: {
                Intent contactIntent = new Intent(Intent.ACTION_VIEW);
                contactIntent.setData(Uri.parse(Apis.URL + "about_us"));
                startActivity(contactIntent);
            }
            break;
            case R.id.action_settings:
                System.out.println("SETTING CLICKED");
                break;
            default:
                System.out.println("Default block clicked on option menu click");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCatTypeItemSelected(View view, int position) {
        System.out.println("VIEW : " + view.getId() + " POSITION : " + position);
    }

    @Override
    public void onCategoryItemSelected(View view, String catType, String catCode, String catName) {
        System.out.println("VIEW : " + view.getId() + " CAT_TYPE : " + catType + " CAT_CODE : " + catCode + " CAT_NAME : " + catName);

        Bundle bn = new Bundle();
        bn.putString("CAT_NAME", catName);
        bn.putInt("CAT_CODE", Integer.valueOf(catCode));

        Intent subCatAct = new Intent(HomeActivity.this, SubCatActivity.class).putExtras(bn);
        startActivity(subCatAct);
    }

    @Override
    public void onSliderClick(BaseSliderView slider)
    {
        System.out.println(slider.getBundle().get("BANNER_ID") + " - HI");

        Bundle bundle = new Bundle();
        bundle.putString("BANNER_ID", slider.getBundle().getString("BANNER_ID"));
        //Intent promoIntent = new  Intent(HomeActivity.this, PromotionActivity.class).putExtras(bundle);
        //startActivity(promoIntent);
    }

    @Override
    public void onHorizontalListClick(String productCode, int pos) {
        Bundle bn = new Bundle();
        bn.putString("MODE",AppConstants.ApplicationLayoutModeAction.NONE.name());
        bn.putString("P_CODE",productCode);
        bn.putInt("POS",pos);
        Intent SingleProduct = new Intent(HomeActivity.this, SingleProductActivity.class).putExtras(bn);
        startActivity(SingleProduct);
    }

    private List<ProductCommonModel> getHorizontalProducts(JSONArray jArray)
    {
        List<ProductCommonModel> mHorizontalData = new ArrayList();
        ProductCommonModel horizontalItem;
        for(int i = 0; i < jArray.length() ; i++)
        {
            horizontalItem = new ProductCommonModel();
            try
            {
                horizontalItem.setProductBrCode(jArray.getJSONObject(i).getString("BR_CODE"));
                horizontalItem.setProductCode(jArray.getJSONObject(i).getString("P_CODE"));
                horizontalItem.setProductName(jArray.getJSONObject(i).getString("P_NAME"));
                horizontalItem.setProductMRP(jArray.getJSONObject(i).getString("MRP"));
                horizontalItem.setProductDFRate(jArray.getJSONObject(i).getString("DF_SALE_RATE"));
                horizontalItem.setProductBalQty(jArray.getJSONObject(i).getString("BAL_QTY"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mHorizontalData.add(horizontalItem);
        }
        //System.out.println();
        return mHorizontalData;
    }

    private List<CatTypeNavItem> getNavData(JSONArray jArray)
    {
        List<CatTypeNavItem> _catTypeNavData = new ArrayList();
        if (jArray.length() > 0)
        {
            CatTypeNavItem _catTypeNavItem;
            for (int i = 0; i < jArray.length(); i++)
            {
                _catTypeNavItem = new CatTypeNavItem();
                try
                {
                    _catTypeNavItem.setTitle(jArray.getJSONObject(i).getString("CAT_TYPE"));
                    List<CategoryItem> _catData = new ArrayList();
                    JSONArray jArrCatList = jArray.getJSONObject(i).getJSONArray("CATEGORY");
                    if (jArrCatList.length() > 0)
                    {
                        CategoryItem _categoryItem = null;
                        for(int j=0; j < jArrCatList.length(); j++)
                        {
                            _categoryItem = new CategoryItem();
                            _categoryItem.setCatCode(jArrCatList.getJSONObject(j).getString("CAT_CODE"));
                            _categoryItem.setCatName(jArrCatList.getJSONObject(j).getString("CAT_NAME"));
                            _catData.add(_categoryItem);
                        }
                    }
                    else
                    {
                        _catData = null;
                    }
                    _catTypeNavItem.setCatList(_catData);
                    _catTypeNavData.add(_catTypeNavItem);
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }
        return _catTypeNavData;
    }

    private void callApi(String _requestUrl, JSONArray _jArr, int _requestCode)
    {
        System.out.println("REQUEST : " + _jArr.toString());
        mAsyncHttpRequest = new AsyncHttpRequest(mContext, _requestUrl, _jArr, _requestCode, AsyncHttpRequest.Type.POST);
        mAsyncHttpRequest.setRequestListener(this);
        mAsyncHttpRequest.execute();
    }

    @Override
    public void onClick(View v)
    {
        int id = v.getId();
        System.out.println("Something is clicked : " + v.getParent());
        switch (id)
        {
            case R.id.new_arrivals_products:
            {
                Bundle bn = new Bundle();
                bn.putString("MODE", AppConstants.ApplicationLayoutModeAction.NEW_PRODUCT.name());
                bn.putString("ORDER","PRODUCT");
                Intent prodAct = new Intent(HomeActivity.this, ProductCommonActivity.class).putExtras(bn);
                startActivity(prodAct);
            }
            break;
            case R.id.school_products:
            {
                Bundle bn = new Bundle();
                bn.putString("MODE", AppConstants.ApplicationLayoutModeAction.NEW_PRODUCT.name());
                bn.putString("ORDER","SCHOOL");
                Intent prodAct = new Intent(HomeActivity.this, ProductCommonActivity.class).putExtras(bn);
                startActivity(prodAct);
            }
            break;
            case R.id.cosmetics_products:
            {
                Bundle bn = new Bundle();
                bn.putString("MODE", AppConstants.ApplicationLayoutModeAction.NEW_PRODUCT.name());
                bn.putString("ORDER","COSMETICS");
                Intent prodAct = new Intent(HomeActivity.this, ProductCommonActivity.class).putExtras(bn);
                startActivity(prodAct);
            }
            break;
            case R.id.home_products:
            {
                Bundle bn = new Bundle();
                bn.putString("MODE", AppConstants.ApplicationLayoutModeAction.NEW_PRODUCT.name());
                bn.putString("ORDER","HOME");
                Intent prodAct = new Intent(HomeActivity.this, ProductCommonActivity.class).putExtras(bn);
                startActivity(prodAct);
            }
            break;
            default:
                System.out.println("Default block clicked on click");
                break;
        }
    }

    @Override
    public void onRequestCompleted(String response, int requestCode)
    {
        try
        {
            JSONArray jArray = new JSONArray(response.trim());
            switch(requestCode)
            {
                case Apis.BANNER_CODE:
                {
                    if (jArray.length() > 0)
                    {
                        TextSliderView textSliderView = null;
                        for(int i = 0; i < jArray.length(); i++)
                        {
                            System.out.println("URL : " + jArray.getJSONObject(i).getString("BANNER_PATH"));
                            textSliderView = new TextSliderView(this);
                            // initialize a SliderLayout
                            textSliderView
                                    .description(jArray.getJSONObject(i).getString("BANNER_TEXT"))
                                    .image(jArray.getJSONObject(i).getString("BANNER_PATH"))
                                    .setScaleType(BaseSliderView.ScaleType.Fit)
                                    .setOnSliderClickListener(this);

                            // add your extra information
                            textSliderView.bundle(new Bundle());
                            textSliderView.getBundle().putString("BANNER_ID", jArray.getJSONObject(i).getString("BANNER_ID"));

                            mSliderLayout.addSlider(textSliderView);
                            mSliderLayout.startAutoCycle();
                            mSliderLayout.clearAnimation();
                        }
                        mLoading.dismiss();
                        callApi(Apis.CAT_TYPE_CATEGORY_LIST_URL, new JSONArray(), Apis.CAT_TYPE_CATEGORY_LIST_CODE);
                    }
                    else
                    {
                        System.out.println("LEN "+jArray.length());
                        HashMap<String,Integer> file_maps = new HashMap<String, Integer>();
                        file_maps.put("SLIDER_01", R.drawable.try_slider_01);
                        file_maps.put("SLIDER_02", R.drawable.try_slider_02);
                        file_maps.put("SLIDER_03", R.drawable.try_slider_03);
                        TextSliderView textSliderView = null;
                        for(String name : file_maps.keySet())
                        {
                            System.out.println("NAME : " + name);
                            textSliderView = new TextSliderView(this);
                            // initialize a SliderLayout
                            textSliderView
                                    .description(name)
                                    .image(file_maps.get(name))
                                    .setScaleType(BaseSliderView.ScaleType.Fit)
                                    .setOnSliderClickListener(this);

                            // add your extra information
                            textSliderView.bundle(new Bundle());
                            textSliderView.getBundle().putString("extra",name);

                            mSliderLayout.addSlider(textSliderView);
                        }
                    }
                    mSliderLayout.clearDisappearingChildren();
                    mSliderLayout.setPresetTransformer(SliderLayout.Transformer.ZoomOutSlide);
                    mSliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
                    mSliderLayout.setCustomAnimation(new DescriptionAnimation());
                    mSliderLayout.setDuration(4000);

                    Display _display = this.getWindowManager().getDefaultDisplay();

                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(_display.getWidth(), _display.getHeight()*30/100);
                    mSliderLayout.setPadding(0, 6, 0, 0);
                    mSliderLayout.setLayoutParams(lp);
                    mLoading.dismiss();
                    callApi(Apis.CAT_TYPE_CATEGORY_LIST_URL, new JSONArray(), Apis.CAT_TYPE_CATEGORY_LIST_CODE);
                }
                break;
                case Apis.CAT_TYPE_CATEGORY_LIST_CODE:
                {
                    if (!jArray.getJSONObject(0).has("ERROR"))
                    {
                        mFragmentDrawer.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar, getNavData(jArray));
                        mFragmentDrawer.setFragmentListener(this);
                        mLoading.dismiss();
                    }
                    else
                    {
                        Snackbar.make(mFragmentDrawer.getView(), jArray.getJSONObject(0).getString("ERROR"), Snackbar.LENGTH_SHORT).show();
                    }

                    // for horizontal list view
                    JSONArray jArr = new JSONArray();
                    JSONObject jObj = new JSONObject();
                    jObj.put("TYPE", "HOME");
                    jArr.put(jObj);
                    System.out.println(jArr.toString());
                    callApi(Apis.NEW_PRODUCT_URL, jArr, Apis.NEW_PRODUCT_CODE);
                }
                break;
                case Apis.NEW_PRODUCT_CODE:
                {
                    if (!jArray.getJSONObject(0).has("ERROR")) {
                        if(jArray.length() > 0)
                        {
                            // for horizontalListView
                            mHlvAdapter = new HorizontalListViewAdapter(this, R.layout.horizontal_list_column, getHorizontalProducts(jArray.getJSONObject(0).getJSONArray("PRODUCT_LIST")));
                            mHorizontalProductListView.setAdapter(mHlvAdapter);

                            mHlvAdapter = new HorizontalListViewAdapter(this, R.layout.horizontal_list_column, getHorizontalProducts(jArray.getJSONObject(0).getJSONArray("SCHOOL_LIST")));
                            mHorizontalSchoolListView.setAdapter(mHlvAdapter);

                            mHlvAdapter = new HorizontalListViewAdapter(this, R.layout.horizontal_list_column, getHorizontalProducts(jArray.getJSONObject(0).getJSONArray("COSMETICS_LIST")));
                            mHorizontalCosmeticsListView.setAdapter(mHlvAdapter);

                            mHlvAdapter = new HorizontalListViewAdapter(this, R.layout.horizontal_list_column, getHorizontalProducts(jArray.getJSONObject(0).getJSONArray("HOME_DECOR_LIST")));
                            mHorizontalDecorListView.setAdapter(mHlvAdapter);
                        }
                        else
                        {
                            mHorizontalProductListView.setVisibility(View.GONE);
                            Snackbar.make(mHorizontalProductListView, "No new products added.", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                    else
                        Snackbar.make(mHorizontalProductListView, jArray.getJSONObject(0).getString("ERROR"), Snackbar.LENGTH_SHORT).show();
                    mLoading.dismiss();
                }
                break;
                case Apis.SEARCH_LIST_CODE:
                {
                    JSONArray jTemp = new JSONArray();
                    if (!jArray.getJSONObject(0).has("ERROR")) {
                        if (jArray.getJSONObject(0).has("COUNT")) {
                            JSONArray jArr = jArray.getJSONObject(0).getJSONArray("LIST");
                            for(int i = 0; i < jArr.length(); i++)
                            {
                                String url = Apis.URL + "assets/products/" + jArr.getJSONObject(i).getString("P_CODE") + ".jpg";
                                JSONObject jObj = new JSONObject();
                                jObj.put("P_NAME", jArr.getJSONObject(i).getString("P_NAME"));
                                jObj.put("P_CODE", jArr.getJSONObject(i).getString("P_CODE"));
                                jObj.put("BR_CODE", jArr.getJSONObject(i).getString("BR_CODE"));
                                jObj.put("URL", url);
                                jTemp.put(jObj);
                            }
                        }
                    }
                    else
                    {
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
            }
        mLoading.dismiss();
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
