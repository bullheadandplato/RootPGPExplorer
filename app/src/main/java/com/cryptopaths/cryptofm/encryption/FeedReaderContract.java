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
        public static final String TB_COL4_PUBKEY="pubkey";
    }
    public static class PubRing implements BaseColumns{
        public static final String TABLE_NAME="pubring";
        public static final String TB_COL1_EMAIL="email";
        public static final String TB_COL2_PUBKEY="pubkey";
    }
    public static class DataTypes{
        public static final String INTEGER="INTEGER";
        public static final String TEXT="TEXT";
        public static final String VARCHAR="VARCHAR 255";

    }

    public static final String COMMA_SEPARATOR =",";
    public static final String CREATE_TABLE="CREATE TABLE";
    public static final String OPENING_BRACE="(";
    public static final String CLOSING_BRACE=")";
    public static final String TERMINATOR=";";
    //SQL queries
    public static final String CREATE_TABLE_SECRING=CREATE_TABLE+SecRing.TABLE_NAME+OPENING_BRACE+
            SecRing.TB_COL2_EMAIL+DataTypes.VARCHAR+ COMMA_SEPARATOR +
            SecRing.TB_COL3_SECKEY+DataTypes.TEXT+ COMMA_SEPARATOR +
            SecRing.TB_COL4_PUBKEY+DataTypes.TEXT+ COMMA_SEPARATOR +
            CLOSING_BRACE+TERMINATOR;
    public static final String CREATE_TABLE_PUBRING=CREATE_TABLE+PubRing.TABLE_NAME+OPENING_BRACE+
            PubRing.TB_COL1_EMAIL+DataTypes.VARCHAR+COMMA_SEPARATOR+
            PubRing.TB_COL2_PUBKEY+DataTypes.TEXT+
            CLOSING_BRACE+TERMINATOR;



}
