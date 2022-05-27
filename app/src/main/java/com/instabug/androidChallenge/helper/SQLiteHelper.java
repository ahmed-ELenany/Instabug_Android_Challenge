package com.instabug.androidChallenge.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.instabug.androidChallenge.model.Words;

import java.util.ArrayList;
import java.util.List;

public class SQLiteHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "instabugWords";
    private static final String TABLE_WORDS = "words";
    private static final String KEY_ID = "id";
    private static final String KEY_WORD = "word";
    private static final String KEY_COUNT = "count";

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
     }
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Creating Tables
        String CREATE_WORDS_TABLE = "CREATE TABLE " + TABLE_WORDS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_WORD + " TEXT,"
                + KEY_COUNT + " INTEGER" + ")";
        db.execSQL(CREATE_WORDS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORDS);
        // Create tables again
        onCreate(db);
    }
    // code to add the new contact
    public void addWord(Words words) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_WORD, words.getWord());
        values.put(KEY_COUNT, words.getCount());

        // Inserting Row
        db.insert(TABLE_WORDS, null, values);
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection
    }

    // code to get the single
    public Words getWord(String word) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_WORDS, new String[] { KEY_ID,
                        KEY_WORD, KEY_COUNT }, KEY_WORD + "=?",
                new String[] { String.valueOf(word) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        if(cursor.getCount()<=0)
            return null;
        Words words = new Words(Integer.parseInt(cursor.getString(0)), cursor.getString(1), Integer.parseInt(cursor.getString(2)));
        // return contact
        return words;
    }

    // code to get all contacts in a list view
    public List<Words> getAllWords(String order,String searchText) {
        List<Words> contactList = new ArrayList<Words>();
        // Select All Query
        String selectQuery;
        if(searchText.isEmpty()){
              selectQuery = "SELECT  * FROM " + TABLE_WORDS+ " ORDER BY "+ KEY_COUNT + " " + order;
        }else{
              selectQuery = "SELECT  * FROM " + TABLE_WORDS+ " WHERE "+KEY_WORD+" LIKE '%"+searchText+"%' ORDER BY "+ KEY_COUNT + " " + order;

        }

         SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Words words = new Words();
                words.setId(Integer.parseInt(cursor.getString(0)));
                words.setWord(cursor.getString(1));
                words.setCount(Integer.parseInt(cursor.getString(2)));
                // Adding  to list
                contactList.add(words);
            } while (cursor.moveToNext());
        }

        // return contact list
        return contactList;
    }

    // code to update the single contact
    public int updateWord(Words words) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_WORD, words.getWord());
        values.put(KEY_COUNT, words.getCount());

        // updating row
        return db.update(TABLE_WORDS, values, KEY_ID + " = ?",
                new String[] { String.valueOf(words.getId()) });
    }

    // Deleting single  row
    public void deleteWord(Words words) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_WORDS, KEY_ID + " = ?",
                new String[] { String.valueOf(words.getId()) });
        db.close();
    }

    // Clear table
    public void clearWordsTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_WORDS);
         db.close();
    }

    // Getting contacts Count
    public int getWordssCount() {
        String countQuery = "SELECT  * FROM " + TABLE_WORDS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }

}

