package com.osama.cryptofm.encryption;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

import java.io.File;

/**
 * Created by osama on 10/12/16.
 * database handle class
 */

public class DatabaseHandler extends SQLiteOpenHelper {
    private Context context;
    private static final String TAG             ="database";
    private static final String DATABASE_NAME   ="pierce";
    private static final int DATABASE_VERSION   =1;
    private SQLiteDatabase mDB;
    public DatabaseHandler(Context context,String pass,Boolean isCreated){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
        SQLiteDatabase.loadLibs(context);
        Log.d(TAG, "DatabaseHandler: this is somewhat messed up ");
        this.context=context;
        File databaseFile=context.getDatabasePath(DATABASE_NAME+".db");

        if(!isCreated){
            databaseFile.mkdirs();
            databaseFile.delete();
            Log.d(TAG,"database was not present, so created");
            mDB=SQLiteDatabase.openOrCreateDatabase(databaseFile,pass,null);
            mDB.execSQL(FeedReaderContract.CREATE_TABLE_SECRING);
            mDB.execSQL(FeedReaderContract.CREATE_TABLE_PUBRING);
            close();

        }else{
            Log.d(TAG, "database was present, so opened");
            mDB=SQLiteDatabase.openDatabase(databaseFile.getPath(),pass,null,0);
        }

    }
    // this constructor will allow me to test input password
    public DatabaseHandler(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
        this.context=context;

    }
    public Boolean checkPass(String pass){
        File databaseFile=context.getDatabasePath(DATABASE_NAME+".db");
        try{
            SQLiteDatabase db=SQLiteDatabase.openDatabase(databaseFile.getPath(),pass,null,0);
            db.close();
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public boolean insertSecKey(String email,byte[] secKeyText){
        boolean status;
        //create content values to put in db
        //not verifying email address here

        Log.d(TAG,"started inserting secret key");
        ContentValues contentValues=new ContentValues();
        contentValues.put(FeedReaderContract.SecRing.TB_COL2_EMAIL,email);
        contentValues.put(FeedReaderContract.SecRing.TB_COL3_SECKEY,secKeyText);
        try{
            mDB.insert(FeedReaderContract.SecRing.TABLE_NAME,null,contentValues);
            status=true;
            Log.d(TAG,"secret key insertion successful");
        }catch (Exception e){
            status=false;
            e.printStackTrace();
            Log.d(TAG,"cannot insert secret key");
        }
        mDB.close();
        return status;

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
    public byte[] getSecretKeyFromDb(String email){
        Log.d("goo34", "getSecretKeyFromDb: "+email);
        byte[] data=null;
        String[] columns=
                {FeedReaderContract.SecRing.TB_COL2_EMAIL,
                        FeedReaderContract.SecRing.TB_COL3_SECKEY};
        String selection= FeedReaderContract.SecRing.TB_COL2_EMAIL + " = ?";
        String[] selectionArgs={email};
        Cursor query=mDB.query(
                FeedReaderContract.SecRing.TABLE_NAME,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null
        );
        if(query.moveToFirst()){
            int index=query.getColumnIndex(FeedReaderContract.SecRing.TB_COL3_SECKEY);
            data=query.getBlob(index);
            Log.d("test","NUmber of rows: "+query.getCount());
        }

        query.close();
        mDB.close();
        assert data!=null;
        return data;
    }


}
