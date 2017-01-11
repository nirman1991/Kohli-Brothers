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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import in.co.retail.kohlibrothers.Controller.ActionBarHelper;
import in.co.retail.kohlibrothers.DBAdapter.SQLiteAdapter;
import in.co.retail.kohlibrothers.Model.Apis;
import in.co.retail.kohlibrothers.Model.CustomerDetailsModel;
import in.co.retail.kohlibrothers.Model.LoggedUser;
import in.co.retail.kohlibrothers.R;
import in.co.retail.kohlibrothers.Utility.AppConstants;
import in.co.retail.kohlibrothers.Utility.Loading;
import in.co.retail.kohlibrothers.Utility.Utils;
import in.co.retail.kohlibrothers.Webservice.AsyncHttpRequest;


/**
 * Created by Nirman on 4/27/2016.
 */
public class UserInfoActivity extends AppCompatActivity implements AsyncHttpRequest.RequestListener, View.OnClickListener
{
    private static String TAG = UserInfoActivity.class.getSimpleName();
    private Context mContext;
    private Toolbar mToolbar;
    private AsyncHttpRequest mAsyncHttpRequest;
    private SQLiteAdapter db;
    private Loading mLoading;
    private Spinner spinArea1, spinArea2, spinTown1, spinTown2;
    private TextView txtEditPAdd, txtEditSAdd, txtMobNo, txtTown1, txtArea1, txtCity1, txtPinCode1, txtDel1, txtTown2, txtArea2, txtCity2, txtPinCode2, txtDel2, deliveryCaption;
    private EditText txtName, txtEmail, txtAddress1, txtLandmark1, txtSecondaryName, txtSecondaryMobNo, txtAddress2, txtLandmark2;
    //private Button btnSaveP, btnSaveS, btnProceed;
    private Button btnAction;
    private RadioButton btnAddPri, btnAddSec;
    //private RadioGroup rdoAddGrp;
    private AppConstants.DeliveryAddressType addType = AppConstants.DeliveryAddressType.NONE;

    private String[] districtArr, townArr, cityArr, areaArr, pincodeArr, delChargesArr;

    private String del1, del2, dist1, dist2, custId ;

    private JSONArray jArrMain, jArray, tempJArr;

    private boolean exit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        System.out.println("TAG : " + TAG);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        this.mContext = this;

        ActionBarHelper.init(this, mToolbar, true, true, "User Information", true);

        initSetArea();

        deliveryCaption = (TextView) findViewById(R.id.deliveryCaption);

        spinTown1 = (Spinner) findViewById(R.id.spinTown1);
        txtTown1 = (TextView) findViewById(R.id.txtTown1);
        spinArea1 = (Spinner) findViewById(R.id.spinArea1);
        txtArea1 = (TextView) findViewById(R.id.txtArea1);
        txtCity1 = (TextView) findViewById(R.id.txtCity1);
        txtMobNo = (TextView) findViewById(R.id.txtMobNo);
        txtPinCode1 = (TextView) findViewById(R.id.txtPinCode1);
        txtDel1 = (TextView) findViewById(R.id.txtDel1);

        spinTown2 = (Spinner) findViewById(R.id.spinTown2);
        txtTown2 = (TextView) findViewById(R.id.txtTown2);
        spinArea2 = (Spinner) findViewById(R.id.spinArea2);
        txtArea2 = (TextView) findViewById(R.id.txtArea2);
        txtCity2 = (TextView) findViewById(R.id.txtCity2);
        txtPinCode2 = (TextView) findViewById(R.id.txtPinCode2);
        txtDel2 = (TextView) findViewById(R.id.txtDel2);

        txtName = (EditText) findViewById(R.id.txtName);
        txtEmail = (EditText) findViewById(R.id.txtEmail);
        txtAddress1 = (EditText) findViewById(R.id.txtAddress1);
        txtLandmark1 = (EditText) findViewById(R.id.txtLandmark1);

        txtSecondaryName = (EditText) findViewById(R.id.txtSecondaryName);
        txtSecondaryMobNo = (EditText) findViewById(R.id.txtSecondaryMobNo);
        txtAddress2 = (EditText) findViewById(R.id.txtAddress2);
        txtLandmark2 = (EditText) findViewById(R.id.txtLandmark2);

        btnAddPri = (RadioButton) findViewById(R.id.btnAddPri);
        btnAddSec = (RadioButton) findViewById(R.id.btnAddSec);

        txtEditPAdd = (TextView) findViewById(R.id.txtEditPAdd);
        txtEditSAdd = (TextView) findViewById(R.id.txtEditSAdd);
        btnAction = (Button) findViewById(R.id.btnAction);

