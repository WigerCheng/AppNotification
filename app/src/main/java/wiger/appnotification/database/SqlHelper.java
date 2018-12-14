package wiger.appnotification.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import java.util.ArrayList;

import wiger.appnotification.model.Remind;

public class SqlHelper {
    private static final String tableName = "remind";
    private SQLiteDatabase database;

    public SqlHelper(SQLiteDatabase database) {
        this.database = database;
    }

    public void insertRemind(@NonNull Remind remind){
        ContentValues values = new ContentValues();
        values.put("msg",remind.getMsg());
        values.put("appName",remind.getAppName());
        values.put("appPackageName", remind.getAppPackageName());
        database.insert(tableName,null,values);
    }

    public void updateRemind(@NonNull Remind remind){
        ContentValues values = new ContentValues();
        values.put("msg", remind.getMsg());
        database.update(tableName, values,"id=?",new String[]{String.valueOf(remind.getId())});
    }

    public void deleteRemind(@NonNull Remind remind){
        database.delete(tableName,"id=?",new String[]{String.valueOf(remind.getId())});
    }

    public void deleteReminds(@NonNull String appPackageName){
        database.delete(tableName, "appPackageName=?",new String[]{appPackageName});
    }

    public ArrayList<Remind> selectReminds(){
        ArrayList<Remind> reminds = new ArrayList<>();
        Cursor cursor = database.query(tableName,null,null,null,null,null,null);
        if (cursor.moveToFirst()){
            do {
                Remind remind = new Remind();
                remind.setId(cursor.getInt(cursor.getColumnIndex("id")));
                remind.setMsg(cursor.getString(cursor.getColumnIndex("msg")));
                remind.setAppName(cursor.getString(cursor.getColumnIndex("appName")));
                remind.setAppPackageName(cursor.getString(cursor.getColumnIndex("appPackageName")));
                reminds.add(remind);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return reminds;
    }

    public ArrayList<Remind> selectRemind(String appPackageName){
        ArrayList<Remind> reminds = new ArrayList<>();
        Cursor cursor = database.query(tableName,null,"appPackageName=?",new String[]{appPackageName},null,null,null);
        if (cursor.moveToFirst()){
            do {
                Remind remind = new Remind();
                remind.setId(cursor.getInt(cursor.getColumnIndex("id")));
                remind.setMsg(cursor.getString(cursor.getColumnIndex("msg")));
                remind.setAppName(cursor.getString(cursor.getColumnIndex("appName")));
                remind.setAppPackageName(cursor.getString(cursor.getColumnIndex("appPackageName")));
                reminds.add(remind);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return reminds;
    }
}
