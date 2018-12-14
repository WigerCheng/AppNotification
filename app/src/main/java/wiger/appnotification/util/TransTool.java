package wiger.appnotification.util;

import java.util.ArrayList;
import java.util.List;

import wiger.appnotification.model.AppInfo;
import wiger.appnotification.model.Remind;

public class TransTool {
    public static final String TAG = TransTool.class.getSimpleName();

    public static AppInfo[][] fromListToArray(List<AppInfo> list){
        AppInfo[][] apps = new AppInfo[2][];
        apps[0] = new AppInfo[list.size()];
        apps[1]= new AppInfo[list.size()];
        int total=0,user_index=0,system_count=0;
        while (total<list.size()){
            if (list.get(total).getAppType()==AppInfo.USER_APP){
                apps[0][user_index] = list.get(total);
                user_index++;
            }
            if (list.get(total).getAppType()==AppInfo.SYSTEM_APP){
                apps[1][system_count] = list.get(total);
                system_count++;
            }
            total ++;
        }
        return apps;
    }

    public static String[] fromListToArray(ArrayList<Remind> reminds){
        String[] msgs = new String[]{};
        for (int i = 0;i <= reminds.size();i++){
            msgs[i] = reminds.get(i).getMsg();
        }
        return msgs;
    }
}
