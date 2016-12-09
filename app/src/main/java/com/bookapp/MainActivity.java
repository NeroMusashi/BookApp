package com.bookapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public static final String LOG_TAG = BookActivity.class.getSimpleName();

    /**
     * URL to query the Google Books API for book information
     */
    public static final String REQUEST_URL = "https://www.googleapis.com/books/v1/volumes?q=";

    //Declaring some global variables
    private EditText searchEditText;
    private String userTypedQuery;
    public static String modifiedUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Find the search button
        Button searchButton = (Button) findViewById(R.id.button);

        //Set an OnClickListener on it
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Create a new intent to open {@link BookActivity)
                Intent searchIntent = new Intent(MainActivity.this, BookActivity.class);

                //Get the user's typed search query
                searchEditText = (EditText) findViewById(R.id.editText);
                userTypedQuery = searchEditText.getText().toString();
                userTypedQuery = userTypedQuery.trim();
                userTypedQuery = userTypedQuery.replace(" ", "+");

                //Add the typed query by the user to the request url
                modifiedUrl = REQUEST_URL + userTypedQuery;
                Log.v(LOG_TAG, "url is: " + modifiedUrl);

                //Add the modifiedUrl to the intent so it can be retrieved in the {@link BookActivity)
                searchIntent.putExtra(modifiedUrl, REQUEST_URL + userTypedQuery);

                // Check if the device has internet connection.
                ConnectivityManager cm =
                        (ConnectivityManager) MainActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);

                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null &&
                        activeNetwork.isConnectedOrConnecting();

                // If there is one, perform the search
                if (isConnected) {

                    //Start the activity
                    startActivity(searchIntent);
                }
                // If not, show a warning message in a toast
                // Also, it will be better if the user stays in the same activity rather than going
                // into the next one without any result. How to do that?
                else {
                    Toast.makeText(MainActivity.this, "Make sure you have internet connection", Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}