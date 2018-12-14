package wiger.appnotification;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import wiger.appnotification.database.DatabaseHelper;
import wiger.appnotification.database.SqlHelper;
import wiger.appnotification.model.AppInfo;
import wiger.appnotification.util.AppTool;

public class MyApplication extends Application {
    private static MyApplication instance;
    public List<AppInfo> mLocalInstalledApp;
    public SqlHelper sqlHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        initData();
    }

    public static Application getInstance(){
        return instance;
    }

    private void initData(){
        mLocalInstalledApp = new ArrayList<>();
        DatabaseHelper databaseHelper = new DatabaseHelper(this, "remind.db", null, 1);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        this.sqlHelper = new SqlHelper(database);
        if (mLocalInstalledApp.size()==0)
            mLocalInstalledApp.addAll(AppTool.getUserInstelledApp(getPackageManager()));
    }

    public  List<AppInfo> getmLocalInstalledApp() {
        return mLocalInstalledApp;
    }

    public SqlHelper getSqlHelper() {
        return sqlHelper;
    }
}
