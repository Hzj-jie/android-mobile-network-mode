package org.gemini.mobile_network_mode;

import android.app.Activity;
import android.os.Bundle;
import android.os.Process;
import android.net.Uri;
import android.telephony.TelephonyManager;
import android.util.Log;
import java.lang.reflect.Method;

public class MobileNetworkActivity extends Activity
{
    private static final String TAG = "Gemini-MobileNetwork";

    @Override
    protected final void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        execute();
        finish();
    }

    @Override
    protected final void onDestroy()
    {
        super.onDestroy();
        Process.killProcess(Process.myPid());
        System.exit(0);
    }

    // Uri format = #1.
    protected int networkType()
    {
        if (getIntent() == null)
        {
            Log.e(TAG, "No intent attached.");
            return -1;
        }

        Uri uri = getIntent().getData();
        if (uri == null)
        {
            Log.e(TAG, "No uri attached.");
            return -1;
        }

        try
        {
            return Integer.parseInt(uri.getFragment());
        }
        catch (Exception ex)
        {
            return -1;
        }
    }

    protected int[] networkTypes()
    {
        return new int[] { networkType() };
    }

    private void execute()
    {
        int[] types = networkTypes();
        if (types == null || types.length == 0)
        {
            Log.e(TAG, "No valid network type received.");
            return;
        }

        boolean r = execute(0, types) ||
                    execute(1, types);
        if (r)
        {
            Log.w(TAG, "Done.");
        }
        else
        {
            Log.e(TAG, "Failed: " +
                "None of the modes can be applied to the system.");
        }
    }

    private boolean execute(int method, int[] types)
    {
        try
        {
            Log.w(TAG, "Current type: " +
                (Integer)(method == 0 ? report1() : report2()));
        }
        catch (Exception ex)
        {
            Log.e(TAG, "Failed to retrieve current type: " + ex.toString());
        }

        for (final int type : types)
        {
            try
            {
                boolean result =
                    (Boolean)(method == 0 ? execute1(type) : execute2(type));
                Log.w(TAG, "Result of applying " + type + ": " + result);
                if (result)
                {
                    return true;
                }
            }
            catch (Exception ex)
            {
                Log.e(TAG, "Failed to execute command: " + ex.toString());
                return false;
            }
        }
        return false;
    }

    private Object report1() throws Exception
    {
        Method method = TelephonyManager.class.getMethod(
            "getPreferredNetworkType", int.class);
        method.setAccessible(true);
        return method.invoke(getManager(), getSubId());
    }

    private Object report2() throws Exception
    {
        Method method = TelephonyManager.class.getMethod(
            "getPreferredNetworkType");
        method.setAccessible(true);
        return method.invoke(getManager());
    }

    private Object execute1(int type) throws Exception
    {
        Method method = TelephonyManager.class.getMethod(
            "setPreferredNetworkType", int.class, int.class);
        method.setAccessible(true);
        return method.invoke(getManager(), getSubId(), type);
    }

    private Object execute2(int type) throws Exception
    {
        Method method = TelephonyManager.class.getMethod(
                "setPreferredNetworkType", int.class);
        method.setAccessible(true);
        return method.invoke(getManager(), type);
    }

    private TelephonyManager getManager()
    {
        return (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
    }

    private int getSubId()
    {
        try
        {
            Method method =
                TelephonyManager.class.getDeclaredMethod("getSubId");
            return (Integer)method.invoke(getManager());
        }
        catch (Exception ex)
        {
            Log.e(TAG, "Failed to get sub id: " + ex.toString());
            return Integer.MAX_VALUE;
        }
    }
}
