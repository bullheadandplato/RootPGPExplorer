package com.cryptopaths.cryptofm.encryption;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;
import net.sqlcipher.database.SQLiteStatement;

import org.spongycastle.asn1.dvcs.Data;
import org.spongycastle.crypto.digests.LongDigest;

import java.io.File;

/**
 * Created by osama on 10/12/16.
 */

public class DatabaseHandler extends SQLiteOpenHelper {
    private Context context;


    private static final String TAG="database";
    private static final String DATABASE_NAME="pierce";
    private static final int DATABASE_VERSION=1;
    private SQLiteDatabase mDB;
    public DatabaseHandler(Context context,String pass,Boolean isCreated){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
        SQLiteDatabase.loadLibs(context);
        this.context=context;
        File databaseFile=context.getDatabasePath(DATABASE_NAME+".db");
        if(isCreated){
            mDB=SQLiteDatabase.openOrCreateDatabase(databaseFile,pass,null);
            mDB.execSQL(FeedReaderContract.CREATE_TABLE_SECRING);
            mDB.execSQL(FeedReaderContract.CREATE_TABLE_PUBRING);

        }else{
            mDB=SQLiteDatabase.openDatabase(databaseFile.getPath(),pass,null,0);
        }

    }

    public boolean insertSecKey(String email,byte[] secKeyText){
        boolean status=false;
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

        return status;

    }
    public boolean insertPubKey(String email,String pubKeyText){
        boolean status=false;
        Log.d(TAG,"started inserting public key");
        ContentValues contentValues=new ContentValues();
        contentValues.put(FeedReaderContract.PubRing.TB_COL1_EMAIL,email);
        contentValues.put(FeedReaderContract.PubRing.TB_COL2_PUBKEY,pubKeyText);
        try{
            mDB.insert(FeedReaderContract.PubRing.TABLE_NAME,null,contentValues);
            status=true;
            Log.d(TAG,"public key successfully inserted");
        }catch (Exception e){
            status=false;
            e.printStackTrace();
            Log.d(TAG,"cannot insert public key");
        }
        return status;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
    public byte[] getSecretKeyFromDb(String email){
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
