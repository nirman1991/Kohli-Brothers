package in.co.retail.kohlibrothers.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import in.co.retail.kohlibrothers.Model.Apis;
import in.co.retail.kohlibrothers.R;
import in.co.retail.kohlibrothers.Utility.Loading;
import in.co.retail.kohlibrothers.Utility.Utils;
import in.co.retail.kohlibrothers.Webservice.AsyncHttpRequest;

/**
 * Created by nirman on 3/19/2016.
 */
public class AuthorizeActivity extends AppCompatActivity implements View.OnClickListener, AsyncHttpRequest.RequestListener
{
    private static String TAG = AuthorizeActivity.class.getSimpleName();

    private Button mBtnValidate;
    private TextView mRead;
    private CheckBox mChkTerms;
    private EditText mMobNo;
    private ImageView mFbLike, mTwitterLike;
    private AsyncHttpRequest mAsyncHttpRequest;
    private Context mContext;
    private Loading mLoading;
    private String[] permission = {"android.permission.SEND_SMS"};

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorize);
        System.out.println("TAG : " + TAG);
        mContext = this;
        mMobNo = (EditText) findViewById(R.id.mob_no);
        mChkTerms = (CheckBox) findViewById(R.id.chk_terms);
        mBtnValidate = (Button) findViewById(R.id.btn_validate);
        mBtnValidate.setOnClickListener(this);

        mRead = (TextView) findViewById(R.id.read);
        mRead.setOnClickListener(this);


        /*mFbLike = (ImageView) findViewById(R.id.fbLike);
        mFbLike.setOnClickListener(this);

        mTwitterLike = (ImageView) findViewById(R.id.twitterLike);
        mTwitterLike.setOnClickListener(this);/**/
        askPermission();
    }


    public void askPermission()
    {
        if(Build.VERSION.SDK_INT> Build.VERSION_CODES.LOLLIPOP_MR1)
        {
            if(checkSelfPermission("android.permission.SEND_SMS") == PackageManager.PERMISSION_GRANTED)
            {
                System.out.println("GRANTED");
            }
            else
            {
                System.out.println("DENIED");
                requestPermissions(permission,200);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode)
        {
            case 200:
                boolean Accepted = grantResults[0]==PackageManager.PERMISSION_GRANTED;
                break;
        }
    }

    @Override
    public void onClick(View v)
    {
        int id = v.getId();
        switch(id)
        {
            case R.id.btn_validate:
                System.out.println("CLICKED");
                if(mMobNo.getText().toString() != null)
                {
                    if (mMobNo.getText().toString().length() == 10 && mChkTerms.isChecked())
                    {
                        System.out.println("Correct Login.");
                        JSONArray jArr = new JSONArray();
                        try
                        {
                            JSONObject jObj = new JSONObject();
                            jObj.put("MOBILE_NO", mMobNo.getText().toString());
                            jArr.put(jObj);
                            callApi(Apis.OTP_URL, jArr, Apis.OTP_CODE);

                            //Bundle bn = new Bundle();
                            //bn.putInt("OTP", 123456);
                            //bn.putString("mobNo", mMobNo.getText().toString());
                            //Intent intentOTP = new Intent(AuthorizeActivity.this, OTPActivity.class);
                            //intentOTP.putExtras(bn);
                            //startActivity(intentOTP);
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                    else
                    {
                        Snackbar.make(mMobNo, "Please insert valid Mobile Number OR check the Terms & Conditions to proceed", Snackbar.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Snackbar.make(mMobNo, "Please insert Mobile Number", Snackbar.LENGTH_SHORT).show();
                }

                break;
            case R.id.read:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                browserIntent.setData(Uri.parse(Apis.URL + "terms_conditions"));
                startActivity(browserIntent);
                break;
            /*case R.id.fbLike:
                Intent fbIntent = new Intent(Intent.ACTION_VIEW);
                fbIntent.setData(Uri.parse("https://www.facebook.com/nxtbazaar/?fref=ts"));
                startActivity(fbIntent);
                break;
            case R.id.twitterLike:
                Intent twitterIntent = new Intent(Intent.ACTION_VIEW);
                twitterIntent.setData(Uri.parse("https://twitter.com/NxtBazaar"));
                startActivity(twitterIntent);
                break;/**/
        }
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
                    case Apis.OTP_CODE:
                    {
                        if(!jArray.getJSONObject(0).has("ERROR"))
                        {
                            if (jArray.getJSONObject(0).getBoolean("STATUS"))
                            {
                                JSONArray jArr = new JSONArray();
                                JSONObject jObj = new JSONObject();
                                jObj.put("OTP", jArray.getJSONObject(0).getString("OTP"));
                                jObj.put("MOBILE_NO", jArray.getJSONObject(0).getString("MOBILE_NO"));
                                jArr.put(jObj);
                                validateOTP(jArr);
                            }
                            else
                                Snackbar.make(mMobNo, jArray.getJSONObject(0).getString("MESSAGE"), Snackbar.LENGTH_SHORT).show();
                        }
                        else
                            Snackbar.make(mMobNo, jArray.getJSONObject(0).getString("ERROR"), Snackbar.LENGTH_SHORT).show();
                    }
                    break;
                }
                mLoading.dismiss();
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    private void validateOTP(JSONArray _jArr)
    {
        int _genOTP = 0;
        String _mobNo = "";
        try
        {
            _genOTP = _jArr.getJSONObject(0).getInt("OTP");
            _mobNo = _jArr.getJSONObject(0).getString("MOBILE_NO");
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        Bundle bn = new Bundle();
        bn.putInt("OTP", _genOTP);
        bn.putString("mobNo", _mobNo);
        Intent intentOTP = new Intent(AuthorizeActivity.this, OTPActivity.class);
        intentOTP.putExtras(bn);
        startActivity(intentOTP);
        finish();
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

    private void callApi(String _requestUrl, JSONArray _jArr, int _requestCode)
    {
        mAsyncHttpRequest = new AsyncHttpRequest(mContext, _requestUrl, _jArr, _requestCode, AsyncHttpRequest.Type.POST);
        mAsyncHttpRequest.setRequestListener(this);
        mAsyncHttpRequest.execute();
    }
}
