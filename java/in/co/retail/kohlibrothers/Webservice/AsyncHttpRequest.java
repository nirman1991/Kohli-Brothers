package in.co.retail.kohlibrothers.Webservice;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.UnknownHostException;

import in.co.retail.kohlibrothers.R;
import in.co.retail.kohlibrothers.Utility.Utils;


/**
 * Created by Nirman on 12/2/2015.
 */
public class AsyncHttpRequest extends AsyncTask<Void, Void, Void>
{
    public enum Type
    {
        POST, GET, POST_WITH_FILE
    };

    private static String TAG = AsyncHttpRequest.class.getSimpleName();
    DefaultHttpClient mHttpClient = null;
    HttpGet mGet = null;
    HttpPost mPost = null;
    String mResponse = null;
    Exception mError = null;
    String mUrl = "";
    Context mContext = null;
    Bundle mParams = null;
    JSONArray mJsonArr = null;
    int mRequestCode = -1;
    Type mType = Type.GET;
    boolean mRunning = false;

    RequestListener mRequestListener = null;

    public interface RequestListener
    {
        void onRequestCompleted(String response, int requestCode);

        void onRequestError(Exception e, int requestCode);

        void onRequestStarted(int requestCode);
    }

    /*public AsyncHttpRequest(Context context, String baseUrl, Bundle params, int requestCode, Type type)
    {
        this.mParams = params;
        this.mRequestCode = requestCode;
        this.mType = type;
        this.mUrl = baseUrl;
        this.mContext = context;
    }/**/

    public AsyncHttpRequest(Context context, String baseUrl, JSONArray _jArr, int requestCode, Type type)
    {
        this.mJsonArr = _jArr;
        this.mRequestCode = requestCode;
        this.mType = type;
        this.mUrl = baseUrl;
        this.mContext = context;
    }

    //public AsyncHttpRequest() { }

    public void setRequestListener(RequestListener listener)
    {
        if (listener != null)
            this.mRequestListener = listener;

    }

    public static boolean isConnected(final Context ctx)
    {
        ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnected();
    }

    @Override
    protected void onCancelled()
    {
        if (mType == Type.GET && mGet != null)
            mGet.abort();
        else if (mType == Type.POST && mPost != null)
            mPost.abort();
        mParams = null;
        mPost = null;
        mGet = null;
        mError = null;
        if (mRequestListener != null)
            mRequestListener.onRequestError(new Exception(mContext.getString(R.string.async_task_error)), mRequestCode);
        mRequestListener = null;
        mRunning = false;
        super.onCancelled();
    }

    public boolean isRunning()
    {
        return mRunning;
    }

