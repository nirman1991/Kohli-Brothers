package in.co.retail.kohlibrothers.Utility;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by Niha on 1/11/2016.
 */
public class Loading extends ProgressDialog
{
    public int reqCode = 0;
    String message;

    public Loading(Context context, String message, int requesCode) {
        super(context);
        reqCode = requesCode;
        setProgressStyle(ProgressDialog.STYLE_SPINNER);
        setCancelable(true);
        setCanceledOnTouchOutside(false);
        setMessage(message);
    }

    public Loading(Context context, int requesCode) {
        super(context);
        reqCode = requesCode;
        setProgressStyle(ProgressDialog.STYLE_SPINNER);
        setCancelable(true);
        setCanceledOnTouchOutside(false);
        setMessage("Loading...");
    }

    public Loading(Context context) {
        super(context);
        setProgressStyle(ProgressDialog.STYLE_SPINNER);
        setCancelable(true);
        setCanceledOnTouchOutside(false);
        setMessage("Loading...");
    }
}
