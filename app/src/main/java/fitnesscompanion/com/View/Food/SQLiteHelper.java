package fitnesscompanion.com.View.Food;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import androidx.annotation.Nullable;

public class SQLiteHelper extends SQLiteOpenHelper {
    public SQLiteHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);

    }

    public void queryData(String sql)
    {
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL(sql);
    }

    public void insertData (String name, String price, byte[] image)
    {
        SQLiteDatabase database = getWritableDatabase();
        String sql = "INSERT INTO FOOD VALUES (NULL, ?, ?, ?)";

        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();

        statement.bindString(1, name);
        statement.bindString(2, price);
        statement.bindBlob(3, image);

        statement.executeInsert();
    }

    //update SQLite
//    public void updateData (String name, String price, byte[] image, int id)
//    {
//        SQLiteDatabase database = getWritableDatabase();
//
//        String sql = "UPDATE FOOD SET name = ?, price = ?, image = ? WHERE id = ?";
//        SQLiteStatement statement = database.compileStatement(sql);
//
//        statement.bindDouble(0, (double)id);
//        statement.bindString(1, name);
//        statement.bindString(2, price);
//        statement.bindBlob(3, image);
//
//
//        statement.execute();
//        database.close();
//
//    }

    public Cursor getData (String sql)
    {
        SQLiteDatabase database = getReadableDatabase();
        String[] selection = {"name","price","Id","image"};
//        return database.query("FOOD",selection,"name =" + id,null,null,null,null);
        return database.rawQuery(sql,null);

    }

    public Cursor getData1 (String sql)
    {
        SQLiteDatabase database = getReadableDatabase();
        String[] selection = {"name","price","id","image"};
//        return database.query("FOOD",selection,"name = bbb",null,null,null,null);
        return database.rawQuery(sql,null);

    }
    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
