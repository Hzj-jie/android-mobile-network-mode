package org.gemini.mobile_network_mode;

import android.app.Activity;
import android.os.Bundle;
import android.os.Process;
import android.net.Uri;
import android.util.Log;
import java.util.Arrays;
import org.gemini.shared.TelephonyState;

public class MobileNetworkActivity extends Activity {
  private static final String TAG = "Gemini.MobileNetwork";
  private TelephonyState state;

  @Override
  protected final void onCreate(Bundle bundle) {
    super.onCreate(bundle);
    execute();
    finish();
  }

  @Override
  protected final void onDestroy() {
    super.onDestroy();
    Process.killProcess(Process.myPid());
    System.exit(0);
  }

  // Uri format = #1.
  protected int networkType() {
    if (getIntent() == null) {
      Log.e(TAG, "No intent attached.");
      return -1;
    }

    Uri uri = getIntent().getData();
    if (uri == null) {
      Log.e(TAG, "No uri attached.");
      return -1;
    }

    try {
      return Integer.parseInt(uri.getFragment());
    }
    catch (Exception ex) {
      return -1;
    }
  }

  protected int[] networkTypes() {
    return new int[] { networkType() };
  }

  private void execute() {
    state = new TelephonyState(this);
    int[] types = networkTypes();
    if (types == null || types.length == 0) {
      Log.e(TAG, "No valid network type received.");
      return;
    }

    if (execute(types)) {
      Log.w(TAG,
          "Succeeded setting mobile network type to " + Arrays.toString(types));
    } else {
      Log.e(TAG,
          "Failed to set mobile network type to " + Arrays.toString(types));
    }
  }

  private boolean execute(int[] types)
  {
    Log.w(TAG, "Current type: " + state.preferredNetworkType());
    for (int type : types) {
      if (state.setPreferredNetworkType(type)) {
        return true;
      }
    }
    return false;
  }
}
