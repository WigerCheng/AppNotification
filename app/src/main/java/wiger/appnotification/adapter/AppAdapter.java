package wiger.appnotification.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import wiger.appnotification.R;
import wiger.appnotification.model.AppInfo;

public class AppAdapter extends RecyclerView.Adapter<AppAdapter.AppInfoViewHolder> {

    private List<AppInfo> appList;
    private OnItemClickListener mOnItemClickListener;

    public AppAdapter(List<AppInfo> appList) {
        this.appList = appList;
    }

    public void setmOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    @NonNull
    @Override
    public AppInfoViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_appinfo, viewGroup, false);
        return new AppInfoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppInfoViewHolder appInfoViewHolder, final int i) {
        AppInfo appInfo = appList.get(i);
        appInfoViewHolder.tv_appName.setText(appInfo.getAppName());
        appInfoViewHolder.img_icon.setImageDrawable(appInfo.getIcon());
        if (mOnItemClickListener != null) {
            appInfoViewHolder.ll_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(v, i);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return appList.size();
    }

    class AppInfoViewHolder extends RecyclerView.ViewHolder {
        ImageView img_icon;
        TextView tv_appName;
        LinearLayout ll_layout;

        AppInfoViewHolder(@NonNull View itemView) {
            super(itemView);
            ll_layout = itemView.findViewById(R.id.ll_layout);
            img_icon = itemView.findViewById(R.id.img_appicon);
            tv_appName = itemView.findViewById(R.id.tv_appName);
        }
    }
}
