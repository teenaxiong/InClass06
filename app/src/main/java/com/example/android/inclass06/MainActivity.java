package com.example.android.inclass06;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

/*
Teena Xiong
In Class Assignment 06
800183555_InClass06.zip
 */

public class MainActivity extends AppCompatActivity implements CategoryAsync.CategoryInterface {

    ImageView imageView;
    ProgressBar progressBar;
    ArrayList<Article> articles;
    int currentPosition=0;
    ImageButton leftArrow;
    ImageButton rightArrow;
    TextView title;
    TextView content;
    TextView publish;
    ScrollView scrollView;
    TextView outOf;
    TextView published;
    TextView beginning;
    TextView ending;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        leftArrow = findViewById(R.id.leftArrow);
        rightArrow = findViewById(R.id.rightArrow);
        imageView = findViewById(R.id.image);
        progressBar = findViewById(R.id.progressBar);
        articles = new ArrayList<>();
        title = findViewById(R.id.title);
        content = findViewById(R.id.content);
        scrollView = findViewById(R.id.scrollView2);
        outOf = findViewById(R.id.outOf);
        published = findViewById(R.id.published);
        beginning = findViewById(R.id.beginning);
        ending = findViewById(R.id.end);


        findViewById(R.id.goButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isConnected()) {
                    final ArrayList<String> categoryList = new ArrayList<>();
                    final String[] stringArray = {"Business", "Entertainment", "General", "Health", "Science", "Sports", "Technology"};
                    categoryList.addAll(Arrays.asList(stringArray));
                    final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Pick one")
                            .setItems(stringArray, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int i) {
                                    TextView k = findViewById(R.id.categoryLabel);
                                    k.setText(categoryList.get(i));
                                    scrollView.setVisibility(View.INVISIBLE);
                                    currentPosition=0;
                                    new CategoryAsync(MainActivity.this, progressBar).execute("https://newsapi.org/v2/top-headlines?country=us&apiKey=d079311e42654a038ec41ee5c8d9bc8c&category=" + categoryList.get(i));

                                }
                            });
                    builder.create().show();

                } else {
                    Toast.makeText(MainActivity.this, "Internet is Not Connected", Toast.LENGTH_SHORT).show();
                }
            }
        });

        leftArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentPosition == 0) {
                    new GetDataAsync().execute(articles.get(articles.size() - 1));
                } else new GetDataAsync().execute(articles.get(currentPosition - 1));
            }
        });

        rightArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentPosition == articles.size() - 1) {
                    new GetDataAsync().execute(articles.get(0));
                } else   new GetDataAsync().execute(articles.get(currentPosition + 1));
            }
        });
    }

    private boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null || !networkInfo.isConnected()) {
            if ((networkInfo.getType() != ConnectivityManager.TYPE_WIFI) && (networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
                return false;
            }
        }
        return true;
    }


    @Override
    public void handleCategoryData(ArrayList<Article> articles) {
        this.articles = articles;


        if (articles.size() == 1 || articles.isEmpty()) {
            leftArrow.setEnabled(false);
            rightArrow.setEnabled(false);
            imageView.setImageResource(android.R.color.transparent);
            if (articles.isEmpty()) {
                Toast toast = Toast.makeText(getApplicationContext(), "No News Found", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        } else {
            leftArrow.setEnabled(true);
            rightArrow.setEnabled(true);
        }
        new GetDataAsync().execute(articles.get(0));

    }


    private class GetDataAsync extends AsyncTask<Article, Void, String> {

        Article article;
        @Override
        protected void onPreExecute() {
            scrollView.setVisibility(View.INVISIBLE);
            leftArrow.setVisibility(View.INVISIBLE);
            rightArrow.setVisibility(View.INVISIBLE);
            scrollView.setVisibility(View.INVISIBLE);
            rightArrow.setVisibility(View.INVISIBLE);
            beginning.setVisibility(View.INVISIBLE);
            ending.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.INVISIBLE);
            outOf.setVisibility(View.INVISIBLE);
        }

        @Override
        protected String doInBackground(Article... params) {
            HttpURLConnection connection = null;
            article = new Article();
            String result = null;
            try {

                URL url = new URL(params[0].getBitmap());
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    result = IOUtils.toString(connection.getInputStream(), "UTF8");
                    article = params[0];
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            scrollView.setVisibility(View.VISIBLE);
            leftArrow.setVisibility(View.VISIBLE);
            rightArrow.setVisibility(View.VISIBLE);
            scrollView.setVisibility(View.VISIBLE);
            rightArrow.setVisibility(View.VISIBLE);
            beginning.setVisibility(View.VISIBLE);
            ending.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
            imageView.setVisibility(View.VISIBLE);
            outOf.setVisibility(View.VISIBLE);

            Picasso.get().load(article.getBitmap()).into(imageView);
            ending.setText(String.valueOf(articles.size()));
            title.setText(article.getTitle());
            published.setText(article.getPublish());
            content.setText(article.getContent());
            currentPosition = articles.indexOf(article);


            if (articles.indexOf(article) == 0) {
                beginning.setText(String.valueOf(1));
            } else if (articles.indexOf(article) == articles.size()) {
                beginning.setText(String.valueOf(articles.size() - 1));
            } else beginning.setText(String.valueOf(articles.indexOf(article) + 1));
        }
    }

}
