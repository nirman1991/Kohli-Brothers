package in.co.retail.kohlibrothers.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import in.co.retail.kohlibrothers.Model.Apis;
import in.co.retail.kohlibrothers.Model.LoggedUser;
import in.co.retail.kohlibrothers.R;
import in.co.retail.kohlibrothers.Webservice.AsyncHttpRequest;

/**
 * Created by nirman on 4/28/2016.
 */
public class ThankYouActivity extends AppCompatActivity implements View.OnClickListener, AsyncHttpRequest.RequestListener
{
    private static String TAG = ThankYouActivity.class.getSimpleName();

    private Context mContext;
    private ImageView mImgBack;
    private TextView mTxtOrdId;
    private AsyncHttpRequest mAsyncHttpRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thank_you);
        System.out.println("TAG : " + TAG);
        mContext = this;

        mTxtOrdId = (TextView) findViewById(R.id.txtOrdId);
        mImgBack = (ImageView) findViewById(R.id.imgBack);
        mImgBack.setOnClickListener(this);

        initData(getIntent());
    }

    private void initData(Intent intent) {
        if (intent != null)
        {
            mTxtOrdId.setText("ORDER ID : " + intent.getExtras().getString("OrderId"));
            JSONArray jArr =  new JSONArray();
            JSONObject jObj = new JSONObject();

            JSONArray jArrMail =  new JSONArray();
            JSONObject jObjMail = new JSONObject();
            try
            {
                //jObj.put("ORDER_ID",intent.getExtras().getString("OrderId"));
                //jObj.put("MOBILE_NO", LoggedUser.customer.getMobNo1());
                //jArr.put(jObj);
                //callApi(Apis.OTP_URL,jArr, Apis.OTP_CODE );

                jObjMail.put("ORDER_ID",intent.getExtras().getString("OrderId"));
                jObjMail.put("EMAIL",LoggedUser.customer.getEmailId());
                jArrMail.put(jObjMail);
                callApi(Apis.SEND_MAIL_URL, jArrMail, Apis.SEND_MAIL_CODE);
            }
            catch (Exception e)
            {

            }
        }
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
        if(v.getId() == R.id.imgBack)
        {
            Intent intent = new Intent(ThankYouActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            Snackbar.make(mTxtOrdId, "The Order is processed. You cannot go back.", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onRequestCompleted(String response, int requestCode)
    {

    }

    @Override
    public void onRequestError(Exception e, int requestCode)
    {

    }

    @Override
    public void onRequestStarted(int requestCode)
    {

    }
}
