package com.bookapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

import static com.bookapp.MainActivity.modifiedUrl;

public final class BookActivity extends AppCompatActivity {


    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    // Declaring some global variables
    private String queryUrl;
    private BooksAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_item_list);

        // Get the modifiedUrl from the {@link MainActivity} and store it into a new variable called queryUrl
        Intent intent = getIntent();
        queryUrl = intent.getStringExtra(modifiedUrl);
        Log.v(LOG_TAG, "url is: " + queryUrl);

        // Find a reference to the {@link ListView} in the layout
        ListView bookList = (ListView) findViewById(R.id.list);
        bookList.setEmptyView(findViewById(R.id.empty_list_view));

        // Create a new {@link BooksAdapter} of books
        adapter = new BooksAdapter(BookActivity.this, new ArrayList<BookInformation>());

        // Set the adapter on the {@link ListView} so the list can be populated in the user interface
        bookList.setAdapter(adapter);

        // Kick off an {@link BookAsyncTask} to perform the network request
        // Here instead of just doing task.execute(); I was advised by a Forum mentor
        // to pass the URL variable as a parameter to the task instead of using the member
        // variable in the activity.
        BookAsyncTask task = new BookAsyncTask();
        try {
            task.execute(new URL(queryUrl));
        } catch (MalformedURLException exception) {
            Log.e(LOG_TAG, "Error with creating URL", exception);
        }
    }

    /**
     * {@link AsyncTask} to perform the network request on a background thread
     */
    class BookAsyncTask extends AsyncTask<URL, Void, ArrayList<BookInformation>> {

        @Override
        protected ArrayList<BookInformation> doInBackground(URL... urls) {
            // Create URL object
            URL url = createUrl(queryUrl);
            String jsonResponse = "";
            try {
                jsonResponse = makeHttpRequest(url);
            } catch (IOException e) {
                Log.e(LOG_TAG, "");
            }

            // Extract relevant fields from the JSON response and create a {@link Book} object
            ArrayList<BookInformation> books = extractFeatureFromJson(jsonResponse);
            Log.v(LOG_TAG, "books is " + books);

            // Return the {@link Book} object as the result fo the {@link BookAsyncTask}
            return books;
        }

        /**
         * Update the screen with the given book info (which was the result of the
         * {@link BookAsyncTask}).
         */
        @Override
        protected void onPostExecute(ArrayList<BookInformation> books) {
            if (books == null) {
                return;
            }
            adapter.addAll(books);
        }

        /**
         * Returns new URL object from the given string URL.
         */
        private URL createUrl(String stringUrl) {
            URL url = null;
            try {
                url = new URL(stringUrl);
            } catch (MalformedURLException exception) {
                Log.e(LOG_TAG, "Error with creating URL", exception);
                return null;
            }
            return url;
        }

        /**
         * Make an HTTP request to the given URL and return a String as the response.
         */
        private String makeHttpRequest(URL url) throws IOException {
            String jsonResponse = "";

            //If the url is null, then return early
            if (url == null) {
                return jsonResponse;
            }

            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setReadTimeout(10000 /* milliseconds */);
                urlConnection.setConnectTimeout(15000 /* milliseconds */);
                urlConnection.connect();

                //If the request was successful (response code 200)
                //then read the input stream and parse the response.
                if (urlConnection.getResponseCode() == 200) {
                    inputStream = urlConnection.getInputStream();
                    jsonResponse = readFromStream(inputStream);
                } else {
                    Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
                }
            } catch (IOException e) {
                Log.e(LOG_TAG, "Problem retrieving the book JSON results", e);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (inputStream != null) {
                    // function must handle java.io.IOException here
                    inputStream.close();
                }
            }
            return jsonResponse;
        }

        /**
         * Convert the {@link InputStream} into a String which contains the
         * whole JSON response from the server.
         */
        private String readFromStream(InputStream inputStream) throws IOException {
            StringBuilder output = new StringBuilder();
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    output.append(line);
                    line = reader.readLine();
                }
            }
            return output.toString();
        }

        /**
         * Return an ArrayList of the Book object by parsing out information
         * about the book from the input bookJSON string.
         */
        private ArrayList<BookInformation> extractFeatureFromJson(String bookJSON) {
            // If the JSON string is empty or null, then return early.
            if (TextUtils.isEmpty(bookJSON)) {
                return null;
            }

            try {
                JSONObject baseJsonResponse = new JSONObject(bookJSON);
                JSONArray itemsArray = baseJsonResponse.getJSONArray("items");
                ArrayList<BookInformation> bookList = new ArrayList<>();

                // If there are results in the items array
                for (int i = 0; i < itemsArray.length(); i++) {
                    // Extract out the first item
                    JSONObject firstItem = itemsArray.getJSONObject(i);
                    JSONObject volumeInfo = firstItem.getJSONObject("volumeInfo");

                    // Extract out the title of the book
                    String title = volumeInfo.getString("title");

                    //Extract out the authors of the book
                    String authors = "";
                    if (volumeInfo.has("authors")) {
                        JSONArray authorsArray = volumeInfo.getJSONArray("authors");
                        authors = authorsArray.join(", ") + ".";
                        authors = authors.replaceAll("\"", "");
                    }

                    // Create a new {@link Book} object
                    bookList.add(new BookInformation(title, authors));
                }
                return bookList;

            } catch (JSONException e) {
                Log.e(LOG_TAG, "Problem parsing the book JSON results", e);
            }
            return null;
        }
    }
}
