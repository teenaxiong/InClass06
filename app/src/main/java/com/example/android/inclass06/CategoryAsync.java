package com.example.android.inclass06;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class CategoryAsync extends AsyncTask<String, Void, ArrayList<Article>> {

    CategoryInterface categoryInterface;
    ProgressBar progressBar;


    public CategoryAsync(CategoryInterface categoryInterface, ProgressBar progressBar) {
        this.progressBar = progressBar;
        this.categoryInterface = categoryInterface;
    }

    @Override
    protected void onPreExecute() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected ArrayList<Article> doInBackground(String... strings) {
        HttpURLConnection connection = null;
        ArrayList<Article> result = new ArrayList<>();
        String urlString = null;
        try {
            URL url = new URL(strings[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {

                urlString = IOUtils.toString(connection.getInputStream(), "UTF8");
                JSONObject root = new JSONObject(urlString);
                JSONArray articleJ = root.getJSONArray("articles");
                for(int x = 0; x<articleJ.length(); x++){
                    JSONObject articleJson = articleJ.getJSONObject(x);
                    JSONObject sourceJson = articleJson.getJSONObject("source");
                    Source source = new Source();
                    source.id = sourceJson.getString("id");
                    source.name = sourceJson.getString("name");

                    Article article = new Article();
                    article.author = articleJson.getString("author");
                    article.title = articleJson.getString("title");
                    article.description = articleJson.getString("description");
                    article.url = articleJson.getString("url");
                    article.bitmap = articleJson.getString("urlToImage");
                    article.publish = articleJson.getString("publishedAt");
                    article.content = articleJson.getString("content");
                    article.source = source;
                    result.add(article);
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            connection.disconnect();
        }
        return result;
    }




    @Override
    protected void onPostExecute(ArrayList<Article> articles) {
            categoryInterface.handleCategoryData(articles);
    }

    public static interface CategoryInterface{
        public void handleCategoryData(ArrayList<Article> articles);
    }
}
