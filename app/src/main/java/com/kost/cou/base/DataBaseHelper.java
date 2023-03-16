package com.kost.cou.base;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHelper extends SQLiteOpenHelper {
   public static String DB_NAME = "pressureBase.db";
   public static final int DB_VERSION = 1;
   public static final String TABLE_RECORDS = "records";
   public static final String COLUMN_ID = "_id";
   public static final String COLUMN_DATE = "date";
   public static final String COLUMN_UP_PRES = "up_pres";
   public static final String COLUMN_DOWN_PRES = "dw_pres";
   public static final String COLUMN_PULS = "puls";

   private  Context context;

    public DataBaseHelper(Context context, String name,
                          SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table records(_id integer PRIMARY KEY AUTOINCREMENT,"
                +"date text, up_pres integer," +
                "dw_pres integer, puls integer)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,  int newVersion) {
        db.execSQL("drop table if exists " + TABLE_RECORDS);
        onCreate(db);
    }

//    public void create_db(){
//
//        File file = new File(DB_PATH);
//        if (!file.exists()) {
//            //получаем локальную бд как поток
//            try(InputStream myInput = myContext.getAssets().open(DB_NAME);
//                // Открываем пустую бд
//                OutputStream myOutput = new FileOutputStream(DB_PATH)) {
//
//                // побайтово копируем данные
//                byte[] buffer = new byte[1024];
//                int length;
//                while ((length = myInput.read(buffer)) > 0) {
//                    myOutput.write(buffer, 0, length);
//                }
//                myOutput.flush();
//            }
//            catch(IOException ex){
//                Log.d("DatabaseHelper", ex.getMessage());
//            }
//        }
//    }
//    public SQLiteDatabase open()throws SQLException {
//
//        return SQLiteDatabase.openDatabase(DB_PATH, null, SQLiteDatabase.OPEN_READWRITE);
//    }
}