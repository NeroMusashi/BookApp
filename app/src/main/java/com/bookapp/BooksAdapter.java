package com.bookapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

class BooksAdapter extends ArrayAdapter<BookInformation> {

    public BooksAdapter(Context context, ArrayList<BookInformation> books) {
        super(context, 0, books);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //Check if there is an existing list item view (called convertView) that we can reuse,
        //otherwise , if convertView is null, then inflate a new list item layout.
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.information_list, parent, false);
        }

        //Find the book at the given position in the list of books
        BookInformation currentBook = getItem(position);

        //Find the TextView with view ID title
        TextView titleView = (TextView) listItemView.findViewById(R.id.book_title);
        //Get the title of the book from the current Book object and
        //set this text on that view
        assert currentBook != null;
        titleView.setText(currentBook.getBookTitle());

        //Find the TextView with view ID authors
        TextView authorsView = (TextView) listItemView.findViewById(R.id.authors);
        //Get the authors of the book from the current Book object and
        //set this text on that view
        authorsView.setText(currentBook.getAuthor());


        return listItemView;
    }
}