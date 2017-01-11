package in.co.retail.kohlibrothers.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import org.json.JSONArray;
import org.json.JSONObject;

import in.co.retail.kohlibrothers.Adapter.PromotionAdapter;
import in.co.retail.kohlibrothers.Controller.ActionBarHelper;
import in.co.retail.kohlibrothers.Model.Apis;
import in.co.retail.kohlibrothers.R;
import in.co.retail.kohlibrothers.Utility.AppConstants;
import in.co.retail.kohlibrothers.Utility.Loading;
import in.co.retail.kohlibrothers.Utility.Utils;
import in.co.retail.kohlibrothers.Webservice.AsyncHttpRequest;

/**
 * Created by nirman on 5/2/2016.
 */
public class PromotionActivity extends AppCompatActivity implements PromotionAdapter.Listener, AsyncHttpRequest.RequestListener
{
    private static String TAG = PromotionActivity.class.getSimpleName();
    private Context mContext;
    private Toolbar mToolbar;
    private AsyncHttpRequest mAsyncHttpRequest;
    private Loading mLoading;
    private PromotionAdapter mPromotionAdapter;
    private RecyclerView mListItem;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promotion);
        System.out.println("TAG : " + TAG);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        ActionBarHelper.init(this, mToolbar, true, true, "HOME", false);
        this.mContext = this;

        mListItem = (RecyclerView) findViewById(R.id.mListItem);

        //String banner_id = getIntent().getExtras().getString("BANNER_ID");
        //System.out.println("BANNER ID : " + banner_id);
        //setInitData(banner_id, "HOME");
    }

    private void setInitData(String _id, String _type) {
        try
        {
            JSONArray jArr = new JSONArray();
            JSONObject jObj = new JSONObject();
            jObj.put("TYPE", _type);
            jObj.put("ID", _id);
            jArr.put(jObj);
            callApi(Apis.GET_PROMOTION_URL, jArr, Apis.GET_PROMOTION_CODE);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id)
        {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void callApi(String _requestUrl, JSONArray _jArr, int _requestCode)
    {
        mAsyncHttpRequest = new AsyncHttpRequest(mContext, _requestUrl, _jArr, _requestCode, AsyncHttpRequest.Type.POST);
        mAsyncHttpRequest.setRequestListener(this);
        mAsyncHttpRequest.execute();
    }

    @Override
    public void onViewClick(String promoCode) {
        Bundle bn = new Bundle();
        bn.putString("MODE", AppConstants.ApplicationLayoutModeAction.PROMOTION.name());
        bn.putString("PROMO_CODE", promoCode);
        Intent promoItemIntent = new Intent(PromotionActivity.this, ProductCommonActivity.class).putExtras(bn);
        startActivity(promoItemIntent);
    }

    @Override
    public void onRequestCompleted(String response, int requestCode) {
        try {
            if (!response.equals("[]") && requestCode != 0)
            {
                JSONArray jArray = new JSONArray(response.trim());
                switch (requestCode)
                {
                    case Apis.GET_PROMOTION_CODE:
                    {
                        if (jArray.getJSONObject(0).has("PROMOTION"))
                        {
                            mPromotionAdapter = new PromotionAdapter(mContext,this, jArray.getJSONObject(0).getJSONArray("PROMOTION"));
                            mListItem.setAdapter(mPromotionAdapter);
                            mListItem.setLayoutManager(new LinearLayoutManager(this));
                        }
                        else
                            System.out.println("NOT IN");
                    }
                }
            }
            else
                Snackbar.make(mListItem, "Promotions Coming up Shortly. Stay Tuned.", Snackbar.LENGTH_SHORT).show();
            mLoading.dismiss();
        }
        catch (Exception e)
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