        txtEditPAdd.setOnClickListener(this);
        txtEditSAdd.setOnClickListener(this);
        btnAction.setOnClickListener(this);

        // for radio buttons
        btnAddPri.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(buttonView.getId() == R.id.btnAddPri && isChecked)
                {
                    System.out.println("PRIMARY SELECTED");
                    btnAddPri.setChecked(isChecked);
                    btnAddSec.setChecked(false);
                    addType = AppConstants.DeliveryAddressType.PRIMARY;
                    initSetData();
                }
            }
        });

        btnAddSec.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.getId() == R.id.btnAddSec && isChecked) {
                    System.out.println("SECONDARY SELECTED");
                    btnAddSec.setChecked(isChecked);
                    btnAddPri.setChecked(false);
                    addType = AppConstants.DeliveryAddressType.SECONDARY;
                    initSetData();
                }
            }
        });
    }

    private void initSetArea() {
        JSONArray jArr = new JSONArray();
        callApi(Apis.AREA_URL, jArr, Apis.AREA_CODE);
    }

    private void initData()
    {
        switch(getIntent().getExtras().getInt("TYPE"))
        {
            case -1: // for both addresses in user info activity
            {
                ((LinearLayout) findViewById(R.id.layoutPri)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.layoutSec)).setVisibility(View.VISIBLE);
                btnAddPri.setVisibility(View.GONE);
                btnAddSec.setVisibility(View.GONE);
                deliveryCaption.setVisibility(View.GONE);
                setEnabled(false, deliveryCaption, txtName, txtEmail, txtAddress1, spinTown1, txtTown1, spinArea1, txtArea1, txtLandmark1, btnAction, AppConstants.UserInfoBtn.SAVE, AppConstants.UserInfoSaveMode.NONE);
                setEnabled(false, deliveryCaption, txtSecondaryName, txtSecondaryMobNo, txtAddress2, spinTown2, txtTown2,  spinArea2, txtArea2, txtLandmark2, btnAction, AppConstants.UserInfoBtn.SAVE, AppConstants.UserInfoSaveMode.NONE);

            }
            break;
            case 0: // for primary address
            {
                ((LinearLayout) findViewById(R.id.layoutPri)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.layoutSec)).setVisibility(View.GONE);
                txtEditPAdd.setVisibility(View.INVISIBLE);
                setEnabled(true, deliveryCaption, txtName, txtEmail, txtAddress1, spinTown1, txtTown1, spinArea1, txtArea1, txtLandmark1, btnAction, AppConstants.UserInfoBtn.SAVE, AppConstants.UserInfoSaveMode.PRIMARY);
                btnAddPri.setVisibility(View.GONE);
                btnAddSec.setVisibility(View.GONE);
                deliveryCaption.setVisibility(View.GONE);
            }
            break;
            case 1: // for secondary address
            {
                ((LinearLayout) findViewById(R.id.layoutPri)).setVisibility(View.GONE);
                ((LinearLayout) findViewById(R.id.layoutSec)).setVisibility(View.VISIBLE);
                txtEditSAdd.setVisibility(View.INVISIBLE);
                setEnabled(true, deliveryCaption, txtSecondaryName, txtSecondaryMobNo, txtAddress2, spinTown2, txtTown2,  spinArea2, txtArea2, txtLandmark2, btnAction, AppConstants.UserInfoBtn.SAVE, AppConstants.UserInfoSaveMode.SECONDARY);
                btnAddPri.setVisibility(View.GONE);
                btnAddSec.setVisibility(View.GONE);
                deliveryCaption.setVisibility(View.GONE);
            }
            break;
            case 2: // for proceeding after clicking checkout page
            {
                ((LinearLayout) findViewById(R.id.layoutPri)).setVisibility(View.VISIBLE);
                ((LinearLayout) findViewById(R.id.layoutSec)).setVisibility(View.VISIBLE);
                btnAction.setText(AppConstants.UserInfoBtn.PROCEED);
                deliveryCaption.setVisibility(View.VISIBLE);
                txtEditSAdd.setVisibility(View.INVISIBLE);
            }
            break;
        }

        //fetch value for exiting @UserInfoActivity
        exit = getIntent().getExtras().getBoolean("EXIT");

        initSetData();
    }

    private void initSetData()
    {

        if(LoggedUser.customer != null) {
            custId = LoggedUser.customer.getCustId();
            txtMobNo.setText(mContext.getResources().getString(R.string.pre_fix_phone) + " " + LoggedUser.customer.getMobNo1());
            String custName1 = LoggedUser.customer.getCustName1();
            String emailId1 = LoggedUser.customer.getEmailId();
            String txtAdd1 = LoggedUser.customer.getAddress1();
            String landmark1 = LoggedUser.customer.getLandMark1();
            String area1 = LoggedUser.customer.getArea1();
            String town1 = LoggedUser.customer.getTown1();
            String del1 = LoggedUser.customer.getDelCharges1();

            String custName2 = LoggedUser.customer.getCustName2();
            String mobNo2 = LoggedUser.customer.getMobNo2();
            String txtAdd2 = LoggedUser.customer.getAddress2();
            String landmark2 = LoggedUser.customer.getLandMark2();
            String area2 = LoggedUser.customer.getArea2();
            String town2 = LoggedUser.customer.getTown2();
            String del2 = LoggedUser.customer.getDelCharges2();

            txtName.setText(custName1);
            txtEmail.setText(emailId1);
            txtAddress1.setText(txtAdd1);
            txtLandmark1.setText(landmark1);

            txtSecondaryName.setText(custName2);
            txtSecondaryMobNo.setText(mobNo2);
            txtAddress2.setText(txtAdd2);
            txtLandmark2.setText(landmark2);

            txtTown1.setText(town1);
            txtArea1.setText(area1);
            txtCity1.setText(LoggedUser.customer.getCity1());
            txtPinCode1.setText(LoggedUser.customer.getPinCode1());
            txtDel1.setText(del1);

            txtTown2.setText(town2);
            txtArea2.setText(area2);
            txtCity2.setText(LoggedUser.customer.getCity2());
            txtPinCode2.setText(LoggedUser.customer.getPinCode2());
            txtDel2.setText(del2);

            if(btnAddPri.isChecked() && btnAddPri.getVisibility() == View.VISIBLE)
            {
                setEnabled(false, deliveryCaption, txtSecondaryName, txtSecondaryMobNo, txtAddress2, spinTown2, txtTown2,  spinArea2, txtArea2, txtLandmark2, btnAction, AppConstants.UserInfoBtn.PROCEED, AppConstants.UserInfoSaveMode.CHECKOUT);
                if (custName1.equals("") && emailId1.equals("") && txtAdd1.equals("") && landmark1.equals("") && area1.equals("")) {
                    setEnabled(true, deliveryCaption, txtName, txtEmail, txtAddress1, spinTown1, txtTown1,  spinArea1, txtArea1, txtLandmark1, btnAction, AppConstants.UserInfoBtn.SP, AppConstants.UserInfoSaveMode.PRI_SAVE_CHECKOUT);
                    txtEditPAdd.setVisibility(View.INVISIBLE);
                    //btnProceed.setVisibility(View.GONE);

                }
                else {
                    setEnabled(false, deliveryCaption, txtName, txtEmail, txtAddress1, spinTown1, txtTown1,  spinArea1, txtArea1, txtLandmark1, btnAction, AppConstants.UserInfoBtn.PROCEED, AppConstants.UserInfoSaveMode.CHECKOUT);
                    txtEditPAdd.setVisibility(View.VISIBLE);
                }
                addType = AppConstants.DeliveryAddressType.PRIMARY;
                txtEditSAdd.setVisibility(View.INVISIBLE);
                btnAction.setVisibility(View.VISIBLE);
            }
            else if(btnAddSec.isChecked() && btnAddSec.getVisibility() == View.VISIBLE)
            {
                setEnabled(false, deliveryCaption, txtName, txtEmail, txtAddress1, spinTown1, txtTown1, spinArea1, txtArea1, txtLandmark1, btnAction, AppConstants.UserInfoBtn.PROCEED, AppConstants.UserInfoSaveMode.CHECKOUT);
                if (custName2.equals("") && mobNo2.equals("") && txtAdd2.equals("") && landmark2.equals("") && area2.equals("")) {
                    setEnabled(true, deliveryCaption, txtSecondaryName, txtSecondaryMobNo, txtAddress2, spinTown2, txtTown2,  spinArea2, txtArea2, txtLandmark2, btnAction, AppConstants.UserInfoBtn.SP, AppConstants.UserInfoSaveMode.SEC_SAVE_CHECKOUT);
                    txtEditSAdd.setVisibility(View.INVISIBLE);
                    //btnProceed.setVisibility(View.GONE);
                }
                else {
                    setEnabled(false, deliveryCaption, txtSecondaryName, txtSecondaryMobNo, txtAddress2, spinTown2, txtTown2,  spinArea2, txtArea2, txtLandmark2, btnAction, AppConstants.UserInfoBtn.PROCEED, AppConstants.UserInfoSaveMode.CHECKOUT);
                    txtEditSAdd.setVisibility(View.VISIBLE);
                }
                addType = AppConstants.DeliveryAddressType.SECONDARY;
                txtEditPAdd.setVisibility(View.INVISIBLE);
                btnAction.setVisibility(View.VISIBLE);
            }
        }
    }

    private void setEnabled(Boolean status, TextView deliveryCaption, EditText txtName, EditText txtField, EditText txtAddress,
                            Spinner spinTown, TextView txtTown,Spinner spinArea, TextView txtArea, EditText txtLandmark, Button btnAction, String btnName, AppConstants.UserInfoSaveMode btnTag)
    {
        if (status) // is editable
        {
            deliveryCaption.setText("Please save the address to proceed further.");
            txtName.setEnabled(true);
            txtField.setEnabled(true);
            txtAddress.setEnabled(true);
            spinTown.setVisibility(View.VISIBLE);
            txtTown.setVisibility(View.GONE);
            spinArea.setVisibility(View.VISIBLE);
            txtArea.setVisibility(View.GONE);
            txtLandmark.setEnabled(true);
            btnAction.setVisibility(View.VISIBLE);
        }
        else // is not editable
        {
            deliveryCaption.setText("Please Select a delivery Address to Proceed Further.");
            txtName.setEnabled(false);
            txtField.setEnabled(false);
            txtAddress.setEnabled(false);
            spinTown.setVisibility(View.GONE);
            txtTown.setVisibility(View.VISIBLE);
            spinArea.setVisibility(View.GONE);
            txtArea.setVisibility(View.VISIBLE);
            txtLandmark.setEnabled(false);
            btnAction.setVisibility(View.GONE);
        }
        btnAction.setText(btnName);
        btnAction.setTag(btnTag);
    }/**/

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

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.txtEditPAdd:
            {
                initSetData();
                txtEditPAdd.setVisibility(View.INVISIBLE);
                setEnabled(true, deliveryCaption, txtName, txtEmail, txtAddress1, spinTown1, txtTown1,  spinArea1, txtArea1, txtLandmark1, btnAction, AppConstants.UserInfoBtn.SAVE, AppConstants.UserInfoSaveMode.PRIMARY);
                System.out.println("VISIBILITY : " + btnAddPri.getVisibility() + " - " + btnAddSec.getVisibility());
                if (btnAddPri.getVisibility() == View.GONE && btnAddSec.getVisibility() == View.GONE) {
                    ((LinearLayout) findViewById(R.id.layoutSec)).setVisibility(View.GONE);
                }
                else {
                    btnAction.setText(AppConstants.UserInfoBtn.SP);
                    btnAction.setTag(AppConstants.UserInfoSaveMode.PRI_SAVE_CHECKOUT);
                }
            }
            break;
            case R.id.txtEditSAdd:
            {
                initSetData();
                txtEditSAdd.setVisibility(View.INVISIBLE);
                setEnabled(true, deliveryCaption, txtSecondaryName, txtSecondaryMobNo, txtAddress2, spinTown2, txtTown2,  spinArea2, txtArea2, txtLandmark2, btnAction, AppConstants.UserInfoBtn.SAVE, AppConstants.UserInfoSaveMode.SECONDARY);
                System.out.println("VISIBILITY : " + btnAddPri.getVisibility() + " - " + btnAddSec.getVisibility());
                if (btnAddPri.getVisibility() == View.GONE && btnAddSec.getVisibility() == View.GONE) {
                    ((LinearLayout) findViewById(R.id.layoutPri)).setVisibility(View.GONE);
                }
                else {
                    btnAction.setText(AppConstants.UserInfoBtn.SP);
                    btnAction.setTag(AppConstants.UserInfoSaveMode.SEC_SAVE_CHECKOUT);
                }
            }
            break;
            case R.id.btnAction:
            {
                System.out.println("BUTTON TAG : " + btnAction.getTag().toString());
                String _name1 = txtName.getText().toString();
                String _email1 = txtEmail.getText().toString();
                String _landmark1 = txtLandmark1.getText().toString();
                String _add1 = txtAddress1.getText().toString();
                String _town1 = txtTown1.getText().toString();
                String _area1 = txtArea1.getText().toString();
                String _pinCode1 = txtPinCode1.getText().toString();
                String _city1 = txtCity1.getText().toString();
                String _delCharges1 = txtDel1.getText().toString();

                String _name2 = txtSecondaryName.getText().toString();
                String _mobNo2 = txtSecondaryMobNo.getText().toString();
                String _landmark2 = txtLandmark2.getText().toString();
                String _add2 = txtAddress2.getText().toString();
                String _town2 = txtTown2.getText().toString();
                String _area2 = txtArea2.getText().toString();
                String _pinCode2 = txtPinCode2.getText().toString();
                String _city2 = txtCity2.getText().toString();
                String _delCharges2 = txtDel2.getText().toString();

                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+\\.+[a-z]+";
                String emailPattern1 = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

                switch(AppConstants.UserInfoSaveMode.valueOf(btnAction.getTag().toString()))
                {
                    case PRIMARY:
                    case PRI_SAVE_CHECKOUT:
                    {
                        System.out.println("PRIMARY ADDRESS SAVE CLICKED");
                        // add field to validate
                        String[] data = { _name1, _email1, _add1, _landmark1,_area1};
                        // error message same as the data to validate
                        String[] error_data = {"Primary Name Field Can't be Null", "E-Mail Id can't be Null",
                                "Primary Address Fields Can't be Null", "LandMark Can't be Null", "Select a Valid Area"};

                        if (validateFields(data, error_data))
                        {
                            if(_email1.matches(emailPattern) || _email1.matches(emailPattern1))
                            {
                                jArrMain = new JSONArray();
                                try
                                {
                                    JSONArray jArr = new JSONArray();
                                    JSONObject jObj = new JSONObject();
                                    jObj.put("CUSTID", custId);
                                    jObj.put("NAME", _name1);
                                    jObj.put("EMAIL", _email1);
                                    jObj.put("ADDRESS", _add1); //+", "+txtArea1.getText().toString()+", "+txtLandmark1.getText().toString()+", "+txtPinCode1.getText().toString())
                                    jObj.put("TOWN", _town1);
                                    jObj.put("DISTRICT", LoggedUser.customer.getDistrict1());
                                    jObj.put("AREA", _area1);
                                    jObj.put("CITY", _city1);
                                    jObj.put("PINCODE", _pinCode1);
                                    jObj.put("DEL_CHARGES", _delCharges1);
                                    jObj.put("LANDMARK", _landmark1);
                                    jArr.put(jObj);


                                    /*CustomerDetailsModel customerDetailsModel = new CustomerDetailsModel();
                                    customerDetailsModel.setTown1(_town1);
                                    customerDetailsModel.setTown2(_town2);
                                    LoggedUser.init(customerDetailsModel);/**/

                                    JSONObject jObjMain = new JSONObject();
                                    jObjMain.put("PRIMARY_ADD", jArr);
                                    jArrMain.put(jObjMain);
                                    System.out.println("GET ARR "+jArr);
                                    System.out.println("GET MAIN ARR "+jArrMain);
                                    callApi(Apis.USER_INFO_URL, jArrMain, Apis.USER_INFO_CODE);
                                }
                                catch (JSONException e)
                                {
                                    e.printStackTrace();
                                }
                            }
                            else
                                Utils.showToast(this, "Please enter a valid email Id");
                        }
                    }
                    break;
                    case SECONDARY:
                    case SEC_SAVE_CHECKOUT:
                    {
                        System.out.println("SECONDARY ADDRESS SAVE CLICKED");
                        // add field to validate
                        String[] data = { _name2, _mobNo2, _add2, _landmark2, _area2};
                        // error message same as the data to validate
                        String[] error_data = {"Secondary Name Field Can't be Null", "Mobile No can't be Null",
                                "Secondary Address Fields Can't be Null", "LandMark Can't be Null", "Select a Valid Area"};
                        if (validateFields(data, error_data))
                        {
                            if (_mobNo2.length() == 10)
                            {
                                jArrMain = new JSONArray();
                                try
                                {
                                    JSONArray jArr = new JSONArray();
                                    JSONObject jObj = new JSONObject();
                                    jObj.put("CUSTID", LoggedUser.customer.getCustId());
                                    jObj.put("ALT_ID", LoggedUser.customer.getAltId());
                                    jObj.put("NAME", _name2);
                                    jObj.put("MOB_NO", _mobNo2);
                                    jObj.put("ADDRESS", _add2);  //+", "+txtArea2.getText().toString()+", "+txtLandmark2.getText().toString()+", "+txtPinCode2.getText().toString());
                                    jObj.put("TOWN", _town2);
                                    jObj.put("DISTRICT", LoggedUser.customer.getDistrict2());
                                    jObj.put("AREA", _area2);
                                    jObj.put("CITY", _city2);
                                    jObj.put("PINCODE", _pinCode2);
                                    jObj.put("DEL_CHARGES", _delCharges2);
                                    jObj.put("LANDMARK", _landmark2);
                                    jArr.put(jObj);

                                    JSONObject jObjMain = new JSONObject();
                                    jObjMain.put("SECONDARY_ADD", jArr);
                                    jArrMain.put(jObjMain);
                                    callApi(Apis.USER_INFO_URL, jArrMain, Apis.USER_INFO_CODE);
                                }
                                catch (JSONException e)
                                {
                                    e.printStackTrace();
                                }
                            }
                            else
                                Snackbar.make(btnAction, "INVALID MOBILE NUMBER.", Snackbar.LENGTH_LONG).show();
                        }
                    }
                    break;
                    case CHECKOUT:
                    {
                        System.out.println("CHECKOUT CLICKED");
                        callOrderOverView(addType);
                    }
                    break;
                }
            }
        }
    }

    @Override
    public void onRequestCompleted(String response, int requestCode) {
        try
        {
            if(!response.equals("[]"))
            {
                JSONArray jArr = new JSONArray(response.trim());
                switch(requestCode)
                {
                    case Apis.AREA_CODE:
                    {
                        if (jArr.length() > 0)
                        {
                            jArray = jArr;
                            System.out.println("ARR "+jArr.length());
                            townArr = new String[jArray.length()];
                            for(int i = 0; i < jArray.length(); i++)
                            {
                                townArr[i] = jArray.getJSONObject(i).getString("TOWN");
                            }

                            ArrayAdapter<String> spinTownAdapter = new ArrayAdapter<String>(this, R.layout.layout_dropdown, townArr); //selected item will look app_like a spinner set from XML
                            spinTown1.setAdapter(spinTownAdapter);
                            spinTown1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                                {
                                    txtTown1.setText(townArr[position]);
                                    // fetch AREA_DET LIST
                                    tempJArr = null;
                                    try {
                                        tempJArr = jArray.getJSONObject(position).getJSONArray("AREA_DET");

                                        // check for NON EMPTY AREA_DET LIST
                                        if (tempJArr.length() > 0)
                                        {
                                            cityArr = new String[tempJArr.length()];
                                            areaArr = new String[tempJArr.length()];
                                            pincodeArr = new String[tempJArr.length()];
                                            delChargesArr = new String[tempJArr.length()];
                                            districtArr = new String[tempJArr.length()];
                                            for(int j = 0; j < tempJArr.length(); j++)
                                            {
                                                cityArr[j] = tempJArr.getJSONObject(j).getString("CITY");
                                                areaArr[j] = tempJArr.getJSONObject(j).getString("AREA");
                                                pincodeArr[j] = tempJArr.getJSONObject(j).getString("PINCODE");
                                                delChargesArr[j] = tempJArr.getJSONObject(j).getString("DEL_CHARGES");
                                                districtArr[j] = tempJArr.getJSONObject(j).getString("ROUTE");
                                            }

                                            ArrayAdapter<String> spinAreaAdapter = new ArrayAdapter<String>(mContext, R.layout.layout_dropdown, areaArr); //selected item will look app_like a spinner set from XML
                                            spinArea1.setAdapter(spinAreaAdapter);
                                            spinArea1.setVisibility(View.VISIBLE);
                                            spinArea1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                @Override
                                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                                                {
                                                    txtPinCode1.setText(pincodeArr[position]);
                                                    txtArea1.setText(areaArr[position]);
                                                    txtCity1.setText(cityArr[position]);

                                                    del1 = delChargesArr[position];
                                                    dist1 = districtArr[position];

                                                    txtDel1.setText(del1);
                                                    /*SQLiteAdapter db = new SQLiteAdapter(mContext);
                                                    db.open();
                                                    CustomerDetailsModel custModel = new CustomerDetailsModel();
                                                    custModel.setPinCode1(pincodeArr[position]);
                                                    custModel.setArea1(areaArr[position]);
                                                    custModel.setCity1(cityArr[position]);
                                                    custModel.setDelCharges1(del1);
                                                    custModel.setDistrict1(dist1);

                                                    custModel.setDelCharges2("0");
                                                    custModel.setDistrict2("null");

                                                    db.close();
                                                    //LoggedUser.init(custModel);/**/
                                                    System.out.println("CHARGES 1 "+del1 );
                                                }

                                                @Override
                                                public void onNothingSelected(AdapterView<?> parent) {

                                                }
                                            });
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });


                            spinTown2.setAdapter(spinTownAdapter);
                            spinTown2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                                {
                                    txtTown2.setText(townArr[position]);
                                    // fetch AREA_DET LIST
                                    tempJArr = null;
                                    try {
                                        tempJArr = jArray.getJSONObject(position).getJSONArray("AREA_DET");

                                        // check for NON EMPTY AREA_DET LIST
                                        if (tempJArr.length() > 0)
                                        {
                                            cityArr = new String[tempJArr.length()];
                                            areaArr = new String[tempJArr.length()];
                                            pincodeArr = new String[tempJArr.length()];
                                            delChargesArr = new String[tempJArr.length()];
                                            districtArr = new String[tempJArr.length()];
                                            for(int j = 0; j < tempJArr.length(); j++)
                                            {
                                                cityArr[j] = tempJArr.getJSONObject(j).getString("CITY");
                                                areaArr[j] = tempJArr.getJSONObject(j).getString("AREA");
                                                pincodeArr[j] = tempJArr.getJSONObject(j).getString("PINCODE");
                                                delChargesArr[j] = tempJArr.getJSONObject(j).getString("DEL_CHARGES");
                                                districtArr[j] = tempJArr.getJSONObject(j).getString("ROUTE");
                                            }

                                            ArrayAdapter<String> spinAreaAdapter = new ArrayAdapter<String>(mContext, R.layout.layout_dropdown, areaArr); //selected item will look app_like a spinner set from XML
                                            spinArea2.setAdapter(spinAreaAdapter);
                                            spinArea2.setVisibility(View.VISIBLE);
                                            spinArea2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                @Override
                                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                                                    txtPinCode2.setText(pincodeArr[position]);
                                                    txtArea2.setText(areaArr[position]);
                                                    txtCity2.setText(cityArr[position]);

                                                    del2 = delChargesArr[position];
                                                    dist2 = districtArr[position];

                                                    txtDel2.setText(del2);
                                                    /*SQLiteAdapter db = new SQLiteAdapter(mContext);
                                                    db.open();

                                                    CustomerDetailsModel custModel = new CustomerDetailsModel();
                                                    custModel.setPinCode1(pincodeArr[position]);
                                                    custModel.setArea1(areaArr[position]);
                                                    custModel.setCity1(cityArr[position]);

                                                    custModel.setDelCharges1(del1);
                                                    custModel.setDistrict1(dist1);

                                                    custModel.setDelCharges2(del2);
                                                    custModel.setDistrict2(dist2);
                                                    db.close();
                                                    //LoggedUser.init(custModel);/**/
                                                    System.out.println("CHARGES 2 "+del2 );

                                                }

                                                @Override
                                                public void onNothingSelected(AdapterView<?> parent) {

                                                }
                                            });
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });

                        }
                        else
                        {
                            String area1 = LoggedUser.customer.getArea1();
                            if (!area1.equals(""))
                                txtArea1.setText(area1);
                            else
                                txtArea1.setText("NO AREA TO FETCH. TRY AGAIN !!!");
                            spinArea1.setVisibility(View.GONE);
                            txtArea1.setVisibility(View.VISIBLE);
                        }
                        initData();
                        mLoading.dismiss();
                    }
                    break;
                    case Apis.USER_INFO_CODE:
                    {
                        AppConstants.UserInfoSaveMode BTN_MODE = AppConstants.UserInfoSaveMode.valueOf(btnAction.getTag().toString());
                        if (jArr.length() > 0)
                        {
                            if (jArr.getJSONObject(0).getBoolean("STATUS"))
                            {
                                db = new SQLiteAdapter(mContext);
                                db.open();
                                if (jArr.getJSONObject(0).has("CUSTID"))
                                {
                                    System.out.println("DATA 1 "+jArrMain.getJSONObject(0).getJSONArray("PRIMARY_ADD"));
                                    if(db.updatePrimaryAdd(jArrMain.getJSONObject(0).getJSONArray("PRIMARY_ADD")))
                                    {
                                        LoggedUser.customer = db.user();
                                        LoggedUser.customer.setDelCharges1(del1);
                                        System.out.println("DEL 1 "+LoggedUser.customer.getDelCharges1());
                                        System.out.println("DEL 1 "+del1);

                                        switch(BTN_MODE)
                                        {
                                            case PRIMARY:
                                            {
                                                setEnabled(false, deliveryCaption, txtName, txtEmail, txtAddress1, spinTown1, txtTown1,  spinArea1, txtArea1, txtLandmark1, btnAction, AppConstants.UserInfoBtn.SAVE, AppConstants.UserInfoSaveMode.NONE);
                                                btnAction.setVisibility(View.GONE);
                                                txtEditPAdd.setVisibility(View.VISIBLE);
                                                ((LinearLayout) findViewById(R.id.layoutSec)).setVisibility(View.VISIBLE);
                                                if (exit)
                                                {
                                                    Bundle bun = new Bundle();
                                                    bun.putString("AddType",AppConstants.DeliveryAddressType.PRIMARY.name());
                                                    bun.putString("DateString",getIntent().getExtras().getString("DateString"));
                                                    bun.putString("TimeString",getIntent().getExtras().getString("TimeString"));
                                                    Intent OrderIntent = new Intent(UserInfoActivity.this, OrderOverviewActivity.class).putExtras(bun);
                                                    startActivity(OrderIntent);
                                                    finish();
                                                }
                                                Snackbar.make(btnAction, "Primary Address Successfully Updated.", Snackbar.LENGTH_LONG).show();
                                            }
                                            break;
                                            case PRI_SAVE_CHECKOUT:
                                            {
                                                db.close();
                                                callOrderOverView(addType);
                                            }
                                            break;
                                        }
                                    }
                                    else
                                        Snackbar.make(btnAction, "Primary Address Updation Failed.", Snackbar.LENGTH_LONG).show();
                                }
                                else if (jArr.getJSONObject(0).has("ALT_ID"))
                                {
                                    System.out.println("DATA 2 "+jArrMain.getJSONObject(0).getJSONArray("SECONDARY_ADD"));
                                    if(db.updateSecondaryAdd(jArrMain.getJSONObject(0).getJSONArray("SECONDARY_ADD"), jArr.getJSONObject(0).getString("ALT_ID"))) {
                                        LoggedUser.customer = db.user();
                                        LoggedUser.customer.setDelCharges2(del2);
                                        System.out.println("DEL 2 "+LoggedUser.customer.getDelCharges2());
                                        System.out.println("DEL 2 "+del2);

                                        switch(BTN_MODE)
                                        {
                                            case SECONDARY:
                                            {
                                                setEnabled(false, deliveryCaption, txtSecondaryName, txtSecondaryMobNo, txtAddress2, spinTown2, txtTown2,  spinArea2, txtArea2, txtLandmark2, btnAction, AppConstants.UserInfoBtn.SAVE, AppConstants.UserInfoSaveMode.NONE);
                                                btnAction.setVisibility(View.GONE);
                                                txtEditSAdd.setVisibility(View.VISIBLE);
                                                ((LinearLayout) findViewById(R.id.layoutPri)).setVisibility(View.VISIBLE);
                                                if (exit)
                                                {
                                                    Bundle bun = new Bundle();
                                                    bun.putString("AddType",AppConstants.DeliveryAddressType.SECONDARY.name());
                                                    bun.putString("DateString",getIntent().getExtras().getString("DateString"));
                                                    bun.putString("TimeString",getIntent().getExtras().getString("TimeString"));
                                                    Intent OrderIntent = new Intent(UserInfoActivity.this, OrderOverviewActivity.class).putExtras(bun);
                                                    startActivity(OrderIntent);
                                                    finish();
                                                }
                                                Snackbar.make(btnAction, "Secondary Address Successfully Updated.", Snackbar.LENGTH_LONG).show();
                                            }
                                            break;
                                            case SEC_SAVE_CHECKOUT:
                                            {
                                                db.close();
                                                callOrderOverView(addType);
                                            }
                                            break;
                                        }
                                    }
                                    else
                                        Snackbar.make(btnAction, "Secondary Address Updation Failed.", Snackbar.LENGTH_LONG).show();
                                }
                                db.close();
                            }
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
        initSetData();
    }

    private void callOrderOverView(AppConstants.DeliveryAddressType addType)
    {
        Bundle bn = new Bundle();
        bn.putString("ADD_TYPE", addType.name());
        Intent intent = new Intent(this, DeliverySpecsActivity.class).putExtras(bn);
        startActivityForResult(intent, 0);
    }

    private boolean validateFields(String[] data, String[] error_data) {
        int total = data.length;
        for (int i = 0; i < total; i++) {
            if (data[i].length() == 0) {
                Snackbar.make(btnAction, error_data[i], Snackbar.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    private void callApi(String _requestUrl, JSONArray _jArr, int _requestCode)
    {
        //Utils.LogBook(Utils.LogType.INFO, TAG, "1_ " + _requestUrl);
        mAsyncHttpRequest = new AsyncHttpRequest(mContext, _requestUrl, _jArr, _requestCode, AsyncHttpRequest.Type.POST);
        mAsyncHttpRequest.setRequestListener(this);
        mAsyncHttpRequest.execute();
    }
}



