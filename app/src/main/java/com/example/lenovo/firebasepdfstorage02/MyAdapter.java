package com.example.lenovo.firebasepdfstorage02;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    RecyclerView recyclerView;
    Context context;
    ArrayList<String> item;
    ArrayList<String> urls;

    //CONSTRUCTOR
    MyAdapter(RecyclerView recyclerView, Context context, ArrayList<String> item, ArrayList<String> urls) {
        this.recyclerView = recyclerView;
        this.context = context;
        this.item = item;
        this.urls=urls;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        //for creating views for items in recycler view
        View view = LayoutInflater.from(context).inflate(R.layout.items, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        //initialising elements of items
        viewHolder.nameOfFile.setText(item.get(position));
    }

    @Override
    public int getItemCount() {
        //returns the no of files available online
        return item.size();
    }

    void update(String fileName, String url) {
        item.add(fileName);
        urls.add(url);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView nameOfFile;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameOfFile=  itemView.findViewById(R.id.nameOfFile);
            //what happens when we click on an item in recycler view
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = recyclerView.getChildLayoutPosition(view);
                    Intent intent = new Intent();
                    //we have to view the file
                    intent.setType(Intent.ACTION_VIEW).setData(Uri.parse(urls.get(pos)));
                    //intent.setDataAndType(Uri.parse("http://"+urls.get(pos)), Intent.ACTION_VIEW);
                    //launch the selected browser to display our corresponding file
                    //context.startActivity(intent);
                    Toast.makeText(context, urls.get(pos), Toast.LENGTH_LONG).show();
                    if (intent.resolveActivity(context.getPackageManager()) != null) {
                        context.startActivity(intent);
                    }
                    else{
                        //Toast.makeText(context, "ELSE PART BEING CALLED : context.getPackageManager() = NULL", Toast.LENGTH_LONG).show();
                        Toast.makeText(context, "DOCUMENT NOT ACKNOWLEDGED YET, PLS WAIT...", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

}
