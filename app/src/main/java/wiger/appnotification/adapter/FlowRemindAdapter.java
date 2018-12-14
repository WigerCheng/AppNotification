package wiger.appnotification.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import wiger.appnotification.R;
import wiger.appnotification.model.Remind;

public class FlowRemindAdapter extends RecyclerView.Adapter<FlowRemindAdapter.FlowRemindAdapterHolder>{
    private ArrayList<Remind> remindList;

    public FlowRemindAdapter(ArrayList<Remind> remindList) {
        this.remindList = remindList;
    }

    @NonNull
    @Override
    public FlowRemindAdapterHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_flowwindow_remind,viewGroup,false);
        return new FlowRemindAdapterHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FlowRemindAdapterHolder flowRemindAdapterHolder, int i) {
        Remind remind = remindList.get(i);
        flowRemindAdapterHolder.tv_remind.setText(remind.getMsg());
    }

    @Override
    public int getItemCount() {
        return remindList.size();
    }

    class FlowRemindAdapterHolder extends RecyclerView.ViewHolder {
        TextView tv_remind;

        FlowRemindAdapterHolder(@NonNull View itemView) {
            super(itemView);
            tv_remind = itemView.findViewById(R.id.tv_remind);
        }
    }
}
