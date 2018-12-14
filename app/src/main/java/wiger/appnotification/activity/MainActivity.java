package wiger.appnotification.activity;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import wiger.appnotification.Constant;
import wiger.appnotification.MyApplication;
import wiger.appnotification.adapter.AppAdapter;
import wiger.appnotification.adapter.OnItemClickListener;
import wiger.appnotification.dialog.PermissionDialog;
import wiger.appnotification.dialog.RemindDialog;
import wiger.appnotification.model.AppInfo;
import wiger.appnotification.R;

public class MainActivity extends AppCompatActivity implements RemindDialog.RemindDialogListener {


    public static final String TAG = MainActivity.class.getSimpleName();

    public static List<AppInfo> mLocalInstalledApp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        MyApplication application = (MyApplication) getApplication();

        mLocalInstalledApp = application.getmLocalInstalledApp();
        RecyclerView recyclerView = findViewById(R.id.rv_applist);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 5);
        AppAdapter adapter = new AppAdapter(mLocalInstalledApp);
        adapter.setmOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                DialogFragment dialog = new RemindDialog();
                Bundle bundle = new Bundle();
                bundle.putInt("Mode",Constant.MODE_INSERT);
                bundle.putString("Title",mLocalInstalledApp.get(position).getAppName());
                bundle.putSerializable("AppInfo",mLocalInstalledApp.get(position));
                dialog.setArguments(bundle);
                dialog.show(getSupportFragmentManager(), "RemindDialog");
            }
        });
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        startActivity(new Intent(MainActivity.this,HomeActivity.class));
    }
}
