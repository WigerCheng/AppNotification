package wiger.appnotification.util;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NavigableSet;
import java.util.TreeMap;

import wiger.appnotification.model.AppInfo;

/**
 * App工具
 */
public class AppTool {
    static final String TAG = AppTool.class.getSimpleName();

    /**
     * Return a List of all packages that are installed on the device.
     * 获得已安装的系统APP
     *
     * @param packageManager packageManager
     * @return 已安装的系统APP
     */
    public static List<AppInfo> getSystemInstelledApp(PackageManager packageManager) {
        List<AppInfo> apps = new ArrayList<>();
        List<PackageInfo> packages = packageManager.getInstalledPackages(0);
        for (PackageInfo packageInfo : packages) {
            // 判断系统/非系统应用
            AppInfo app = new AppInfo();
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) // 系统应用
            {
                app.setPackageName(packageInfo.packageName);
                app.setAppName(packageInfo.applicationInfo.loadLabel(packageManager).toString());
                app.setAppType(AppInfo.SYSTEM_APP);
                if (packageInfo.applicationInfo.loadIcon(packageManager) == null) {
                    continue;
                }
                app.setIcon(packageInfo.applicationInfo.loadUnbadgedIcon(packageManager));
            } else continue;
            apps.add(app);
        }
        return apps;
    }

    /**
     * 获得当前运行的APP包名
     *
     * @param context 上下文
     * @return 当前运行的APP包名
     */
    public static String getTopAppPackageName(Context context) {
        String topActivity = null;
        UsageStatsManager m = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        long now = System.currentTimeMillis();
        List<UsageStats> stats = m.queryUsageStats(UsageStatsManager.INTERVAL_BEST, now - 60 * 1000, now);
        TreeMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
        Field mLastEventField = null;
        for (UsageStats usageStats : stats) {
            mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
        }
        if (mySortedMap != null && !mySortedMap.isEmpty()) {
            NavigableSet<Long> keySet = mySortedMap.navigableKeySet();
            Iterator iterator = keySet.descendingIterator();
            while (iterator.hasNext()) {
                UsageStats usageStats = mySortedMap.get(iterator.next());
                if (mLastEventField == null) {
                    try {
                        mLastEventField = UsageStats.class.getField("mLastEvent");
                    } catch (NoSuchFieldException e) {
                        break;
                    }
                }
                int lastEvent = 0;
                try {
                    lastEvent = mLastEventField.getInt(usageStats);
                } catch (IllegalAccessException e) {
                    break;
                }
                if (lastEvent == 1) {
                    topActivity = usageStats.getPackageName();
                    break;
                }
            }
        }

        return topActivity;
    }

    /**
     * 获得已安装的用户APP
     *
     * @param packageManager packageManager
     * @return 已安装的用户APP
     */
    public static List<AppInfo> getUserInstelledApp(PackageManager packageManager) {
        List<AppInfo> apps = new ArrayList<>();
        List<PackageInfo> packages = packageManager.getInstalledPackages(0);
        for (PackageInfo packageInfo : packages) {
            // 判断系统/非系统应用
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) // 非系统应用
            {
                AppInfo app = new AppInfo();
                app.setPackageName(packageInfo.packageName);
                app.setAppName(packageInfo.applicationInfo.loadLabel(packageManager).toString());
                app.setAppType(AppInfo.USER_APP);
                app.setIcon(packageInfo.applicationInfo.loadIcon(packageManager));
                apps.add(app);
            }
        }
        return apps;
    }

    /**
     * 获得启动器的包名
     * @param packageManager packageManager
     * @return 启动器的包名
     */
    public static String getLauncherPackageName(PackageManager packageManager) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        ResolveInfo resolveInfo = packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return resolveInfo.activityInfo.packageName;
    }
}
