/*
 * Copyright (c) 2017. slownet5
 *  This file is part of RootPGPExplorer also known as CryptoFM
 *
 *       RootPGPExplorer a is free software: you can redistribute it and/or modify
 *       it under the terms of the GNU General Public License as published by
 *       the Free Software Foundation, either version 3 of the License, or
 *       (at your option) any later version.
 *
 *       RootPGPExplorer is distributed in the hope that it will be useful,
 *       but WITHOUT ANY WARRANTY; without even the implied warranty of
 *       MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *       GNU General Public License for more details.
 *
 *       You should have received a copy of the GNU General Public License
 *       along with RootPGPExplorer.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.slownet5.pgprootexplorer.encryption;

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
    public DatabaseHandler(Context context,String pass,Boolean isCreated) throws Exception {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
        this.context=context;
        File databaseFile=context.getDatabasePath(DATABASE_NAME+".db");

        if(!isCreated){
            Log.d(TAG, "DatabaseHandler: im here");
            if(databaseFile.mkdirs()){
                Log.d(TAG, "DatabaseHandler: created dir");
            }else{
                throw new Exception("Cannot create dir");
            }
            databaseFile.delete();
            Log.d(TAG,"database was not present, so created");
            mDB=SQLiteDatabase.openOrCreateDatabase(databaseFile,pass,null);
            mDB.execSQL(FeedReaderContract.CREATE_TABLE_SECRING);
            mDB.execSQL(FeedReaderContract.CREATE_TABLE_PUBRING);
            close();
            mDB.close();
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
        ContentValues contentValues=new ContentValues();
        contentValues.put(FeedReaderContract.SecRing.TB_COL2_EMAIL,email);
        contentValues.put(FeedReaderContract.SecRing.TB_COL3_SECKEY,secKeyText);
        try{
            mDB.insert(FeedReaderContract.SecRing.TABLE_NAME,null,contentValues);
            status=true;
        }catch (Exception e){
            status=false;
            e.printStackTrace();
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
