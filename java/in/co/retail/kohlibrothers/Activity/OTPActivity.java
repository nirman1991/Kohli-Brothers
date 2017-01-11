package in.co.retail.kohlibrothers.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import in.co.retail.kohlibrothers.DBAdapter.SQLiteAdapter;
import in.co.retail.kohlibrothers.Model.Apis;
import in.co.retail.kohlibrothers.Model.CustomerDetailsModel;
import in.co.retail.kohlibrothers.Model.LoggedUser;
import in.co.retail.kohlibrothers.R;
import in.co.retail.kohlibrothers.Utility.Utils;
import in.co.retail.kohlibrothers.Webservice.AsyncHttpRequest;

/**
 * Created by nirman on 4/20/2016.
 */
public class OTPActivity extends AppCompatActivity implements AsyncHttpRequest.RequestListener
{

    private static String TAG = OTPActivity.class.getSimpleName();

    private AsyncHttpRequest mAsyncHttpRequest;
    private Context mContext;
    private EditText mTxtOTP;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_otp);
        System.out.println("TAG : " + TAG);
        mContext = this;

        final int _genOTP = getIntent().getExtras().getInt("OTP");
        final String _mobNo = getIntent().getExtras().getString("mobNo");

        System.out.println("OTP : " + _genOTP);
        System.out.println("MOBILE NO : " + _mobNo);

        mTxtOTP = (EditText) findViewById(R.id.txtOTP);
        Button _btnOTPvalidate = (Button) findViewById(R.id.btnOTPvalidate);
        _btnOTPvalidate.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (!mTxtOTP.getText().toString().equals(""))
                {
                    if (_genOTP == Integer.valueOf(mTxtOTP.getText().toString()))
                    {
                        Snackbar.make(mTxtOTP, "VALID OTP...", Snackbar.LENGTH_SHORT).show();

                        try {
                            JSONArray jArr = new JSONArray();
                            JSONObject jObj = new JSONObject();
                            jObj.put("MOBILE_NO", _mobNo);
                            jArr.put(jObj);

                            callApi(Apis.AUTHORIZE_URL, jArr, Apis.AUTHORIZE_CODE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    else
                        Snackbar.make(mTxtOTP, "INVALID OTP. PLEASE TRY AGAIN!!!", Snackbar.LENGTH_SHORT).show();
                }
                else
                {
                    Snackbar.make(mTxtOTP, "OTP FIELD IS COMPULSORY.", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadDBUser(JSONArray _details)
    {
        System.out.println("JSON ARR "+_details);
        try
        {
            String temp = "";
            CustomerDetailsModel userModel = new CustomerDetailsModel();

            userModel.setCustId(_details.getJSONObject(0).getString("CUSTID"));

            temp = _details.getJSONObject(0).getString("CUST_TITLE");
            userModel.setCustTitle(temp.equals("null") ? "" : temp);

            temp = _details.getJSONObject(0).getString("CUST_EMAIL");
            userModel.setEmailId(temp.equals("null") ? "" : temp);

            temp = _details.getJSONObject(0).getString("ALT_ID");
            userModel.setAltId(temp.equals("null") ? "" : temp);


            temp = _details.getJSONObject(0).getString("CUST_NAME1");
            userModel.setCustName1(temp.equals("null")?"" : temp);

            temp = _details.getJSONObject(0).getString("TEXT_ADDRESS1");
            userModel.setAddress1(temp.equals("null") ? "" : temp);

            temp = _details.getJSONObject(0).getString("TOWN1");
            userModel.setTown1(temp.equals("null") ? "" : temp);

            temp = _details.getJSONObject(0).getString("DISTRICT1");
            userModel.setDistrict1(temp.equals("null") ? "" : temp);

            temp = _details.getJSONObject(0).getString("AREA1");
            userModel.setArea1(temp.equals("null") ? "" : temp);

            temp = _details.getJSONObject(0).getString("CITY1");
            userModel.setCity1(temp.equals("null") ? "" : temp);

            temp = _details.getJSONObject(0).getString("PIN_CODE1");
            userModel.setPinCode1(temp.equals("null") ? "" : temp);

            temp = _details.getJSONObject(0).getString("LANDMARK1");
            userModel.setLandMark1(temp.equals("null") ? "" : temp);

            temp = _details.getJSONObject(0).getString("DEL_CHARGES1");
            userModel.setDelCharges1(temp.equals("null") ? "" : temp);

            userModel.setMobNo1(_details.getJSONObject(0).getString("MOB_NO1"));


            temp = _details.getJSONObject(0).getString("CUST_NAME2");
            userModel.setCustName2(temp.equals("null") ? "" : temp);

            temp = _details.getJSONObject(0).getString("TEXT_ADDRESS2");
            userModel.setAddress2(temp.equals("null") ? "" : temp);

            temp = _details.getJSONObject(0).getString("TOWN2");
            userModel.setTown2(temp.equals("null") ? "" : temp);

            temp = _details.getJSONObject(0).getString("DISTRICT2");
            userModel.setDistrict2(temp.equals("null") ? "" : temp);

            temp = _details.getJSONObject(0).getString("AREA2");
            userModel.setArea2(temp.equals("null") ? "" : temp);

            temp = _details.getJSONObject(0).getString("CITY2");
            userModel.setCity2(temp.equals("null") ? "" : temp);

            temp = _details.getJSONObject(0).getString("PIN_CODE2");
            userModel.setPinCode2(temp.equals("null") ? "" : temp);

            temp = _details.getJSONObject(0).getString("LANDMARK2");
            userModel.setLandMark2(temp.equals("null") ? "" : temp);

            temp = _details.getJSONObject(0).getString("DEL_CHARGES2");
            userModel.setDelCharges2(temp.equals("null") ? "" : temp);

            temp = _details.getJSONObject(0).getString("MOB_NO2");
            userModel.setMobNo2(temp.equals("null") ? "" : temp);



            // save to database
            SQLiteAdapter db = new SQLiteAdapter(this);
            db.open();
            db.saveUser(userModel);
            db.close();
            LoggedUser.init(userModel);
            Snackbar.make(mTxtOTP, "Sign up done successfully.", Snackbar.LENGTH_SHORT).show();

            //start HomeActivity through OTP validation
            Intent intentHome = new Intent(OTPActivity.this, HomeActivity.class);
            System.out.println("Sign up done successfully. "+LoggedUser.customer.getMobNo1());
            startActivity(intentHome);
            finish();/**/
        }
        catch (Exception e)
        {
            // TODO: handle exception
        }
    }

    private void callApi(String _requestUrl, JSONArray _jArr, int _requestCode)
    {
        mAsyncHttpRequest = new AsyncHttpRequest(mContext, _requestUrl, _jArr, _requestCode, AsyncHttpRequest.Type.POST);
        mAsyncHttpRequest.setRequestListener(this);
        mAsyncHttpRequest.execute();
    }

    @Override
    public void onRequestCompleted(String response, int requestCode)
    {
        try
        {
            if(!response.equals("[]"))
            {
                JSONArray jArray = new JSONArray(response.trim());
                switch(requestCode)
                {
                    case Apis.AUTHORIZE_CODE:
                    {
                        if (!jArray.getJSONObject(0).has("ERROR"))
                        {
                            System.out.println("RESPONSE : " +response);
                            if (jArray.getJSONObject(0).getBoolean("STATUS"))
                            {
                                loadDBUser(jArray.getJSONObject(0).getJSONArray("DETAILS"));
                            }
                            else
                            {
                                Snackbar.make(mTxtOTP, "FAILED TO VALIDATE. PLEASE TRY AGAIN", Snackbar.LENGTH_SHORT).show();
                            }
                        }
                        else
                        {
                            Snackbar.make(mTxtOTP, jArray.getJSONObject(0).getString("ERROR"), Snackbar.LENGTH_SHORT).show();
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
