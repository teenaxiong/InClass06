package com.example.android.inclass06;

import android.graphics.Bitmap;

public class Article {
    String author, title, description, url, publish, content;
    String bitmap;
    Source source;

    public Article() {
    }

    public String getAuthor() {
        return author;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }

    public String getPublish() {
        return publish;
    }

    public String getContent() {
        return content;
    }

    public String getBitmap() {
        return bitmap;
    }

    public Source getSource() {
        return source;
    }
}
