package com.parmar.amarjot.android_recipe_book_with_firebase;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    String currentFragment, currentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        currentFilter = getString(R.string.filter_all);

        setupActionbar();
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        // Checking if user is trying to import recipe
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if (getString(R.string.share_type).equals(type)) {
                handleIncomingRecipe(intent); // Handle recipe being sent
            }
        }

        setupFragment();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    // Show user their local recipes
    public void setupFragment() {
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragment_local_recipe_list fragment = new fragment_local_recipe_list();
        fragmentTransaction.add(R.id.list_frame, fragment);
        fragmentTransaction.commit();

        currentFragment = getString(R.string.fragment_local);
    }

    // Loads appropriate list of recipes according to what user clicked
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            switch (item.getItemId()) {
                case R.id.navigation_local:
                    fragment_local_recipe_list localFragment = new fragment_local_recipe_list();
                    fragmentTransaction.replace(R.id.list_frame, localFragment);
                    fragmentTransaction.commit();
                    currentFragment = getString(R.string.fragment_local);
                    return true;
                case R.id.navigation_online:
                    fragment_online_recipe_list onlineFragment = new fragment_online_recipe_list();
                    fragmentTransaction.replace(R.id.list_frame, onlineFragment);
                    fragmentTransaction.commit();
                    currentFragment = getString(R.string.fragment_online);
                    return true;
            }
            return false;
        }
    };

    // Sets up custom action bar with filter spinner (all, vegan, vege)
    private void setupActionbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);

        SpinnerAdapter spinnerAdapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.category, R.layout.spinner_dropdown_item);
        Spinner navigationSpinner = new Spinner(getSupportActionBar().getThemedContext());
        navigationSpinner.setAdapter(spinnerAdapter);
        toolbar.addView(navigationSpinner, 1);

        final String [] category = getResources().getStringArray(R.array.category);

        navigationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            // Depending on what recipes (local, online) user is viewing, call appropriate fragments method
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCategory = category[position];
                currentFilter = selectedCategory;
                FragmentManager fm = getSupportFragmentManager();

                if (getString(R.string.fragment_local).equals(currentFragment))
                {
                    fragment_local_recipe_list fragment = (fragment_local_recipe_list)fm.findFragmentById(R.id.list_frame);
                    assert fragment != null;
                    fragment.setupList(selectedCategory);
                }
                else if (getString(R.string.fragment_online).equals(currentFragment)) {
                    fragment_online_recipe_list fragment = (fragment_online_recipe_list)fm.findFragmentById(R.id.list_frame);
                    assert fragment != null;
                    fragment.setupList(selectedCategory);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    // gets called when user imports recipe
    private void handleIncomingRecipe(Intent intent) {
        String recipeData = intent.getStringExtra(Intent.EXTRA_TEXT);

        // Making sure imported data is recipe
        if (recipeData != null) {

            String data [] = recipeData.split(getString(R.string.split));

            if(data.length == 4 & !recipeExists(data[0]))
            {
                String recipeName = data[0];
                String recipeDescription = data[1];
                String recipeCategory= data[2];
                String recipeArticle = data[3];
                String recipeImageID= getString(R.string.default_recipe_imageID);

                System.out.println (recipeName + " " +  recipeDescription  + " " +  recipeCategory + " " +  recipeArticle);
                Recipe newRecipe = new Recipe(recipeName, recipeDescription, recipeCategory, recipeArticle, recipeImageID, "true");

                RecipeSQLiteDatabaseHelper localDB = new RecipeSQLiteDatabaseHelper(this, getString(R.string.local_db));
                localDB.addRecipe(newRecipe);
            }
            else {
                toastMessage(getString(R.string.error_recipe));
            }
        }
    }

    private boolean recipeExists(String recipe_name) {

        // this is prob bad (SQL injection :P )
        RecipeSQLiteDatabaseHelper localDB = new RecipeSQLiteDatabaseHelper(this, "localRecipes");
        return localDB.recipeExists(recipe_name);
    }

    private void toastMessage(String msg){

        Context context = getApplicationContext();
        CharSequence text = msg;
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    public String getCurrentFilter() {
        return currentFilter;
    }

    // Methods below used to retrieve IDs from resources

    public int getImageID(String imageName) {
        Resources resources = getApplicationContext().getResources();
        final int resourceId = resources.getIdentifier(imageName, "drawable",
                getApplicationContext().getPackageName());
        return resourceId;
    }

    public String getArticle(String articleName) {

        Resources resources = getApplicationContext().getResources();
        final int resourceId = resources.getIdentifier(articleName, "string",
                getApplicationContext().getPackageName());
        return getString(resourceId);
    }

    public String getDescription(String recipeDescriptionName) {
        System.out.println(recipeDescriptionName);
        Resources resources = getApplicationContext().getResources();
        final int resourceId = resources.getIdentifier(recipeDescriptionName, "string",
                getApplicationContext().getPackageName());
        return getString(resourceId);
    }

}
