package ru.greenfil.translator;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Класс общения с БД (история и избранное)
 */

public class DBHelper extends SQLiteOpenHelper {
    public static final String TABLE_HISTORY = "history";
    public static final String TABLE_FAVORITES = "history";

    public static final String KEY_ID = "_id";
    public static final String KEY_SOURCE_LANG = "sourceLang";
    public static final String KEY_TARGET_LANG = "targetLang";
    public static final String KEY_SOURCE_TEXT = "sourceText";
    public static final String KEY_TARGET_TEXT = "targetText";
    private static final String DATABASE_NAME =  "translatorDB";
    private static final int DATABASE_VERSION = 1;

    private String getCreateSQL(String db_Name){
        return "create table " + db_Name + "(" +
                KEY_ID + " integer primary key," +
                KEY_SOURCE_LANG + " text," +
                KEY_TARGET_LANG + " text" +
                KEY_SOURCE_TEXT + " text" +
                KEY_TARGET_TEXT + " text" +
                ")"; //надо поставить уникальность на первые 3 поля
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(getCreateSQL(TABLE_HISTORY));
        db.execSQL(getCreateSQL(TABLE_FAVORITES));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_HISTORY);
        db.execSQL("drop table if exists " + TABLE_FAVORITES);
        onCreate(db);
    }

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
}
