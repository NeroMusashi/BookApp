package com.bookapp;

public class BookInformation {

    private String mAuthor;

    private String mBookTitle;

    public BookInformation(String author, String bookTitle) {
        mAuthor = author;
        mBookTitle = bookTitle;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public String getBookTitle() {
        return mBookTitle;
    }
}
