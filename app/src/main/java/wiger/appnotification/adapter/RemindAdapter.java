package wiger.appnotification.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import wiger.appnotification.Constant;
import wiger.appnotification.R;
import wiger.appnotification.database.SqlHelper;
import wiger.appnotification.model.AppInfo;
import wiger.appnotification.model.Remind;

public class RemindAdapter extends BaseExpandableListAdapter {
    private ArrayList<AppInfo> appInfos;
    private ArrayList<ArrayList<Remind>> reminds;
    private Context context;
    private SqlHelper sqlHelper;

    public RemindAdapter(ArrayList<AppInfo> appInfos, ArrayList<ArrayList<Remind>> reminds, Context context, SqlHelper sqlHelper) {
        this.appInfos = appInfos;
        this.reminds = reminds;
        this.context = context;
        this.sqlHelper = sqlHelper;
    }


    @Override
    public int getGroupCount() {
        return appInfos.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return reminds.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return appInfos.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return reminds.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        AppInfoViewHolder appInfoViewHolder;
        if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_exlist_appinfo,parent,false);
            appInfoViewHolder = new AppInfoViewHolder();
            appInfoViewHolder.img_appIcon = convertView.findViewById(R.id.img_appIcon);
            appInfoViewHolder.tv_appName = convertView.findViewById(R.id.tv_appName);
            appInfoViewHolder.imgbtn_deleteReminds = convertView.findViewById(R.id.imgbtn_deleteReminds);
            convertView.setTag(appInfoViewHolder);
        }else {
            appInfoViewHolder = (AppInfoViewHolder) convertView.getTag();
        }
        appInfoViewHolder.tv_appName.setText(appInfos.get(groupPosition).getAppName());
        appInfoViewHolder.img_appIcon.setImageDrawable(appInfos.get(groupPosition).getIcon());
        appInfoViewHolder.imgbtn_deleteReminds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sqlHelper.deleteReminds(appInfos.get(groupPosition).getPackageName());
                context.sendBroadcast(new Intent(Constant.ACTION_DATA_CHANGE));
            }
        });
        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        RemindViewHolder remindViewHolder;
        if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_exlist_remind,parent,false);
            remindViewHolder = new RemindViewHolder();
            remindViewHolder.tv_remind = convertView.findViewById(R.id.tv_remind);
            remindViewHolder.imgbtn_deleteRemind = convertView.findViewById(R.id.imgbtn_deleteRemind);
            convertView.setTag(remindViewHolder);
        }else {
            remindViewHolder = (RemindViewHolder) convertView.getTag();
        }
        remindViewHolder.tv_remind.setText(reminds.get(groupPosition).get(childPosition).getMsg());
        remindViewHolder.imgbtn_deleteRemind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sqlHelper.deleteRemind(reminds.get(groupPosition).get(childPosition));
                context.sendBroadcast(new Intent(Constant.ACTION_DATA_CHANGE));
            }
        });
        return convertView;
    }



    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }



    private static class AppInfoViewHolder{
        private ImageView img_appIcon;
        private TextView tv_appName;
        private ImageButton imgbtn_deleteReminds;
    }

    private static class RemindViewHolder{
        private TextView tv_remind;
        private ImageButton imgbtn_deleteRemind;
    }
}
