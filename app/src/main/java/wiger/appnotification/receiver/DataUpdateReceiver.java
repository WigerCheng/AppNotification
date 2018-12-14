package wiger.appnotification.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import wiger.appnotification.Constant;
import wiger.appnotification.activity.HomeActivity;

public class DataUpdateReceiver extends BroadcastReceiver {

    private static final String TAG = DataUpdateReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: "+Constant.ACTION_DATA_CHANGE);
        HomeActivity activity = (HomeActivity) context;
        activity.updateData();
    }
}
