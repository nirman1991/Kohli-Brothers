package in.co.retail.kohlibrothers.Controller;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import in.co.retail.kohlibrothers.R;

/**
 * Created by Niha on 10/20/2015.
 */
@SuppressWarnings("ALL")
public class ActionBarHelper
{
    public static void init(AppCompatActivity _appCompatActivity, Toolbar _tb, boolean _enableLogo, boolean _enableUp, String _heading,
                            boolean _enableTitle)
    {
        _appCompatActivity.setSupportActionBar(_tb);

        ActionBar ab = _appCompatActivity.getSupportActionBar();
        ab.setLogo(R.mipmap.ic_launcher); // app_logo to display
        ab.setDisplayUseLogoEnabled(_enableLogo); // true - will display app_logo, false - wont display app_logo
        ab.setDisplayHomeAsUpEnabled(_enableUp); // enables home icon to return to home page
        //ab.setDisplayShowHomeEnabled(_enableHome); // enables home button click event -- not working , boolean _enableHome

        ab.setTitle(_heading); // sets heading
        ab.setDisplayShowTitleEnabled(_enableTitle); // true - displays title; false - doesnt displays title
        //ab.setDisplayShowHomeEnabled(_enableHome); // true - enables and show the home icon, false - to diable and hide the home icon

    }

    /*public static void init(AppCompatActivity _appCompatActivity, int _tbId,  String _heading, boolean _enableTitle, boolean _enableLogo, boolean _enableHome, boolean _enableUp, boolean _enableCustomDisp)
    {
        Toolbar tb = (Toolbar) _appCompatActivity.findViewById(_tbId);
        _appCompatActivity.setSupportActionBar(tb);

        tb.setNavigationIcon(R.drawable.my_menu_icon); // navigation icon to show category
        tb.setLogo(R.drawable.app_header_logo); // app_logo to display
        tb.inflateMenu(R.menu.menu_home); // action menu

        ActionBar ab = _appCompatActivity.getSupportActionBar();

        ab.setTitle(_heading); // sets heading
        ab.setDisplayShowTitleEnabled(_enableTitle); // true - displays title; false - doesnt displays title
        ab.setDisplayHomeAsUpEnabled(_enableUp); // enables home icon to return to home page
    }/**/
}
