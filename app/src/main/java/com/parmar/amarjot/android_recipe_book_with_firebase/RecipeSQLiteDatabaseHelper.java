package com.parmar.amarjot.android_recipe_book_with_firebase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RecipeSQLiteDatabaseHelper extends SQLiteOpenHelper {

    private String TABLE_NAME;
    private static final String COL0 = "ID";
    private static final String COL1 = "name";
    private static final String COL2 = "description";
    private static final String COL3 = "category";
    private static final String COL4 = "article";
    private static final String COL5 = "imageID";
    private static final String COL6 = "userAdded";

    public RecipeSQLiteDatabaseHelper(Context context, String _tableName) {

        super(context, _tableName, null, 1);
        TABLE_NAME = _tableName;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String createTable = "CREATE TABLE " + TABLE_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                " " + COL1 + " TEXT," + " " + COL2 + " TEXT," + " " + COL3 + " TEXT," + " " +
                COL4 + " TEXT," + " " + COL5 + " TEXT," + " " +
                COL6 + " TEXT)";

        sqLiteDatabase.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP IF TABLE EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public boolean addRecipe(Recipe recipe) {

        Recipe addRecipe = recipe;
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL1, addRecipe.getName());
        contentValues.put(COL2, addRecipe.getDescription());
        contentValues.put(COL3, addRecipe.getCategory());
        contentValues.put(COL4, addRecipe.getArticle());
        contentValues.put(COL5, addRecipe.getImageID());
        contentValues.put(COL6, addRecipe.getUserAdded());
        SQLiteDatabase db = this.getWritableDatabase();

        long result = db.insert(TABLE_NAME, null, contentValues);

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public boolean deleteRecipe(String recipeName) {

        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(TABLE_NAME, COL1 + "=\"" + recipeName + "\"", null);

        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public Recipe getRecipe(String recipeName) {
        Recipe recipe = null;
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COL1 + "=" + "\"" + recipeName + "\"";
        Cursor data = db.rawQuery(query, null);


        while (data.moveToNext()) {
            String recipeNamee = data.getString(1);
            String recipeDescription = data.getString(2);
            String recipeCategory = data.getString(3);
            String recipeArticle = data.getString(4);
            String recipeImageID = data.getString(5);
            String recipeUserAdded= data.getString(6);
            recipe = new Recipe(recipeNamee, recipeDescription, recipeCategory, recipeArticle, recipeImageID, recipeUserAdded);
            return recipe;
        }
        return recipe;
    }

    public Cursor getRecipes(String filter) {

        Cursor recipes;
        SQLiteDatabase db = this.getWritableDatabase();
        String query = null;

        switch (filter) {
            case "All":
                query = "SELECT * FROM " + TABLE_NAME;
                break;
            case "Vegetarian":
                query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COL3 + "=" +"\"" + filter + "\""+
                        " OR " + COL3 + "=" +"\"" + "Vegan" + "\" ";
                break;
            case "Vegan":
                query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COL3 + "=" +"\"" + filter + "\" ";
                break;
        }

        recipes = db.rawQuery(query, null);
        return recipes;
    }

    public boolean recipeExists(String recipeName) {

        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT EXISTS(SELECT * FROM " + TABLE_NAME + " WHERE " + COL1 + "=" + "\"" + recipeName + "\")";
        Cursor data = db.rawQuery(query, null);

        while (data.moveToNext()) {
            int dataExists = data.getInt(0);
            if (dataExists == 0) {
                return false;
            } else {
                return true;
            }
        }

        return true;
    }

    public void clearDatabase() {
        SQLiteDatabase db = this.getWritableDatabase();
        String clearDBQuery = "DELETE FROM "+TABLE_NAME;
        db.execSQL(clearDBQuery);
    }
}