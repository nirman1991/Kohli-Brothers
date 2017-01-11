package in.co.retail.kohlibrothers.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import in.co.retail.kohlibrothers.Controller.ActionBarHelper;
import in.co.retail.kohlibrothers.DBAdapter.SQLiteAdapter;
import in.co.retail.kohlibrothers.Model.Apis;
import in.co.retail.kohlibrothers.Model.CartModel;
import in.co.retail.kohlibrothers.Model.LoggedUser;
import in.co.retail.kohlibrothers.Model.ProductCommonModel;
import in.co.retail.kohlibrothers.R;
import in.co.retail.kohlibrothers.Utility.AppConstants;
import in.co.retail.kohlibrothers.Utility.Utils;
import in.co.retail.kohlibrothers.Webservice.AsyncHttpRequest;

/**
 * Created by nirman on 4/28/2016.
 */
public class OrderOverviewActivity extends AppCompatActivity implements View.OnClickListener, AsyncHttpRequest.RequestListener
{
    private static String TAG = OrderOverviewActivity.class.getSimpleName();
    private Context mContext;
    private Toolbar mToolbar;
    private AsyncHttpRequest mAsyncHttpRequest;

    private SQLiteAdapter db;

    private CartModel mCartModel;

    private TextView mPlaceMyOrder, mEditAdd1, mEditAdd2, mDeliveryDetEdit, mReviewOrder, mBtnSwap;
    private ImageView  mPriAddrClick, mSecAddrClick;
    private RadioGroup mRdoPmtGrp;
    private JSONArray jArrMain, jArrMain1;
    private float oth_charges;
    private String del_priority, date, time;
    private int pmt_type = 0;

