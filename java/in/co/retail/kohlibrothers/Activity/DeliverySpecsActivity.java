package in.co.retail.kohlibrothers.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import in.co.retail.kohlibrothers.Controller.ActionBarHelper;
import in.co.retail.kohlibrothers.DBAdapter.SQLiteAdapter;
import in.co.retail.kohlibrothers.Model.Apis;
import in.co.retail.kohlibrothers.Model.LoggedUser;
import in.co.retail.kohlibrothers.R;
import in.co.retail.kohlibrothers.Utility.AppConstants;
import in.co.retail.kohlibrothers.Utility.Loading;
import in.co.retail.kohlibrothers.Utility.Utils;
import in.co.retail.kohlibrothers.Webservice.AsyncHttpRequest;

/**
 * Created by nirman on 4/27/2016.
 */
public class DeliverySpecsActivity extends AppCompatActivity implements View.OnClickListener, AsyncHttpRequest.RequestListener
{
    private static String TAG = DeliverySpecsActivity.class.getSimpleName();
    private Context mContext;
    private Toolbar mToolbar;
    private AsyncHttpRequest mAsyncHttpRequest;

    private Spinner mSpinDelivType, mSpinDate, mSpinTime;
    private Button mBtnBack, mBtnProceed;
    private TextView mTxtMsg;
    private String mFinalMsg;

    private int fix_amt, min_ord_val, max_ord_val;
    private String mDelPriority, mDelCharges;
    private Loading mLoading;

