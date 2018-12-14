package wiger.appnotification.activity;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import wiger.appnotification.Constant;
import wiger.appnotification.MyApplication;
import wiger.appnotification.R;
import wiger.appnotification.adapter.RemindAdapter;
import wiger.appnotification.database.SqlHelper;
import wiger.appnotification.dialog.PermissionDialog;
import wiger.appnotification.dialog.RemindDialog;
import wiger.appnotification.model.AppInfo;
import wiger.appnotification.model.Remind;
import wiger.appnotification.receiver.DataUpdateReceiver;
import wiger.appnotification.service.PollingService;

public class HomeActivity extends AppCompatActivity implements RemindDialog.RemindDialogListener, PermissionDialog.PermissionDialogListener {
    private ArrayList<AppInfo> appInfos = new ArrayList<>();//Group
    private ArrayList<ArrayList<Remind>> remindArrayList = new ArrayList<>();//Item
    private List<AppInfo> mLocalInstalledApp;
    private Context context;
    private SqlHelper sqlHelper;
    private DataUpdateReceiver receiver;
    public RemindAdapter remindAdapter;
    private ExpandableListView exlv_remind;
    private Intent intent_Service;
    private PermissionDialog permissionDialog;

    private static final String TAG = HomeActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home2);
        context = HomeActivity.this;
        MyApplication application = (MyApplication) getApplication();
        sqlHelper = application.getSqlHelper();
        mLocalInstalledApp = application.getmLocalInstalledApp();
        intent_Service = new Intent(this, PollingService.class);
        initData();
        initView();
    }

    private void getPermission() {
        if (!Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            if (!hasPermission()) {
                Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!hasPermission() || !Settings.canDrawOverlays(this)) {
            permissionDialog.show(getSupportFragmentManager(), "PermissionDialog");
        }
        if (hasPermission() && Settings.canDrawOverlays(this)) {
            Toast.makeText(this, "服务启动", Toast.LENGTH_SHORT).show();
            startService(intent_Service);
        }
    }

    public void initData() {
        ArrayList<Remind> reminds = sqlHelper.selectReminds();
        for (AppInfo appInfo : mLocalInstalledApp) {
            ArrayList<Remind> temps = new ArrayList<>();
            boolean hasRemind = false;
            for (Remind remind : reminds) {
                if (remind.getAppPackageName().equals(appInfo.getPackageName())) {
                    if (!hasRemind) {
                        appInfos.add(appInfo);
                        hasRemind = true;
                    }
                    temps.add(remind);
                }
            }
            if (hasRemind) remindArrayList.add(temps);
        }
    }

    private void initView() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        permissionDialog = new PermissionDialog();
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, MainActivity.class));
            }
        });

        exlv_remind = findViewById(R.id.exlv_remind);
        remindAdapter = new RemindAdapter(appInfos, remindArrayList, context, sqlHelper);
        exlv_remind.setAdapter(remindAdapter);
        int groupCount = exlv_remind.getCount();
        for (int i = 0; i < groupCount; i++) {
            exlv_remind.expandGroup(i);
        }
        exlv_remind.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Toast.makeText(context, remindArrayList.get(groupPosition).get(childPosition).getMsg(), Toast.LENGTH_SHORT).show();
                Bundle bundle = new Bundle();
                bundle.putSerializable("UpdateRemind", remindArrayList.get(groupPosition).get(childPosition));
                bundle.putInt("Mode", Constant.MODE_UPDATE);
                bundle.putString("Title", "修改");
                DialogFragment dialog = new RemindDialog();
                dialog.setArguments(bundle);
                dialog.show(getSupportFragmentManager(), "RemindDialog");
                return true;
            }
        });
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        if (dialog.getTag() == null) return;
        switch (dialog.getTag()) {
            case "RemindDialog":
                updateData();
                break;
            case "PermissionDialog": {
                getPermission();
            }
            break;
            default:
                break;
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        if (dialog.getTag() == null) return;
        switch (dialog.getTag()) {
            case "RemindDialog":
                break;
            case "PermissionDialog": {
                Toast.makeText(this, "没有权限APP就废了", Toast.LENGTH_SHORT).show();
            }
            break;
            default:
                break;
        }
    }

    public void updateData() {
        appInfos.clear();
        remindArrayList.clear();
        initData();
        remindAdapter = new RemindAdapter(appInfos, remindArrayList, context, sqlHelper);
        exlv_remind.setAdapter(remindAdapter);
        int groupCount = exlv_remind.getCount();
        for (int i = 0; i < groupCount; i++) {
            exlv_remind.expandGroup(i);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        receiver = new DataUpdateReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.ACTION_DATA_CHANGE);
        registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    //检测用户是否对本app开启了“Apps with usage access”权限
    private boolean hasPermission() {
        AppOpsManager appOps = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), getPackageName());
        return mode == AppOpsManager.MODE_ALLOWED;
    }
}
