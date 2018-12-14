package wiger.appnotification.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import java.util.Objects;

import wiger.appnotification.Constant;
import wiger.appnotification.MyApplication;
import wiger.appnotification.R;
import wiger.appnotification.database.SqlHelper;
import wiger.appnotification.model.AppInfo;
import wiger.appnotification.model.Remind;

public class RemindDialog extends DialogFragment {
    private EditText ed_msg;
    private Bundle bundle;
    private int Mode;
    private SqlHelper sqlHelper;

    RemindDialogListener mListener;

    public RemindDialog() {
    }

    public interface RemindDialogListener {
        void onDialogPositiveClick(DialogFragment dialog);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.bundle = getArguments();
        this.Mode = Objects.requireNonNull(bundle).getInt("Mode");
        MyApplication application = (MyApplication) Objects.requireNonNull(getActivity()).getApplication();
        sqlHelper = application.getSqlHelper();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = Objects.requireNonNull(getActivity()).getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_note, null);
        ed_msg = view.findViewById(R.id.et_msg);
        builder.setView(view)
                .setTitle(bundle.getString("Title"))
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        switch (Mode){
            case Constant.MODE_INSERT:{
                builder.setPositiveButton("添加", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sqlHelper.insertRemind(getRemind((AppInfo)Objects.requireNonNull(bundle.getSerializable("AppInfo"))));
                        mListener.onDialogPositiveClick(RemindDialog.this);
                    }
                });
            }break;
            case Constant.MODE_UPDATE:{
                builder.setPositiveButton("修改", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onDialogPositiveClick(RemindDialog.this);
                        Remind remind = (Remind)Objects.requireNonNull(bundle.getSerializable("UpdateRemind"));
                        remind.setMsg(ed_msg.getText().toString());
                        sqlHelper.updateRemind(remind);
                    }
                });
            }break;
        }
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (RemindDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    private Remind getRemind(AppInfo appInfo){
        Remind remind = new Remind();
        remind.setAppName(appInfo.getAppName());
        remind.setAppPackageName(appInfo.getPackageName());
        remind.setMsg(ed_msg.getText().toString());
        return remind;
    }

}