    private AppConstants.DeliveryAddressType addType = AppConstants.DeliveryAddressType.NONE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_specs);
        System.out.println("TAG : " + TAG);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        this.mContext = this;


        ActionBarHelper.init(this, mToolbar, true, true, "DELIVERY SPECIFICATIONS", false);

        mSpinDate = (Spinner) findViewById(R.id.spinDate);
        mSpinTime = (Spinner) findViewById(R.id.spinTimeSlot);
        mSpinDelivType = (Spinner) findViewById(R.id.spinDelivType);
        mTxtMsg = (TextView) findViewById(R.id.txtMsg);

        mBtnBack = (Button) findViewById(R.id.btnBack);
        mBtnBack.setOnClickListener(this);
        mBtnProceed = (Button) findViewById(R.id.btnProceed);
        mBtnProceed.setOnClickListener(this);

        initData(getIntent());

    }

    private void initData(Intent _intent)
    {
        if (_intent != null)
        addType = AppConstants.DeliveryAddressType.valueOf(_intent.getExtras().getString("ADD_TYPE"));
        System.out.println("ADD TYPE : " + addType.name());
        JSONArray jArr = new JSONArray();
        callApi(Apis.DELIVERY_SPECS_URL, jArr, Apis.DELIVERY_SPECS_CODE);
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

    private void callApi(String _requestUrl, JSONArray _jArr, int _requestCode)
    {
        mAsyncHttpRequest = new AsyncHttpRequest(mContext, _requestUrl, _jArr, _requestCode, AsyncHttpRequest.Type.POST);
        mAsyncHttpRequest.setRequestListener(this);
        mAsyncHttpRequest.execute();
    }

    private void setTimeSlot(String[] spinTempTS, Spinner mSpinTime) {
        ArrayAdapter<String> spinAdap = new ArrayAdapter<String>(this, R.layout.layout_dropdown, spinTempTS); //selected item will look app_like a spinner set from XML
        mSpinTime.setAdapter(spinAdap);
    }/**/


    @Override
    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.btnBack:
                finish();
                break;
            case R.id.btnProceed:
            {
                String dateValue = mSpinDate.getSelectedItem().toString();
                String timeValue = mSpinTime.getSelectedItem().toString();

                JSONArray jArr;
                SQLiteAdapter db = new SQLiteAdapter(mContext);
                db.open();
                jArr = db.getTotal();
                db.close();

                try
                {
                    float _total = Float.valueOf(jArr.getJSONObject(0).getString("TOTAL_AMOUNT"));
                    System.out.println("MIN ORDER VAL "+min_ord_val);
                    if (_total > min_ord_val)
                    {
                        System.out.println("ADDR TYPE "+addType.name());
                        Bundle bn = new Bundle();
                        bn.putString("AddType", addType.name());
                        bn.putString("DateString",dateValue);
                        bn.putString("TimeString", timeValue);
                        Intent intent = new Intent(DeliverySpecsActivity.this, OrderOverviewActivity.class);
                        intent.putExtras(bn);
                        startActivity(intent);
                    }
                    else
                        Snackbar.make(mBtnProceed, "You are just " + mContext.getResources().getString(R.string.rupees) + " " + (min_ord_val-_total)+" "+"away from making your Order.", Snackbar.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            break;
        }
    }

    @Override
    public void onRequestCompleted(String response, int requestCode)
    {
        System.out.println(response);
        try
        {
            if(!response.equals("[]") && requestCode == Apis.DELIVERY_SPECS_CODE)
            {
                JSONArray jArr = new JSONArray(response.trim());
                switch(requestCode) {
                    case Apis.DELIVERY_SPECS_CODE: {
                        if(jArr.length() > 0)
                        {
                            JSONArray jTemp = jArr.getJSONObject(0).getJSONArray("DATE_TIME_SLOT");
                            if (jTemp.length() > 0)
                            {
                                String[] spinTempDates = new String[jTemp.length()];
                                for(int i = 0; i < jTemp.length(); i++)
                                {
                                    spinTempDates[i] = jTemp.getJSONObject(i).getString("T_DATE");
                                }

                                ArrayAdapter<String> spinAdap = new ArrayAdapter<String>(this, R.layout.layout_dropdown, spinTempDates); //selected item will look app_like a spinner set from XML
                                mSpinDate.setAdapter(spinAdap);

                                final JSONArray finalJTemp1 = jTemp;
                                mSpinDate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        // time slots
                                        try
                                        {
                                            String[] spinTempTS = finalJTemp1.getJSONObject(position).getString("TIME_SLOTS").split(",");
                                            setTimeSlot(spinTempTS,mSpinTime);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                        ((TableRow) findViewById(R.id.layoutTS)).setVisibility(View.VISIBLE);
                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {

                                    }
                                });
                            }

                            jTemp = jArr.getJSONObject(0).getJSONArray("DELIV_PRIORITY");
                            max_ord_val = Integer.valueOf(jTemp.getJSONObject(0).getString("MAX_ORD_VAL"));
                            min_ord_val = Integer.valueOf(jTemp.getJSONObject(0).getString("MIN_ORD_VAL"));

                            if(addType == AppConstants.DeliveryAddressType.PRIMARY)
                            {
                                mDelCharges = LoggedUser.customer.getDelCharges1().toString();
                                if(min_ord_val == 0 && max_ord_val == 0)
                                    mFinalMsg = "* There is no minimum order value";
                                else if(min_ord_val == 0 && max_ord_val > 0)
                                    mFinalMsg = "* Order total should not be more than " + mContext.getResources().getString(R.string.rupees) + " " + max_ord_val;
                                else if(min_ord_val > 0 && max_ord_val == 0)
                                    mFinalMsg = "*  Order total should be greater than " + mContext.getResources().getString(R.string.rupees) + " " + min_ord_val + " with an additional delivery charge of "  + mContext.getResources().getString(R.string.rupees) + " " + mDelCharges ;
                                else if (min_ord_val > 0 && max_ord_val > 0)
                                    mFinalMsg = "* Orders between " + mContext.getResources().getString(R.string.rupees) + " " + min_ord_val + " and " + mContext.getResources().getString(R.string.rupees) + " " + max_ord_val + " with an additional delivery charge of "  + mContext.getResources().getString(R.string.rupees) + " " + mDelCharges ;
                                else
                                    mFinalMsg = "* Set Message";
                                mTxtMsg.setText(mFinalMsg);
                            }
                            else
                            {
                                mDelCharges = LoggedUser.customer.getDelCharges2().toString();
                                if(min_ord_val == 0 && max_ord_val == 0)
                                    mFinalMsg = "* There is no minimum order value";
                                else if(min_ord_val == 0 && max_ord_val > 0)
                                    mFinalMsg = "* Order total should not be more than " + mContext.getResources().getString(R.string.rupees) + " " + max_ord_val;
                                else if(min_ord_val > 0 && max_ord_val == 0)
                                    mFinalMsg = "*  Order total should be greater than " + mContext.getResources().getString(R.string.rupees) + " " + min_ord_val + " with an additional delivery charge of "  + mContext.getResources().getString(R.string.rupees) + " " + mDelCharges ;
                                else if (min_ord_val > 0 && max_ord_val > 0)
                                    mFinalMsg = "* Orders between " + mContext.getResources().getString(R.string.rupees) + " " + min_ord_val + " and " + mContext.getResources().getString(R.string.rupees) + " " + max_ord_val + " with an additional delivery charge of "  + mContext.getResources().getString(R.string.rupees) + " " + mDelCharges ;
                                else
                                    mFinalMsg = "* Set Message";
                                mTxtMsg.setText(mFinalMsg);
                            }
                        }
                    }
                    mLoading.dismiss();
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
        mLoading.hide();
    }

    @Override
    public void onRequestStarted(int requestCode)
    {
        mLoading = new Loading(this);
        mLoading.show();
    }
}
