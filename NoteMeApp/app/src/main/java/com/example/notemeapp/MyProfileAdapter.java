package com.example.notemeapp;

import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyProfileAdapter extends RecyclerView.Adapter<MyProfileAdapter.MyProfileViewHolder>{
    private List<String> myNotesNames;
    private List<String> myNotesDescriptions;

    public MyProfileAdapter(List<String> myNotesNames, List<String> myNotesDescriptions){
        this.myNotesNames = myNotesNames;
        this.myNotesDescriptions = myNotesDescriptions;
    }

    @NonNull
    @Override
    public MyProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_notes_profile, parent, false);
        return new MyProfileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyProfileViewHolder holder, int position) {
        String noteName = myNotesNames.get(position);
        String noteDesc = myNotesDescriptions.get(position);
        holder.noteName.setText(noteName);
        holder.noteDescription.setText(noteDesc);
    }

    @Override
    public int getItemCount() {
        return myNotesNames.size();
    }

    public static class MyProfileViewHolder extends RecyclerView.ViewHolder{
        public TextView noteName;
        public TextView noteDescription;
        public MyProfileViewHolder(@NonNull View itemView) {
            super(itemView);
            noteName = itemView.findViewById(R.id.note_name);
            noteDescription = itemView.findViewById(R.id.note_description);
        }
    }
}
