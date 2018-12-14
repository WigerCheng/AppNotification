package wiger.appnotification.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import wiger.appnotification.MyApplication;
import wiger.appnotification.R;
import wiger.appnotification.adapter.FlowRemindAdapter;
import wiger.appnotification.database.SqlHelper;
import wiger.appnotification.model.AppInfo;
import wiger.appnotification.model.Remind;
import wiger.appnotification.util.AppTool;

public class PollingService extends Service {
    public static final String TAG = PollingService.class.getSimpleName();

    private Handler mHandler = null;
    private static String LauncherPackageName = null;
    private boolean isHome = false;
    private SqlHelper sqlHelper;
    private ArrayList<Remind> reminds;
    private List<AppInfo> appInfos;

    //FlowWindow
    private boolean isAdded = false; // 是否已增加悬浮窗
    private static final int HANDLE_CHECK_ACTIVITY = 200;

    FrameLayout toucherLayout;
    LinearLayout remindsLayout;
    WindowManager.LayoutParams toucher_params, reminds_params;
    WindowManager windowManager;
    ImageButton imgbtn_appIcon;
    TextView tv_remindCount, tv_appName;
    RecyclerView rv_reminds;
    int height, width;

    //FLAG
    private static int FLAG;
    private static final int FLAG_START = 0;
    private static final int FLAG_HOME = 1;
    private static final int FLAG_OPENAPP = 2;
    private static final int FLAG_SWITCHAPP = 3;

    public PollingService() {
    }

