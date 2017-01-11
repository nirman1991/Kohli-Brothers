package in.co.retail.kohlibrothers.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import in.co.retail.kohlibrothers.Adapter.CommonListAdapter;
import in.co.retail.kohlibrothers.Controller.ActionBarHelper;
import in.co.retail.kohlibrothers.Model.Apis;
import in.co.retail.kohlibrothers.R;
import in.co.retail.kohlibrothers.Utility.AppConstants;
import in.co.retail.kohlibrothers.Utility.Utils;
import in.co.retail.kohlibrothers.Webservice.AsyncHttpRequest;

/**
 * Created by nirman on 4/22/2016.
 */
public class SubCatActivity extends AppCompatActivity implements AsyncHttpRequest.RequestListener
{
    private static String TAG = SubCatActivity.class.getSimpleName();
    private Context mContext;
    private Toolbar mToolbar;
    private AsyncHttpRequest mAsyncHttpRequest;
    private RecyclerView mListItem;
    private CommonListAdapter mSubCatAdapter;
    private JSONArray jArr;
    private AppConstants.ApplicationLayoutModeAction MODE = AppConstants.ApplicationLayoutModeAction.NONE;
    private String _subCatCode;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_cat);
        System.out.println("TAG : " + TAG);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        ActionBarHelper.init(this, mToolbar, true, true, getIntent().getExtras().getString("CAT_NAME"), false);
        this.mContext = this;
        mListItem = (RecyclerView) findViewById(R.id.listItem);

        MODE = AppConstants.ApplicationLayoutModeAction.valueOf(getIntent().getExtras().getString("MODE", AppConstants.ApplicationLayoutModeAction.NONE.name()));
        switch (MODE)
        {
            case NONE:
                try
                {
                    JSONArray jArr = new JSONArray();
                    JSONObject jObj = new JSONObject();
                    System.out.println("CAT CODE " + getIntent().getExtras().getInt("CAT_CODE"));
                    jObj.put("CAT_CODE", getIntent().getExtras().getInt("CAT_CODE"));
                    jArr.put(jObj);
                    callApi(Apis.SUB_CAT_LIST_URL, jArr, Apis.SUB_CAT_LIST_CODE);
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
                break;
            case SCHOOL:
                _subCatCode = getIntent().getExtras().getString("SUB_CAT_CODE");
                callApi(Apis.SUB_CAT_LIST_URL, new JSONArray(), Apis.SUB_CAT_LIST_CODE);
                break;
            case BRAND:
                try
                {
                    System.out.println("BRAND");
                    JSONArray jArr = new JSONArray();
                    JSONObject jObj = new JSONObject();
                    _subCatCode = getIntent().getExtras().getString("SUB_CAT_CODE");
                    jObj.put("SUB_CAT_NAME",  getIntent().getExtras().getString("SUB_CAT_NAME"));
                    jArr.put(jObj);
                    callApi(Apis.SUB_CAT_LIST_URL, jArr, Apis.SUB_CAT_LIST_CODE);
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
                break;
            case CATEGORY:
                try
                {
                    JSONArray jArr = new JSONArray();
                    JSONObject jObj = new JSONObject();
                    jObj.put("CAT_TYPE", getIntent().getExtras().getString("CAT_TYPE"));
                    System.out.println("CAT TYPE "+getIntent().getExtras().getString("CAT_TYPE"));
                    jArr.put(jObj);
                    callApi(Apis.SUB_CAT_LIST_URL, jArr, Apis.SUB_CAT_LIST_CODE);
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
                break;
            default:
                try
                {
                    System.out.println("DEFAULT");
                    JSONArray jArr = new JSONArray();
                    JSONObject jObj = new JSONObject();
                    jObj.put("CAT_CODE", getIntent().getExtras().getInt("CAT_CODE"));
                    jArr.put(jObj);
                    callApi(Apis.SUB_CAT_LIST_URL, jArr, Apis.SUB_CAT_LIST_CODE);
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
            {
                finish();
            }
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    private static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener
    {
        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        public RecyclerTouchListener(Context _context, final RecyclerView _recyclerView, final ClickListener _clickListener)
        {
            this.clickListener = _clickListener;
            gestureDetector = new GestureDetector(_context, new GestureDetector.SimpleOnGestureListener()
            {
                @Override
                public boolean onSingleTapUp(MotionEvent e)
                {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e)
                {
                    View child = _recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null)
                    {
                        clickListener.onLongClick(child, _recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e)
        {
            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e))
            {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e)
        {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept)
        {

        }
    }

    public interface ClickListener
    {
        void onClick(View view, int position);
        void onLongClick(View view, int position);
    }

    private void callApi(String _requestUrl, JSONArray _jArr, int _requestCode)
    {
        //Utils.LogBook(Utils.LogType.INFO, TAG, "1_ " + _requestUrl);
        mAsyncHttpRequest = new AsyncHttpRequest(mContext, _requestUrl, _jArr, _requestCode, AsyncHttpRequest.Type.POST);
        mAsyncHttpRequest.setRequestListener(this);
        mAsyncHttpRequest.execute();
    }

    @Override
    public void onRequestCompleted(String response, int requestCode)
    {
        try
        {
            if(!response.equals("[]") && requestCode != 0)
            {
                JSONArray jArray = new JSONArray(response.trim());
                switch(requestCode)
                {
                    case Apis.SUB_CAT_LIST_CODE:
                    {
                        if (!jArray.getJSONObject(0).has("ERROR"))
                        {
                            if (jArray.getJSONObject(0).getBoolean("STATUS"))
                            {
                                jArr = jArray.getJSONObject(0).getJSONArray("LIST");
                                System.out.println("ARR "+jArr);
                                mSubCatAdapter = new CommonListAdapter(this, jArray.getJSONObject(0).getJSONArray("LIST"), MODE);
                                mListItem.setAdapter(mSubCatAdapter);
                                mListItem.setLayoutManager(new LinearLayoutManager(this));
                                mListItem.addOnItemTouchListener(new RecyclerTouchListener(mContext, mListItem, new ClickListener() {
                                    @Override
                                    public void onClick(View view, int position) {
                                        switch(MODE)
                                        {
                                            case NONE:
                                                try
                                                {
                                                    Bundle bundle = new Bundle();
                                                    bundle.putString("MODE", AppConstants.ApplicationLayoutModeAction.PRODUCT.name());
                                                    bundle.putInt("CAT_CODE", getIntent().getExtras().getInt("CAT_CODE"));
                                                    bundle.putString("CAT_NAME", getIntent().getExtras().getString("CAT_NAME"));
                                                    bundle.putString("SUB_CAT_CODE", jArr.getJSONObject(position).getString("SUB_CAT_CODE"));
                                                    bundle.putString("SUB_CAT_NAME", jArr.getJSONObject(position).getString("SUB_CAT_NAME"));
                                                    Intent intentProduct = new Intent(SubCatActivity.this, ProductCommonActivity.class).putExtras(bundle);
                                                    startActivity(intentProduct);
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                break;
                                            case SCHOOL:
                                                try
                                                {
                                                    System.out.println("SUB "+_subCatCode);
                                                    Bundle bundle = new Bundle();
                                                    bundle.putString("MODE", AppConstants.ApplicationLayoutModeAction.SCHOOL.name());
                                                    bundle.putString("SUB_CAT_CODE",_subCatCode);
                                                    bundle.putString("SUB_CAT_NAME", "SCHOOL LIST");
                                                    bundle.putString("BRAND_NAME", jArr.getJSONObject(position).getString("BRAND_NAME"));
                                                    Intent intentProduct = new Intent(SubCatActivity.this, ProductCommonActivity.class).putExtras(bundle);
                                                    startActivity(intentProduct);
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                break;
                                            case BRAND:
                                                try
                                                {
                                                    //System.out.println("SUB NAME "+jArr.getJSONObject(position).getString("SUB_CAT_NAME"));
                                                    Bundle bundle = new Bundle();
                                                    bundle.putString("MODE", AppConstants.ApplicationLayoutModeAction.BRAND.name());
                                                    bundle.putString("SUB_CAT_CODE", _subCatCode);
                                                    bundle.putString("SUB_CAT_CODE", jArr.getJSONObject(position).getString("SUB_CAT_CODE"));
                                                    bundle.putString("SUB_CAT_NAME", jArr.getJSONObject(position).getString("SUB_CAT_NAME"));
                                                    bundle.putString("BRAND_NAME", jArr.getJSONObject(position).getString("BRAND_NAME"));
                                                    Intent intentProduct = new Intent(SubCatActivity.this, ProductCommonActivity.class).putExtras(bundle);
                                                    startActivity(intentProduct);
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                break;
                                            case CATEGORY:
                                                try
                                                {
                                                    Bundle bundle = new Bundle();
                                                    bundle.putString("MODE", AppConstants.ApplicationLayoutModeAction.CATEGORY.name());
                                                    bundle.putString("SUB_CAT_CODE", getIntent().getExtras().getString("SUB_CAT_CODE"));
                                                    bundle.putString("SUB_CAT_NAME", "FILTER LIST");
                                                    bundle.putString("CAT_NAME", jArr.getJSONObject(position).getString("CAT_NAME"));
                                                    Intent intentProduct = new Intent(SubCatActivity.this, ProductCommonActivity.class).putExtras(bundle);
                                                    startActivity(intentProduct);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                                break;
                                        }
                                    }

                                    @Override
                                    public void onLongClick(View view, int position) {
                                        System.out.println("POSITION : " + position);
                                        try {
                                            System.out.println("SUB CAT CODE : " + jArr.getJSONObject(position).getString("SUB_CAT_CODE"));
                                            System.out.println("SUB CAT NAME : " + jArr.getJSONObject(position).getString("SUB_CAT_NAME"));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }));
                            }
                            else
                                Snackbar.make(mListItem, "NO SUB CATEGORY FETCHED. TRY AGAIN !!!", Snackbar.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Snackbar.make(mListItem, jArray.getJSONObject(0).getString("ERROR"), Snackbar.LENGTH_SHORT).show();
                        }
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
    public void onRequestError(Exception e, int requestCode)
    {
        Utils.showToast(this, "Please check your Internet Connection");
    }

    @Override
    public void onRequestStarted(int requestCode)
    {

    }
}
