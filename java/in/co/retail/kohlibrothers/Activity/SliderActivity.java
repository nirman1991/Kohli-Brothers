package in.co.retail.kohlibrothers.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;

import in.co.retail.applibrary.imageslider.Animations.DescriptionAnimation;
import in.co.retail.applibrary.imageslider.Layout.SliderLayout;
import in.co.retail.applibrary.imageslider.SliderTypes.BaseSliderView;
import in.co.retail.applibrary.imageslider.SliderTypes.TextSliderInfoView;
import in.co.retail.applibrary.imageslider.SliderTypes.TextSliderView;
import in.co.retail.kohlibrothers.R;

/**
 * Created by nirman on 7/7/2016.
 */
public class SliderActivity extends AppCompatActivity
{
    private static String TAG = SliderActivity.class.getSimpleName();
    private Context mContext;
    private SliderLayout mSliderLayout;
    private TextView mInfoTxt;
    private Button mBtnSkip;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slider);
        System.out.println("TAG : " + TAG);
        this.mContext = this;

        mInfoTxt = (TextView)findViewById(R.id.txtInfo);
        mSliderLayout = (SliderLayout)findViewById(R.id.mainSlider);



        HashMap<String,Integer> file_maps = new HashMap<String, Integer>();
        file_maps.put("GATHER ALL DETAILS", R.drawable.overflowmenu);
        file_maps.put("AN UNCANNY PRODUCT DISPLAY", R.drawable.singleproduct);
        file_maps.put("YOUR ORDERS ARE ALWAYS WITH US", R.drawable.trackorders);
        file_maps.put("LISTINGS WITH ADD TO CART OPTIONS", R.drawable.productlist);
        file_maps.put("ONE TIME REGISTRATION", R.drawable.authorize);
        TextSliderInfoView textSliderView = null;
        for(String name : file_maps.keySet())
        {
            System.out.println("NAME : " + name);
            textSliderView = new TextSliderInfoView(this);
            // initialize a SliderLayout
            textSliderView.description(name).image(file_maps.get(name)).setScaleType(BaseSliderView.ScaleType.Fit);

            // add your extra information
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle().putString("extra",name);

            mSliderLayout.addSlider(textSliderView);
        }
        mSliderLayout.clearDisappearingChildren();
        mSliderLayout.setPresetTransformer(SliderLayout.Transformer.ZoomOutSlide);
        mSliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mSliderLayout.setCustomAnimation(new DescriptionAnimation());


        mBtnSkip = (Button)findViewById(R.id.btnSkip);
        mBtnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentAuth = new Intent(SliderActivity.this, AuthorizeActivity.class);
                startActivity(intentAuth);
            }
        });
    }
}
