package com.cryptopaths.cryptofm.encryption;

import android.provider.BaseColumns;

/**
 * Created by osama on 10/14/16.
 */

public final class FeedReaderContract implements BaseColumns{
    //make construct private so nobody can instantiate the class
    private FeedReaderContract(){}
    public static class SecRing implements BaseColumns{
        public static final String TABLE_NAME="secring";
        public static final String TB_COL2_EMAIL="email";
        public static final String TB_COL3_SECKEY="seckey";
    }
    public static class PubRing implements BaseColumns{
        public static final String TABLE_NAME="pubring";
        public static final String TB_COL1_EMAIL="email";
        public static final String TB_COL2_PUBKEY="pubkey";
    }



    //SQL queries
    public static final String CREATE_TABLE_SECRING="CREATE TABLE "+SecRing.TABLE_NAME+" ("
            + SecRing.TB_COL2_EMAIL+" TEXT,"
            +SecRing.TB_COL3_SECKEY+" BOLB"+
            " )";
    public static final String CREATE_TABLE_PUBRING="CREATE TABLE "+PubRing.TABLE_NAME+"( "+
            PubRing.TB_COL1_EMAIL+" TEXT,"+
            PubRing.TB_COL2_PUBKEY+" TEXT " +
            " )";



}