    @SuppressLint("HandlerLeak")
    @Override
    public void onCreate() {
        super.onCreate();
        createWindowView();
        LauncherPackageName = AppTool.getLauncherPackageName(PollingService.this.getPackageManager());
        MyApplication application = (MyApplication) getApplication();
        sqlHelper = application.getSqlHelper();
        appInfos = application.getmLocalInstalledApp();
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case HANDLE_CHECK_ACTIVITY:
                        run();
                        break;
                }
                mHandler.sendEmptyMessage(HANDLE_CHECK_ACTIVITY);
            }
        };
        mHandler.sendEmptyMessage(HANDLE_CHECK_ACTIVITY);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void run() {
        reminds = sqlHelper.selectReminds();
        String topPackageName = AppTool.getTopAppPackageName(PollingService.this);
        isHome = LauncherPackageName.equals(topPackageName);
        if (isHome) {
            if (FLAG == FLAG_START) FLAG = FLAG_HOME;
            if (FLAG == FLAG_OPENAPP) FLAG = FLAG_SWITCHAPP;
        }
        if (FLAG == FLAG_HOME) {
            for (Remind remind : reminds) {
                if (remind.getAppPackageName().equals(topPackageName)) {
                    ArrayList<Remind> reminds = sqlHelper.selectRemind(remind.getAppPackageName());
                    if (!isAdded) {
                        AppInfo app = new AppInfo();
                        for (AppInfo appInfo : appInfos) {
                            if (appInfo.getPackageName().equals(topPackageName)) {
                                app = appInfo;
                            }
                        }
                        updateView(app, reminds);
                        toucherLayout.setVisibility(View.VISIBLE);
                        Toast.makeText(this, "单击悬浮窗可以显示或隐藏提醒框，移到屏幕右下角关闭悬浮窗", Toast.LENGTH_SHORT).show();
                        FLAG = FLAG_OPENAPP;
                        isAdded = true;
                    }
                }
            }
        }
        if (FLAG == FLAG_SWITCHAPP) {
            //hideFloatWindow
            isAdded = false;
            toucherLayout.setVisibility(View.INVISIBLE);
            remindsLayout.setVisibility(View.INVISIBLE);
            FLAG = FLAG_START;
            Log.d(TAG, "run: switch");
        }
    }

    private void updateView(AppInfo app, ArrayList<Remind> reminds) {
        imgbtn_appIcon.setImageDrawable(app.getIcon());
        tv_remindCount.setText(String.valueOf(reminds.size()));
        tv_appName.setText(app.getAppName());
        FlowRemindAdapter flowRemindAdapter = new FlowRemindAdapter(reminds);
        rv_reminds.setAdapter(flowRemindAdapter);
        windowManager.updateViewLayout(remindsLayout, reminds_params);
        windowManager.updateViewLayout(toucherLayout, toucher_params);
    }

    private void createWindowView() {
        windowManager = (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = getResources().getDisplayMetrics();
        height = dm.heightPixels;
        width = dm.widthPixels;
        Log.i(TAG, "onCreate: height" + height);
        Log.i(TAG, "onCreate: width" + width);
        createReminds();
        createToucher();
    }

    private void createReminds() {
        //赋值WindowManager&LayoutParam.
        reminds_params = new WindowManager.LayoutParams();

        //设置type.系统提示型窗口，一般都在应用程序窗口之上.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            reminds_params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            reminds_params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
        //设置效果为背景透明.
        reminds_params.format = PixelFormat.RGBA_8888;
        //设置flags.不可聚焦及不可使用按钮对悬浮窗进行操控.
        reminds_params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        //设置窗口初始停靠位置.
        reminds_params.gravity = Gravity.START | Gravity.TOP;
        reminds_params.x = 0;
        reminds_params.y = 400;
        reminds_params.width = width;
        reminds_params.height = 400;
        LayoutInflater inflater = LayoutInflater.from(getApplication());
        remindsLayout = (LinearLayout) inflater.inflate(R.layout.flowwindow_reminds, null);
        tv_appName = remindsLayout.findViewById(R.id.tv_appName);
        rv_reminds = remindsLayout.findViewById(R.id.rv_reminds);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rv_reminds.setLayoutManager(layoutManager);
        remindsLayout.setVisibility(View.INVISIBLE);
        windowManager.addView(remindsLayout, reminds_params);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void createToucher() {
        //赋值WindowManager&LayoutParam.
        toucher_params = new WindowManager.LayoutParams();
        //设置type.系统提示型窗口，一般都在应用程序窗口之上.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            toucher_params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            toucher_params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
        //设置效果为背景透明.
        toucher_params.format = PixelFormat.RGBA_8888;
        //设置flags.不可聚焦及不可使用按钮对悬浮窗进行操控.
        toucher_params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        //设置窗口初始停靠位置.
        toucher_params.gravity = Gravity.START | Gravity.TOP;

        //设置悬浮窗口长宽数据.
        //注意，这里的width和height均使用px而非dp.这里我偷了个懒
        //如果你想完全对应布局设置，需要先获取到机器的dpi
        //px与dp的换算为px = dp * (dpi / 160).
        toucher_params.width = 150;
        toucher_params.height = 150;

        toucher_params.x = width - toucher_params.width;
        toucher_params.y = 0;
        LayoutInflater inflater = LayoutInflater.from(getApplication());
        //获取浮动窗口视图所在布局.
        toucherLayout = (FrameLayout) inflater.inflate(R.layout.flowwindow_toucher, null);

        //浮动窗口按钮.
        imgbtn_appIcon = toucherLayout.findViewById(R.id.imgbtn_appIcon);
        tv_remindCount = toucherLayout.findViewById(R.id.tv_remindCount);

        imgbtn_appIcon.setOnTouchListener(new View.OnTouchListener() {
            //保存悬浮框最后位置的变量
            int lastX, lastY;
            int paramX, paramY;
            int dx, dy;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                dx = (int) event.getRawX() - lastX;
                dy = (int) event.getRawY() - lastY;
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        lastX = (int) event.getRawX();
                        lastY = (int) event.getRawY();
                        paramX = toucher_params.x;
                        paramY = toucher_params.y;
                        break;

                    case MotionEvent.ACTION_MOVE:
                        toucher_params.x = paramX + dx;
                        toucher_params.y = paramY + dy;
                        // 更新悬浮窗位置
                        windowManager.updateViewLayout(toucherLayout, toucher_params);
                        break;
                    case MotionEvent.ACTION_UP:
                        //如果没动就显示提醒框
                        if (Math.abs(dx) <= 10 && Math.abs(dy) <= 10) {
                            if (remindsLayout.getVisibility() == View.VISIBLE) {
                                remindsLayout.setVisibility(View.INVISIBLE);
                            } else if (remindsLayout.getVisibility() == View.INVISIBLE) {
                                remindsLayout.setVisibility(View.VISIBLE);
                            }
                        } else {
                            //移到右下角关闭悬浮窗
                            Log.d(TAG, "onTouch: x" + event.getRawX());
                            Log.d(TAG, "onTouch: y" + event.getRawY());
                            Log.d(TAG, "onTouch: height" + height);
                            Log.d(TAG, "onTouch: l_height" + toucher_params.height);
                            if (Math.abs(height - event.getRawY()) < toucher_params.height / 2 && Math.abs(width - event.getRawX()) < toucher_params.width / 2) {
                                Toast.makeText(PollingService.this, "悬浮窗已关闭", Toast.LENGTH_SHORT).show();
                                if (remindsLayout.getVisibility() != View.INVISIBLE) {
                                    remindsLayout.setVisibility(View.INVISIBLE);
                                }
                                toucherLayout.setVisibility(View.INVISIBLE);
                                remindsLayout.setVisibility(View.INVISIBLE);
                                windowManager.removeView(toucherLayout);
                                windowManager.removeView(remindsLayout);
                                createWindowView();
                            }
                        }
                        break;
                }
                return true;
            }
        });
        toucherLayout.setVisibility(View.INVISIBLE);
        windowManager.addView(toucherLayout, toucher_params);
    }

    @Override
    public void onDestroy() {
        if (imgbtn_appIcon != null) {
            windowManager.removeView(toucherLayout);
            windowManager.removeView(remindsLayout);
        }
        super.onDestroy();
    }
}
