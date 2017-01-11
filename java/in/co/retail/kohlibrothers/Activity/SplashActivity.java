package in.co.retail.kohlibrothers.Activity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;

import in.co.retail.kohlibrothers.DBAdapter.SQLiteAdapter;
import in.co.retail.kohlibrothers.Model.LoggedUser;
import in.co.retail.kohlibrothers.R;

public class SplashActivity extends AppCompatActivity
{
    ImageView mImgView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        /*mImgView = (ImageView)findViewById(R.id.img_Logo);
        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(mImgView, "alpha",  1f, .3f);
        fadeOut.setDuration(2000);
        fadeOut.start();
        //ObjectAnimator animation1 = ObjectAnimator.ofFloat(mImgView, "rotation", 360);
        //animation1.setDuration(2000);
        //animation1.start();/**/

        new IntentLauncher().start();
    }

    private class IntentLauncher extends Thread {
        @Override
        /**
         *
         *
         * Sleep for some time and than start new activity.
         */
        public void run()
        {
            try
            {
                // Sleeping
                Thread.sleep(3 * 1000);
                checkUser();

                if (LoggedUser.customer == null)
                {
                    System.out.println("AUTH");
                    Intent intentAuthorize = new Intent(SplashActivity.this, SliderActivity.class);
                    startActivity(intentAuthorize);
                    finish();
                }
                else
                {
                    Intent intentHome = new Intent(SplashActivity.this, HomeActivity.class);
                    startActivity(intentHome);
                    finish();
                }
            }
            catch (Exception e)
            {
                System.out.println("Exception : " + e.getMessage());
            }
        }
    }

    private void checkUser()
    {
        try
        {
            SQLiteAdapter db = new SQLiteAdapter(this);
            db.open();
            LoggedUser.customer = db.user();
            db.close();
        }
        catch (Exception e)
        {
            // TODO: handle exception
        }
    }
}
