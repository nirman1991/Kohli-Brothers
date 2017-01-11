package in.co.retail.kohlibrothers.Utility;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

public class Utils
{
	public enum LogType
	{
		DEBUG, ERROR, INFO, VERBOSE, WARN
		/*DEBUG('D'), ERROR('E'), INFO('I'), VERBOSE('V'), WARN('W');
		
		private char statusCode;
 
		private LogType(char s)
		{
			statusCode = s;
		}
 
		public char getStatusCode()
		{
			return statusCode;
		}/**/
	};
	
	public static void LogBook(LogType log_type, String _tag, String _msg)
	{
		/*char ch = log_type.getStatusCode();
		switch(ch)
		{
		case 'D':
			break;
		case 'E':
			break;
		case 'I':
			break;
		case 'V':
			break;
		case 'W':
			break;
		default:
			break;
		}/**/
		switch(log_type)
		{
		case DEBUG:
			Log.d("LOG BOOK DEBUG MSG - " + _tag, _msg); //blue
			break;
		case ERROR:
			Log.e("LOG BOOK ERROR MSG - " + _tag, _msg); //red
			break;
		case INFO:
			Log.i("LOG BOOK INFO MSG - " + _tag, _msg); //green
			break;
		case VERBOSE:
			Log.v("LOG BOOK VERBOSE MSG - " + _tag, _msg); //black
			break;
		case WARN:
			Log.w("LOG BOOK WARN MSG - " + _tag, _msg); //orange
			break;
		default:
			//Log.d("LOG BOOK DEFAULT MESSAGE", msg);
			break;
		}
	}
	
	public static void showToast(Context _context, String _msg)
	{
		Toast toast = Toast.makeText(_context, _msg, Toast.LENGTH_SHORT);
		toast.show();
		//toast.setGravity(Gravity.CLIP_HORIZONTAL | Gravity.CENTER_HORIZONTAL, 0, 0);
	}
	
	public static boolean isConnectedToInternet(Context _context)
	{
		ConnectivityManager connectivity = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null)
		{
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null)
				for (int i = 0; i < info.length; i++)
					if (info[i].getState() == NetworkInfo.State.CONNECTED)
					{
						return true;
					}
		}
		return false;
	}

	/*public static void getRetryPage(Context _context, Exception _exception)
	{
		// TODO Auto-generated method stub
		RetryPage mRetry = new RetryPage(_context, _exception);
		mRetry.setRetryPageClickListener((RetryPageListener) _context);
		mRetry.show();
	}/**/
	
	public static void getDialogBox(Context _context, int _icon, String _title, String _msg, int _event)
	{
		/*RequestDialogBox mRequestDialogBox = new RequestDialogBox(_context, _icon, _title, _msg, _event);
		mRequestDialogBox.setBtn(BtnType.POSITIVE, "Yes");
		mRequestDialogBox.setBtn(BtnType.NEGATIVE, "No");
		mRequestDialogBox.setDialogBoxClickListener((DialogBoxClickListener) _context);
		mRequestDialogBox.show();/**/
	}	
}
	
/*private void showPrompt(Context _context, int _alertIcon, String _title, String _msg)
{
	AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(_context);
	
	alertDialogBuilder
	.setIcon(_alertIcon)// set icon
	.setTitle(_title)// set title
	.setMessage(_msg)// set dialog message
	.setCancelable(false)
	.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog,int id) {
			// if this button is clicked, close
			// current activity
			//initiatepopupTranStatus(1);
			Utils.LogBook(LogType.INFO, TAG, "EXITING...");
			MainActivity.this.finish();
		}
	})
	.setNegativeButton("No",new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog,int id) {
			// if this button is clicked, just close
			// the dialog box and do nothing
			dialog.cancel();
		}
	});

	// create alert dialog
	AlertDialog alertDialog = alertDialogBuilder.create();
	
	// show it
	alertDialog.show();
}/**/

/*public static void requestRetryPage(Context _context, Exception _exception)
{
	// TODO Auto-generated method stub		
	Bundle bn = new Bundle();
	bn.putString("VALUE", _exception.getMessage());
	_context.startActivity(new Intent(_context, RetryPage.class).putExtras(bn));
	
}/**/

	/*public static String getResult(NetworkOperation net)
	{
		String res = "";
		// TODO Auto-generated method stub
		try
		{
			res = net.get();
			//System.out.println("*****" + res + "*****");
		}
		catch (InterruptedException _exception) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		catch (ExecutionException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}
	
	/*public static ProgressDialog progressDialog(Context _context)
	{
		ProgressDialog pDialog = new ProgressDialog(_context);
		pDialog.setMessage("Loading. Please wait...");
		//pDialog.setTitle("WAIT");
        //pDialog.setIndeterminate(false);
        //pDialog.setMax(100);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setCancelable(true);
        pDialog.setCanceledOnTouchOutside(false);
        pDialog.show();
        return pDialog;
	}/**/
	
	/*public static boolean getAlert(Context _context, String _msg, int _draw, String _title)
	{
		boolean _alert_effect = showAlert(_context, _msg, _draw, _title);
		return _alert_effect;
	}
	
	@SuppressLint("NewApi")
	public static boolean showAlert(Context _context, String _msg, int _draw, String _title)
	{
		//final boolean _alert_effect = false;
		Builder alertDialog = new AlertDialog.Builder(_context);
		alertDialog.setMessage(_msg);
		alertDialog.setIcon(_draw);
		alertDialog.setTitle(_title);
		alertDialog.setPositiveButton("No", new DialogInterface.OnClickListener()
		{
			
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				// TODO Auto-generated method stub
				alert_effect = true;
			}
		});
		alertDialog.setNegativeButton("Yes", new DialogInterface.OnClickListener()
		{
			
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				// TODO Auto-generated method stub
				alert_effect = false;
			}
		});
		alertDialog.setCancelable(true);
		alertDialog.create().show();
		return alert_effect;
	}
	
	public static boolean getAlertEffect()
	{
		return alert_effect;
	}/**/
