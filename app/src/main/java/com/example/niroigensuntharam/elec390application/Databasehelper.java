package com.example.niroigensuntharam.elec390application;

/**
 * Created by abdullahalsaeed on 2017-10-29.
 */

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;


public class Databasehelper extends SQLiteOpenHelper
{
    private SQLiteDatabase myDataBase;
    private final Context myContext;
    private static final String DATABASE_NAME = "lab_info.db";
    public  static String DATABASE_PATH = "";
    public static final int DATABASE_VERSION = 1;
    //public static final int DATABASE_VERSION_old = 1;
    //Constructor

    public Databasehelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.myContext = context;
        DATABASE_PATH = myContext.getDatabasePath(DATABASE_NAME).toString();
        //Log.v("DB Path", myContext.getDatabasePath(DATABASE_NAME).toString());
    }

    //Create a empty database on the system
    public void createDatabase() throws IOException
    {
        boolean dbExist = checkDataBase();
        if(dbExist)
        {
            Log.v("DB Exists", "db exists");
            // By calling this method here onUpgrade will be called on a
            // writeable database, but only if the version number has been
            // bumped
            //onUpgrade(myDataBase, DATABASE_VERSION_old, DATABASE_VERSION);
        }
        boolean dbExist1 = checkDataBase();
        if(!dbExist1)
        {
            this.getReadableDatabase();
            try
            {
                this.close();
                copyDataBase();
            }
            catch (IOException e)
            {
                throw new Error("Error copying database");
            }
        }
    }

    //Check database already exist or not
    private boolean checkDataBase()
    {
        boolean checkDB = false;
        try
        {
            String myPath = DATABASE_PATH;
            File dbfile = new File(myPath);
            checkDB = dbfile.exists();
        }
        catch(SQLiteException e)
        {
        }
        return checkDB;
    }

    //Copies your database from your local assets-folder to the just created empty database in the system folder

    private void copyDataBase() throws IOException
    {
        String outFileName = DATABASE_PATH;
        OutputStream myOutput = new FileOutputStream(outFileName);
        InputStream myInput = myContext.getAssets().open(DATABASE_NAME);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0)
        {
            myOutput.write(buffer, 0, length);
        }
        myInput.close();
        myOutput.flush();
        myOutput.close();
    }

    //delete database
    public void db_delete()
    {
        File file = new File(DATABASE_PATH);
        if(file.exists())
        {
            file.delete();
            System.out.println("delete database file.");
        }
    }

    //Open database
    public void openDatabase() throws SQLException
    {
        String myPath = DATABASE_PATH;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
    }

    public synchronized void closeDataBase()throws SQLException
    {
        if(myDataBase != null)
            myDataBase.close();
        super.close();
    }

    public void onCreate(SQLiteDatabase db)
    {
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        if (newVersion > oldVersion)
        {
            Log.v("Database Upgrade", "Database version higher than old.");
            db_delete();
        }
    }

    public ArrayList<Room> getData() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Room> car= new ArrayList<Room>();
        Cursor result = db.rawQuery("select * from lab" , null);
        while(result.moveToNext()){
            car.add( new Room(result.getString(result.getColumnIndex("lab_no")), result.getString(result.getColumnIndex("lab_capacity"))));

        }
        return car;
    }

    public ArrayList<Room> getDataByName(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Room> car= new ArrayList<Room>();
        Cursor result = db.rawQuery("select * from lab where lab_no = " + name , null);
        while(result.moveToNext()){
            car.add( new Room(result.getString(result.getColumnIndex("lab_no")), result.getString(result.getColumnIndex("lab_capacity"))));

        }
        return car;
    }


    //add your public methods for insert, get, delete and update data in database.
}