    @Override
    protected Void doInBackground(Void... p)
    {
        if (AsyncHttpRequest.isConnected(mContext))
        {
            mRunning = true;
            try
            {
                if (mType == Type.POST)
                {
                    mResponse = doHttpPostRequest(mUrl, mJsonArr);
                }
                /*if (mType == Type.GET)
                    mResponse = doHttpGetRequest(mUrl, mParams);
                else if (mType == Type.POST)
                {
                    if (mParams == null)
                        mResponse = doHttpPostRequest(mUrl, mJsonArr);
                    else
                        mResponse = doHttpPostRequest(mUrl, mParams);
                }
                else if (mType == Type.POST_WITH_FILE)
                    mResponse = doHttpPostWithFileRequest(mUrl, mParams);/**/
                //Utils.log(mResponse);
                Utils.LogBook(Utils.LogType.INFO, TAG, mResponse);
            }
            catch (UnknownHostException e)
            {
                mError = new Exception("Connection to server failed.");
            }
            catch (Exception e)
            {
                e.printStackTrace();
                mError = e;
            }
        }
        else
        {
            mError = new Exception(mContext.getString(R.string.async_task_no_internet));
        }
        mHandler.sendEmptyMessage(1);
        return null;
    }

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(android.os.Message msg)
        {
            mRunning = false;
            if (mError != null && mRequestListener != null)
            {
                //System.out.println("Error Handler");
                mRequestListener.onRequestError(mError, mRequestCode);
            }
            else if (mResponse != null && mRequestListener != null)
            {
                mRequestListener.onRequestCompleted(mResponse, mRequestCode);
            }
        }
    };

    @Override
    protected void onPostExecute(Void result) { }

    /*private String doHttpGetRequest(String url, Bundle params) throws Exception
    {
        String response = null;
        mHttpClient = new DefaultHttpClient();
        if (params != null) {
            String paramsString = "";
            Set<String> parameters = params.keySet();
            Iterator<String> it = parameters.iterator();
            while (it.hasNext()) {
                String key = it.next().trim();
                if (params.containsKey(key)) {
                    if (key.startsWith("<") && key.endsWith(">"))
                        paramsString = paramsString + "&"
                                + key.replace("<", "").replace(">", "") + "="
                                + params.getString(key);
                    else
                        paramsString = paramsString
                                + "&"
                                + key
                                + "="
                                + URLEncoder.encode(params.getString(key),
                                "UTF-8");
                }
            }
            url = url + "?" + paramsString;
        }
        //Utils.log(url);
        Utils.LogBook(Utils.LogType.INFO, TAG, url);
        mGet = new HttpGet(url);
        HttpResponse resp = mHttpClient.execute(mGet);
        if (resp.getStatusLine().getStatusCode() != 200)
            throw new Exception(String.format((mContext
                    .getString(R.string.async_task_request)), resp
                    .getStatusLine().getStatusCode()));
        InputStream inStream = resp.getEntity().getContent();
        response = convertStreamToString(inStream);
        inStream.close();
        return response;
    }/**/

    @Override
    protected void onPreExecute()
    {
        if (mRequestListener != null)
            mRequestListener.onRequestStarted(mRequestCode);
        super.onPreExecute();
    }

    /*private String doHttpPostRequest(String url, Bundle params) throws Exception
    {
        String response = null;
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        if (params != null)
        {
            Set<String> parameters = params.keySet();
            Iterator<String> it = parameters.iterator();
            while (it.hasNext())
            {
                String key = it.next();
                if (params.containsKey(key))
                {
                    //Utils.log(key + ":" + params.getString(key));
                    Utils.LogBook(Utils.LogType.INFO, TAG, key + ":" + params.getString(key));
                    nameValuePairs.add(new BasicNameValuePair(key, params
                            .getString(key)));
                }
            }
        }
        mHttpClient = new DefaultHttpClient();
        //Utils.log(url);
        Utils.LogBook(Utils.LogType.INFO, TAG, url);
        mPost = new HttpPost(url);
        mPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        HttpResponse resp = mHttpClient.execute(mPost);
        if (resp.getStatusLine().getStatusCode() != 200)
            throw new Exception(String.format((mContext
                    .getString(R.string.async_task_request)), resp
                    .getStatusLine().getStatusCode()));
        InputStream inStream = resp.getEntity().getContent();
        response = convertStreamToString(inStream);
        inStream.close();
        return response;
    }/**/

    private String doHttpPostRequest(String _url, JSONArray _jArr) throws Exception
    {
        String response = null;
        JSONArray m_jArr = _jArr;

        mHttpClient = new DefaultHttpClient();
        Utils.LogBook(Utils.LogType.INFO, TAG, _url);
        mPost = new HttpPost(_url);
        mPost.setHeader(HTTP.CONTENT_TYPE, "application/json");

        StringEntity se = new StringEntity(m_jArr.toString());
        mPost.setEntity(se);

        HttpResponse resp = mHttpClient.execute(mPost);
        int requestCode = resp.getStatusLine().getStatusCode();
        //System.out.println("REQUEST CODE : " + requestCode);
        //System.out.println("REQUEST CODE : " + resp.getStatusLine().getReasonPhrase());
        //if (resp.getStatusLine().getStatusCode() != 200)
        if (requestCode != 200 && requestCode != 500)
            throw new Exception(String.format((mContext
                    .getString(R.string.async_task_request)), resp
                    .getStatusLine().getStatusCode()));
        InputStream inStream = resp.getEntity().getContent();
        response = convertStreamToString(inStream);
        inStream.close();
        return response;
    }

    /*private String doHttpPostWithFileRequest(String url, Bundle params) throws Exception
    {
        String response = null;
        MultipartEntity reqEntity = new MultipartEntity(
                HttpMultipartMode.BROWSER_COMPATIBLE);

        if (params != null)
        {
            Set<String> parameters = params.keySet();
            Iterator<String> it = parameters.iterator();
            FormBodyPart bodyPart;
            while (it.hasNext())
            {
                String key = it.next();
                if (params.containsKey(key))
                {
                    if (key.equals("uploadedFile"))
                    {
                        try
                        {
                            Bitmap bm = BitmapFactory.decodeFile(params.getString(key));
                            ByteArrayOutputStream bos = new ByteArrayOutputStream();
                            bm.compress(Bitmap.CompressFormat.PNG, 100, bos);
                            byte[] data = bos.toByteArray();
                            ByteArrayBody bab = new ByteArrayBody(data, key + ".jpg");
                            reqEntity.addPart(key, bab);
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                    else if (key.equals("profilePic") && !params.getString(key).equals(""))
                    {
                        Bitmap bm = BitmapFactory.decodeFile(params.getString(key));
                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        bm.compress(Bitmap.CompressFormat.JPEG, 75, bos);
                        byte[] data = bos.toByteArray();
                        ByteArrayBody bab = new ByteArrayBody(data, key	+ ".jpg");
                        reqEntity.addPart(key, bab);
                    }
                    else
                    {
                        //Utils.log(key + ":" + params.getString(key));
                        Utils.LogBook(Utils.LogType.INFO, TAG, key + ":" + params.getString(key));
                        bodyPart = new FormBodyPart(key, new StringBody(
                                params.getString(key)));
                        reqEntity.addPart(bodyPart);
                    }
                }
            }
        }
        mHttpClient = new DefaultHttpClient();
        //Utils.log(url);
        Utils.LogBook(Utils.LogType.INFO, TAG, url);
        mPost = new HttpPost(url);
        mPost.setEntity(reqEntity);
        HttpResponse resp = mHttpClient.execute(mPost);
        if (resp.getStatusLine().getStatusCode() != 200)
            throw new Exception(String.format((mContext
                    .getString(R.string.async_task_request)), resp
                    .getStatusLine().getStatusCode()));
        InputStream inStream = resp.getEntity().getContent();
        response = convertStreamToString(inStream);
        inStream.close();
        return response;
    }/**/

    private String convertStreamToString(InputStream instream)
    {
        String response = "";
        BufferedReader reader = new BufferedReader(new InputStreamReader(instream));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try
        {
            while ((line = reader.readLine()) != null)
            {
                sb.append(line + "\n");
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                instream.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
        response = sb.toString();
        return response.trim();
    }/**/
}