    private AppConstants.DeliveryAddressType addType = AppConstants.DeliveryAddressType.NONE;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orderoverview);
        System.out.println("TAG : " + TAG);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        this.mContext = this;

        ActionBarHelper.init(this, mToolbar, true, true, "ORDER OVERVIEW", false);


        mRdoPmtGrp = (RadioGroup) findViewById(R.id.rdoPmtGrp);


        mPlaceMyOrder = (TextView) findViewById(R.id.place_my_order);
        mEditAdd1 = (TextView) findViewById(R.id.EditAdd1);
        mEditAdd2 = (TextView) findViewById(R.id.EditAdd2);
        mDeliveryDetEdit = (TextView) findViewById(R.id.deliveryDetEdit);
        mBtnSwap = (TextView) findViewById(R.id.btnChange);
        mReviewOrder = (TextView) findViewById(R.id.txtReviewOrder);
        mPriAddrClick = (ImageView) findViewById(R.id.priAddressCheck);
        mSecAddrClick = (ImageView) findViewById(R.id.secAddressCheck);

        mPlaceMyOrder.setOnClickListener(this);
        mEditAdd1.setOnClickListener(this);
        mEditAdd2.setOnClickListener(this);
        mDeliveryDetEdit.setOnClickListener(this);
        mReviewOrder.setOnClickListener(this);
        mPriAddrClick.setOnClickListener(this);
        mSecAddrClick.setOnClickListener(this);
        mBtnSwap.setOnClickListener(this);

        mRdoPmtGrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                System.out.println("CHECKED ID : " + checkedId);
                pmt_type = checkedId;
            }
        });

        initData(getIntent());
        callPmtTypeApi();

    }

    private void callPmtTypeApi() {
        callApi(Apis.GET_PMT_TYPE_URL, new JSONArray(), Apis.GET_PMT_TYPE_CODE);
    }



    private void initData(Intent intent) {
        if (intent != null)
        {
            addType = AppConstants.DeliveryAddressType.valueOf(intent.getExtras().getString("AddType"));
            date = intent.getExtras().getString("DateString");
            time = intent.getExtras().getString("TimeString");
            System.out.println("ADDR TYPE "+addType.name());
            ((TextView) findViewById(R.id.txtDate)).setText(date);
            ((TextView) findViewById(R.id.txtTime)).setText(time);


            // for delivery charges
            if(addType.equals(AppConstants.DeliveryAddressType.PRIMARY)) {
                oth_charges = Float.valueOf(LoggedUser.customer.getDelCharges1().toString());
                System.out.print("PRIMARY " + LoggedUser.customer.getDelCharges1());
            }
            else if(addType == AppConstants.DeliveryAddressType.SECONDARY)
            {
                oth_charges = Float.valueOf(LoggedUser.customer.getDelCharges2().toString());
                System.out.print("SECONDARY " + LoggedUser.customer.getDelCharges2());
            }

            ((TextView) findViewById(R.id.valueCharged)).setText( mContext.getResources().getString(R.string.rupees) + " " +oth_charges);

            //database entry
            db = new SQLiteAdapter(mContext);
            db.open();
            mCartModel = db.getCartItem();
            db.close();

            // for app_cart total
            ((TextView) findViewById(R.id.txtCartTotal)).setText(mContext.getResources().getString(R.string.you_have_total) + " " + mCartModel.getProductItemList().size() + " " + mContext.getResources().getString(R.string.items_in_cart));
            ((TextView) findViewById(R.id.txtTotal)).setText("TOTAL : " + mContext.getResources().getString(R.string.rupees) + " " + (Float.valueOf(mCartModel.getmTotal()) + oth_charges));

            ((TextView) findViewById(R.id.txtName)).setTypeface(Typeface.DEFAULT_BOLD);
            ((TextView) findViewById(R.id.txtMobileNo)).setTypeface(Typeface.DEFAULT_BOLD);
            ((TextView) findViewById(R.id.txtAddress)).setTypeface(Typeface.DEFAULT_BOLD);
            ((TextView) findViewById(R.id.txtLandmark)).setTypeface(Typeface.DEFAULT_BOLD);

            ((TextView) findViewById(R.id.txtName2)).setTypeface(Typeface.DEFAULT_BOLD);
            ((TextView) findViewById(R.id.txtMobileNo2)).setTypeface(Typeface.DEFAULT_BOLD);
            ((TextView) findViewById(R.id.txtAddress2)).setTypeface(Typeface.DEFAULT_BOLD);
            ((TextView) findViewById(R.id.txtLandmark2)).setTypeface(Typeface.DEFAULT_BOLD);

            // user details
            ((TextView) findViewById(R.id.txtName)).setText("NAME : " + LoggedUser.customer.getCustName1());
            ((TextView) findViewById(R.id.txtMobileNo)).setText("MOBILE : " + LoggedUser.customer.getMobNo1());
            ((TextView) findViewById(R.id.txtAddress)).setText("ADDRESS : " + LoggedUser.customer.getAddress1());
            ((TextView) findViewById(R.id.txtLandmark)).setText("LANDMARK : " + LoggedUser.customer.getLandMark1());

            // secondary details
            ((TextView) findViewById(R.id.txtName2)).setText("NAME : " + LoggedUser.customer.getCustName2());
            ((TextView) findViewById(R.id.txtMobileNo2)).setText("MOBILE : " + LoggedUser.customer.getMobNo2());
            ((TextView) findViewById(R.id.txtAddress2)).setText("ADDRESS : " + LoggedUser.customer.getAddress2());
            ((TextView) findViewById(R.id.txtLandmark2)).setText("LANDMARK : " + LoggedUser.customer.getLandMark2());
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
        initData(getIntent());
    }

    private void placeOrder()
    {
        String billingFName = "", delFName = "";
        String billingLName = "", delLName = "";
        String delAdd = "", delPhone = "", delCity = "", delArea = "", delPincode = "", delLandmark = "";

        // billing name and address
        String[] CustName = LoggedUser.customer.getCustName1().split(" ");
        int count = CustName.length;
        if(count == 1)
        {
            billingFName = LoggedUser.customer.getCustName1();
            billingLName = "";
        }
        else
        {
            billingLName = CustName[count - 1];
            if (count > 2) {
                billingFName = CustName[0] + " " + CustName[1];
            } else if (count == 2) {
                billingFName = CustName[0];
            }
        }

        switch(addType)
        {
            case PRIMARY:
            {
                delFName = billingFName;
                delLName = billingLName;

                delAdd = LoggedUser.customer.getAddress1()+", "+LoggedUser.customer.getArea1()+", "+LoggedUser.customer.getCity1()+", "+LoggedUser.customer.getLandMark1()+", "+ LoggedUser.customer.getPinCode1();
                delPhone = LoggedUser.customer.getMobNo1();
                delCity = LoggedUser.customer.getCity1();
                delArea = LoggedUser.customer.getArea1();
                delPincode = LoggedUser.customer.getPinCode1();
                delLandmark = LoggedUser.customer.getLandMark1();
            }
            break;
            case SECONDARY:
            {
                CustName = LoggedUser.customer.getCustName2().split(" ");
                count = CustName.length;
                if(count == 1)
                {
                    delFName = LoggedUser.customer.getCustName2();
                    delLName = "";
                }
                else
                {
                    delLName = CustName[count - 1];
                    if (count > 2) {
                        delFName = CustName[0] + " " + CustName[1];
                    } else if (count == 2) {
                        delFName = CustName[0];
                    }
                }
                delAdd = LoggedUser.customer.getAddress2()+", "+LoggedUser.customer.getArea2()+", "+LoggedUser.customer.getCity2()+", "+LoggedUser.customer.getLandMark2()+", "+ LoggedUser.customer.getPinCode2();
                delPhone = LoggedUser.customer.getMobNo2();
                delCity = LoggedUser.customer.getCity2();
                delArea = LoggedUser.customer.getArea2();
                delPincode = LoggedUser.customer.getPinCode2();
                delLandmark = LoggedUser.customer.getLandMark2();
            }
            break;
        }

        if(pmt_type > -1)
        {
            System.out.println("OK");
            // JSON ARRAY FORMATION FOR DETAILS OF ORDER PLACED BY A CUSTOMER
            // MAIN JSONArray
            JSONArray jArrMain = new JSONArray();
            JSONObject jObjMain = new JSONObject();

            try
            {
                // for header part
                //TEMPORARY VARIABLES FOR FORMING JSONARRAY AND JSONOBJECT OF HEARDER, DETAIL AND PAYMENTS
                JSONArray jArrTemp = new JSONArray();
                JSONObject jObjTemp = new JSONObject();

                jObjTemp.put("ORD_TYPE",6);
                jObjTemp.put("CUSTID",LoggedUser.customer.getCustId());
                jObjTemp.put("ORD_STATUS","I");
                jObjTemp.put("ORD_TOTAL", (Float.valueOf(mCartModel.getmTotal()) + oth_charges));
                jObjTemp.put("PRODUCT_COUNT",mCartModel.getProductItemList().size());
                jObjTemp.put("PRIMARY_EMAIL",LoggedUser.customer.getEmailId());
                jObjTemp.put("DEL_PRIORITY",5);
                jObjTemp.put("LANDMARK", delLandmark);
                jObjTemp.put("DELIVERY_DATE",getIntent().getExtras().getString("DateString"));
                jObjTemp.put("DELIVERY_FIRST_NAME",delFName);
                jObjTemp.put("DELIVERY_LAST_NAME",delLName);
                jObjTemp.put("DELIVERY_PHONE",delPhone);
                jObjTemp.put("DELIVERY_COMPANY","NA");
                jObjTemp.put("DELIVERY_STREET1", delAdd);
                jObjTemp.put("DELIVERY_STREET2","");
                jObjTemp.put("DELIVERY_CITY",delCity);
                jObjTemp.put("DELIVERY_ZONE", delArea);
                jObjTemp.put("DELIVERY_POSTAL_CODE",delPincode);
                jObjTemp.put("DELIVERY_COUNTRY","IND");
                jObjTemp.put("BILLING_FIRST_NAME",billingFName);
                jObjTemp.put("BILLING_LAST_NAME",billingLName);
                jObjTemp.put("BILLING_PHONE",LoggedUser.customer.getMobNo1());
                jObjTemp.put("BILLING_COMPANY","NA");
                jObjTemp.put("BILLING_STREET1", LoggedUser.customer.getAddress1()+", "+LoggedUser.customer.getArea1()+", "+LoggedUser.customer.getCity1()+", "+LoggedUser.customer.getLandMark1()+", "+ LoggedUser.customer.getPinCode1());
                jObjTemp.put("BILLING_STREET2","");
                jObjTemp.put("BILLING_CITY",LoggedUser.customer.getCity1());
                jObjTemp.put("BILLING_ZONE", LoggedUser.customer.getArea1());
                jObjTemp.put("BILLING_POSTAL_CODE",LoggedUser.customer.getPinCode1());
                jObjTemp.put("BILLING_COUNTRY","IND");
                jObjTemp.put("GEN_PO_IND", "");
                jObjTemp.put("GEN_PO_IND_BY", "");
                jObjTemp.put("GEN_PO_IND_ON", "");
                jObjTemp.put("TIME_PERIOD",getIntent().getExtras().getString("TimeString"));
                jObjTemp.put("AGENT_ID", "");
                jObjTemp.put("NARRATION",((TextView) findViewById(R.id.instructions)).getText().toString());
                jObjTemp.put("ORD_GEN_TYPE", "W");
                jObjTemp.put("COMM_CONF_IND", "");
                jObjTemp.put("COMM_CONF_IND_BY", "");
                jObjTemp.put("COMM_CONF_IND_ON", "");
                jObjTemp.put("JV_VNO", "");
                jObjTemp.put("INT_TRF_VNO", "");
                jObjTemp.put("PARENT_ORDER_ID", "");
                jObjTemp.put("PARENT_ORDER_ID", "");
                jObjTemp.put("DISCOUNT",mCartModel.getmDiscount());
                jObjTemp.put("OTHER_CHARGES", oth_charges);
                jObjTemp.put("ROUND_OFF","");
                jObjTemp.put("BOOKING_STATUS","I");
                jObjTemp.put("DELIV_PRIORITY", "STS");

                jArrTemp.put(jObjTemp);
                // formation of main json object
                jObjMain.put("ORD_HDR", jArrTemp);

                // for detail part
                jArrTemp = new JSONArray();
                for(int i=0; i<mCartModel.getProductItemList().size(); i++ )
                {
                    ProductCommonModel prodModel = mCartModel.getProductItemList().get(i);

                    jObjTemp = new JSONObject();
                    jObjTemp.put("BR_CODE", prodModel.getProductBrCode());
                    jObjTemp.put("LINE_ITEM_ID", i+1);
                    jObjTemp.put("P_CODE", prodModel.getProductCode());
                    jObjTemp.put("QTY", prodModel.getSelectedQty());
                    jObjTemp.put("PROD_MRP", prodModel.getProductMRP());
                    jObjTemp.put("PROD_RATE", prodModel.getProductDFRate());
                    jObjTemp.put("GL_CODE", "");
                    jObjTemp.put("REMARKS", "");
                    jObjTemp.put("ITM_PROMO_CODE", "");
                    jObjTemp.put("ITM_SCHEME_DISC", "");

                    jArrTemp.put(jObjTemp);
                }
                // formation of main json object
                jObjMain.put("ORD_DET", jArrTemp);

                // for payment part
                jArrTemp = new JSONArray();
                jObjTemp = new JSONObject();
                jObjTemp.put("PMT_TYPE", pmt_type);
                jObjTemp.put("AMOUNT", (Float.valueOf(mCartModel.getmTotal()) + oth_charges));
                jObjTemp.put("REMARK", ((TextView) findViewById(R.id.instructions)).getText().toString());
                jObjTemp.put("TRANSACTION_ID", "");
                jArrTemp.put(jObjTemp);

                // formation of main json object
                jObjMain.put("ORD_PMT", jArrTemp);
                jArrMain.put(jObjMain);
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
            callApi(Apis.ORDER_DETAILS_URL, jArrMain, Apis.ORDER_DETAILS_CODE);
        }
        else
            Snackbar.make(mPlaceMyOrder, "PLEASE SELECT A MODE OF PAYMENT.", Snackbar.LENGTH_SHORT).show();
    }

    private void callApi(String _requestUrl, JSONArray _jArr, int _requestCode)
    {
        mAsyncHttpRequest = new AsyncHttpRequest(mContext, _requestUrl, _jArr, _requestCode, AsyncHttpRequest.Type.POST);
        mAsyncHttpRequest.setRequestListener(this);
        mAsyncHttpRequest.execute();
    }

    private void swapAdd()
    {
        JSONArray jArrMain = new JSONArray();
        JSONArray jArrMain1 = new JSONArray();
        try
        {
            JSONArray jArr = new JSONArray();
            JSONObject jObj = new JSONObject();
            jObj.put("CUSTID", LoggedUser.customer.getCustId());
            jObj.put("NAME", LoggedUser.customer.getCustName2());
            jObj.put("EMAIL", LoggedUser.customer.getEmailId());
            jObj.put("ADDRESS", LoggedUser.customer.getAddress2());
            jObj.put("TOWN", LoggedUser.customer.getTown2());
            jObj.put("DISTRICT", LoggedUser.customer.getDistrict2());
            jObj.put("AREA", LoggedUser.customer.getArea2());
            jObj.put("CITY", LoggedUser.customer.getCity2());
            jObj.put("PINCODE", LoggedUser.customer.getPinCode2());
            jObj.put("DEL_CHARGES", LoggedUser.customer.getDelCharges2());
            jObj.put("LANDMARK", LoggedUser.customer.getLandMark2());
            jArr.put(jObj);

            JSONObject jObjMain = new JSONObject();
            jObjMain.put("PRIMARY_ADD", jArr);
            jArrMain.put(jObjMain);
            callApi(Apis.USER_INFO_URL, jArrMain, Apis.USER_INFO_CODE);


            JSONArray jArr1 = new JSONArray();
            JSONObject jObj1 = new JSONObject();
            jObj1.put("CUSTID", LoggedUser.customer.getCustId());
            jObj1.put("ALT_ID", LoggedUser.customer.getAltId());
            jObj1.put("NAME", LoggedUser.customer.getCustName1());
            jObj1.put("MOB_NO", LoggedUser.customer.getMobNo1());
            jObj1.put("ADDRESS", LoggedUser.customer.getAddress1());
            jObj.put("TOWN", LoggedUser.customer.getTown1());
            jObj.put("DISTRICT", LoggedUser.customer.getDistrict1());
            jObj1.put("AREA", LoggedUser.customer.getArea1());
            jObj1.put("CITY", LoggedUser.customer.getCity1());
            jObj1.put("PINCODE", LoggedUser.customer.getPinCode1());
            jObj.put("DEL_CHARGES", LoggedUser.customer.getDelCharges1());
            jObj1.put("LANDMARK", LoggedUser.customer.getLandMark1());
            jArr1.put(jObj1);

            JSONObject jObjMain1 = new JSONObject();
            jObjMain1.put("SECONDARY_ADD", jArr1);
            jArrMain1.put(jObjMain1);
            callApi(Apis.USER_INFO_URL, jArrMain1, Apis.USER_INFO_CODE);

            db.open();
            db.updatePrimaryAdd(jArrMain.getJSONObject(0).getJSONArray("PRIMARY_ADD"));
            db.updateSecondaryAdd(jArrMain1.getJSONObject(0).getJSONArray("SECONDARY_ADD"), jArr1.getJSONObject(0).getString("ALT_ID"));
            LoggedUser.customer = db.user();
            db.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    @Override
    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.txtReviewOrder:
            {
                Bundle bn = new Bundle();
                bn.putString("MODE", AppConstants.ApplicationLayoutModeAction.REVIEW_ORDER.name());
                Intent reviewOrderIntent = new Intent(OrderOverviewActivity.this, CartActivity.class).putExtras(bn);
                startActivityForResult(reviewOrderIntent, 0);
            }
            break;
            case R.id.deliveryDetEdit:
            {
                Bundle bn  = new Bundle();
                bn.putString("ADD_TYPE", addType.name());
                Intent deliverySpecsIntent = new Intent(OrderOverviewActivity.this, DeliverySpecsActivity.class).putExtras(bn);
                startActivityForResult(deliverySpecsIntent, 0);
            }
            break;
            case R.id.EditAdd1:
            {
                Bundle bn = new Bundle();
                bn.putInt("TYPE", 0);
                bn.putBoolean("EXIT", true);
                bn.putString("DateString",date);
                bn.putString("TimeString",time);
                bn.putString("AddType",AppConstants.DeliveryAddressType.PRIMARY.name());
                Intent userInfoIntent = new Intent(OrderOverviewActivity.this, UserInfoActivity.class).putExtras(bn);
                startActivityForResult(userInfoIntent, 0);
            }
            break;
            case R.id.EditAdd2:
            {
                Bundle bn = new Bundle();
                bn.putInt("TYPE", 1);
                bn.putBoolean("EXIT", true);
                bn.putString("DateString",date);
                bn.putString("TimeString",time);
                bn.putString("AddType",AppConstants.DeliveryAddressType.SECONDARY.name());
                Intent userInfoIntent = new Intent(OrderOverviewActivity.this, UserInfoActivity.class).putExtras(bn);
                startActivityForResult(userInfoIntent, 0);
            }
            break;
            case R.id.place_my_order:
            {
                placeOrder();
            }
            break;
            case R.id.priAddressCheck:
            {
                addType = AppConstants.DeliveryAddressType.PRIMARY;
                System.out.println("PRIMARY");
                System.out.println("CHARGES " + LoggedUser.customer.getDelCharges1());
                mPriAddrClick.setImageResource(R.drawable.ic_address_check_filled);
                mSecAddrClick.setImageResource(R.drawable.ic_address_check);
                oth_charges = Float.valueOf(LoggedUser.customer.getDelCharges1().toString());
                ((TextView) findViewById(R.id.valueCharged)).setText( mContext.getResources().getString(R.string.rupees) + " " +oth_charges);
                ((TextView) findViewById(R.id.txtTotal)).setText("TOTAL : " + mContext.getResources().getString(R.string.rupees) + " " + (Float.valueOf(mCartModel.getmTotal()) + oth_charges));
            }
            break;
            case R.id.secAddressCheck:
            {
                addType = AppConstants.DeliveryAddressType.SECONDARY;
                System.out.println("SECONDARY");
                System.out.println("CHARGES " + LoggedUser.customer.getDelCharges2());
                mPriAddrClick.setImageResource(R.drawable.ic_address_check);
                mSecAddrClick.setImageResource(R.drawable.ic_address_check_filled);
                oth_charges = Float.valueOf(LoggedUser.customer.getDelCharges2().toString());
                ((TextView) findViewById(R.id.valueCharged)).setText( mContext.getResources().getString(R.string.rupees) + " " +oth_charges);
                ((TextView) findViewById(R.id.txtTotal)).setText("TOTAL : " + mContext.getResources().getString(R.string.rupees) + " " + (Float.valueOf(mCartModel.getmTotal()) + oth_charges));
            }
            break;
            case R.id.btnChange:
            {
                if((((TextView) findViewById(R.id.txtName)).getText().toString()).contains(LoggedUser.customer.getCustName1()) )
                {
                    System.out.println("PRIMARY");
                    // user details
                    ((TextView) findViewById(R.id.txtName)).setText("NAME : " + LoggedUser.customer.getCustName2());
                    ((TextView) findViewById(R.id.txtMobileNo)).setText("MOBILE : " + LoggedUser.customer.getMobNo2());
                    ((TextView) findViewById(R.id.txtAddress)).setText("ADDRESS : " + LoggedUser.customer.getAddress2());
                    ((TextView) findViewById(R.id.txtLandmark)).setText("LANDMARK : " + LoggedUser.customer.getLandMark2());

                    // secondary details
                    ((TextView) findViewById(R.id.txtName2)).setText("NAME : " + LoggedUser.customer.getCustName1());
                    ((TextView) findViewById(R.id.txtMobileNo2)).setText("MOBILE : " + LoggedUser.customer.getMobNo1());
                    ((TextView) findViewById(R.id.txtAddress2)).setText("ADDRESS : " + LoggedUser.customer.getAddress1());
                    ((TextView) findViewById(R.id.txtLandmark2)).setText("LANDMARK : " + LoggedUser.customer.getLandMark1());

                    swapAdd();
                }
                else if((((TextView) findViewById(R.id.txtName)).getText().toString()).contains(LoggedUser.customer.getCustName2()))
                {
                    System.out.println("SECONDARY");

                    // user details
                    ((TextView) findViewById(R.id.txtName)).setText("NAME : " + LoggedUser.customer.getCustName1());
                    ((TextView) findViewById(R.id.txtMobileNo)).setText("MOBILE : " + LoggedUser.customer.getMobNo1());
                    ((TextView) findViewById(R.id.txtAddress)).setText("ADDRESS : " + LoggedUser.customer.getAddress1());
                    ((TextView) findViewById(R.id.txtLandmark)).setText("LANDMARK : " + LoggedUser.customer.getLandMark1());

                    // secondary details
                    ((TextView) findViewById(R.id.txtName2)).setText("NAME : " + LoggedUser.customer.getCustName2());
                    ((TextView) findViewById(R.id.txtMobileNo2)).setText("MOBILE : " + LoggedUser.customer.getMobNo2());
                    ((TextView) findViewById(R.id.txtAddress2)).setText("ADDRESS : " + LoggedUser.customer.getAddress2());
                    ((TextView) findViewById(R.id.txtLandmark2)).setText("LANDMARK : " + LoggedUser.customer.getLandMark2());

                    swapAdd();
                }
                else
                {
                    System.out.println("NONE");
                }
            }
            break;
        }
    }

    @Override
    public void onRequestCompleted(String response, int requestCode) {
        System.out.println("RESPONSE : " + response);
        try
        {
            if(!response.equals("[]"))
            {
                JSONArray jArray = new JSONArray(response.trim());
                switch(requestCode)
                {
                    case Apis.GET_PMT_TYPE_CODE:
                    {
                        if (jArray.getJSONObject(0).has("STATUS"))
                        {
                            if (jArray.getJSONObject(0).getBoolean("STATUS"))
                            {
                                JSONArray jPmtArr = jArray.getJSONObject(0).getJSONArray("LIST");
                                RadioGroup.LayoutParams rprms;

                                for (int i = 0; i < jPmtArr.length(); i++)
                                {
                                    RadioButton rdoBtn = new RadioButton(this);
                                    rdoBtn.setText(jPmtArr.getJSONObject(i).getString("PMT_NAME"));
                                    rdoBtn.setId(jPmtArr.getJSONObject(i).getInt("PMT_TYPE"));
                                    if (i == 0)
                                        rdoBtn.setChecked(true);
                                    rprms= new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT);
                                    mRdoPmtGrp.addView(rdoBtn, rprms);
                                }
                            }
                            else
                            {
                                Snackbar.make(mPlaceMyOrder, "ERROR IN FETCHING PAYMENT TYPES OR NO PAYMENT TYPES AVAILABLE.", Snackbar.LENGTH_LONG).show();
                            }
                        }
                        else
                        {
                            Snackbar.make(mPlaceMyOrder, "NO PAYMENT TYPES FETCHED. ", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                    break;
                    case Apis.ORDER_DETAILS_CODE:
                    {
                        if (jArray.getJSONObject(0).has("STATUS"))
                        {

                            if (jArray.getJSONObject(0).getBoolean("STATUS"))
                            {
                                Bundle bn = new Bundle();
                                bn.putString("OrderId",jArray.getJSONObject(0).getString("ORD_ID"));
                                Intent intent = new Intent(OrderOverviewActivity.this, ThankYouActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.putExtras(bn);
                                startActivity(intent);
                                Snackbar.make(mPlaceMyOrder, "ORDER PLACE SUCCESSFULLY. " + jArray.getJSONObject(0).getString("ORD_ID"), Snackbar.LENGTH_SHORT).show();
                                finish();
                                db.open();
                                db.emptyCart();
                                db.close();
                            }
                            else
                            {
                                Snackbar.make(mPlaceMyOrder, "UNSUCCESSFULL. " + jArray.getJSONObject(0).getString("ORD_ID"), Snackbar.LENGTH_SHORT).show();
                            }
                        }
                        else
                        {
                            Snackbar.make(mPlaceMyOrder, "NO DATA FETCHED. ", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                    break;
                    case Apis.USER_INFO_CODE:
                    {
                        if (jArray.length() > 0)
                        {
                            if (jArray.getJSONObject(0).getBoolean("STATUS"))
                            {
                            }
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
    public void onRequestError(Exception e, int requestCode) {
        Utils.showToast(this, "Please check your Internet Connection");
    }

    @Override
    public void onRequestStarted(int requestCode) {

    }
}
