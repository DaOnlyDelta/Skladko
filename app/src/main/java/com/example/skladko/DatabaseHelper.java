package com.example.skladko;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "skladko.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_ITEMS = "items";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_COUNT = "count";
    public static final String COLUMN_CATEGORY = "category";

    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_ITEMS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME + " TEXT NOT NULL UNIQUE, " +
                    COLUMN_COUNT + " REAL NOT NULL DEFAULT 0.0, " +
                    COLUMN_CATEGORY + " TEXT NOT NULL DEFAULT 'Brez kategorije'" +
                    ");";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
        insertDefaultItems(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS);
        onCreate(db);
    }

    private void insertDefaultItems(SQLiteDatabase db) {
        // Let's add some typical Slovenian bar stock items with realistic count values
        insertDefault(db, "Union", "Pivo", 24.0);
        insertDefault(db, "Laško", "Pivo", 18.0);
        insertDefault(db, "Borovničke", "Žgane pijače", 3.5);
        insertDefault(db, "Jack Daniel's", "Žgane pijače", 1.2);
        insertDefault(db, "Cviček", "Vino", 5.0);
        insertDefault(db, "Radenska", "Brezalkoholne pijače", 30.0);
        insertDefault(db, "Coca-Cola", "Brezalkoholne pijače", 15.0);
    }

    private void insertDefault(SQLiteDatabase db, String name, String category, double count) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_CATEGORY, category);
        values.put(COLUMN_COUNT, count);
        db.insert(TABLE_ITEMS, null, values);
    }

    public long insertItem(String name, String category, double count) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_CATEGORY, category);
        values.put(COLUMN_COUNT, count);
        long id = db.insert(TABLE_ITEMS, null, values);
        db.close();
        return id;
    }

    public boolean isNameExists(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ITEMS, new String[]{COLUMN_ID},
                COLUMN_NAME + " = ?", new String[]{name},
                null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

    public boolean isNameExistsExcludeId(String name, long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ITEMS, new String[]{COLUMN_ID},
                COLUMN_NAME + " = ? AND " + COLUMN_ID + " != ?", new String[]{name, String.valueOf(id)},
                null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

    public int updateItemCount(long id, double count) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_COUNT, count);
        int rows = db.update(TABLE_ITEMS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return rows;
    }

    public int updateItemNameAndCategory(long id, String newName, String newCategory) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, newName);
        values.put(COLUMN_CATEGORY, newCategory);
        int rows = db.update(TABLE_ITEMS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return rows;
    }

    public int deleteItem(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_ITEMS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return rows;
    }

    public List<ItemModel> getAllItems() {
        List<ItemModel> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ITEMS, null, null, null, null, null, COLUMN_CATEGORY + " ASC, " + COLUMN_NAME + " ASC");

        if (cursor.moveToFirst()) {
            int idCol = cursor.getColumnIndex(COLUMN_ID);
            int nameCol = cursor.getColumnIndex(COLUMN_NAME);
            int countCol = cursor.getColumnIndex(COLUMN_COUNT);
            int catCol = cursor.getColumnIndex(COLUMN_CATEGORY);

            do {
                long id = cursor.getLong(idCol);
                String name = cursor.getString(nameCol);
                double count = cursor.getDouble(countCol);
                String category = cursor.getString(catCol);
                list.add(new ItemModel(id, name, count, category));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }
}