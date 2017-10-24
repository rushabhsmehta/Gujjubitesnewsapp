package com.gujjubites;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 10/13/2017.
 */

public class BookMarksDatabase extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "BookMarksManager";

    // Contacts table name
    private static final String TABLE_BOOKMARKS = "BookMarks";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_BOOKMARK = "key";


    public BookMarksDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_BOOKMARKS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_BOOKMARK + " TEXT" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKMARKS);
        // Create tables again
        onCreate(db);
    }
    // Adding new contact
    public void addBookMarks(BookMarks bookMarks) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_BOOKMARK, bookMarks.getKey()); // Contact Name

        // Inserting Row
        db.insert(TABLE_BOOKMARKS, null, values);

        db.close(); // Closing database connection
    }

    public  boolean getBookMarks(String key) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_BOOKMARKS, new String[] { KEY_ID,
                        KEY_BOOKMARK }, KEY_BOOKMARK + "=?",
                new String[] { key }, null, null, null, null);

        if (cursor.getCount() > 0 )
            return true;
        else
            return false;
         //    cursor.moveToFirst();

        //BookMarks bookMarks = new BookMarks(Integer.parseInt(cursor.getString(0)),
        //        cursor.getString(1));
        // return bookmark
        //return bookMarks;
    }

    public List<BookMarks> getAllBookMarks() {
        List<BookMarks> bookMarksList = new ArrayList<BookMarks>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_BOOKMARKS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                BookMarks bookMark = new BookMarks();
                bookMark.setId(Integer.parseInt(cursor.getString(0)));
                bookMark.setKey(cursor.getString(1));

                // Adding contact to list
                bookMarksList.add(bookMark);
            } while (cursor.moveToNext());
        }
       // cursor.close();
        // return contact list
        return bookMarksList;
    }
    // Getting contacts Count
    public int getBookMarksCount() {

        String countQuery = "SELECT  * FROM " + TABLE_BOOKMARKS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        // return count
        return cursor.getCount();
    }
    // Updating single contact
    public int updateBookMarks(BookMarks contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_BOOKMARK, contact.getKey());
        // updating row
        return db.update(TABLE_BOOKMARKS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(contact.getId()) });
    }
    // Deleting single contact
    public void deleteBookMarks(String key) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_BOOKMARKS, KEY_BOOKMARK + " = ?",
                new String[] { key });
        db.close();
    }
}
