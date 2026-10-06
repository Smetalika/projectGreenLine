package com.example.project;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class LandmarkAdapter extends RecyclerView.Adapter<LandmarkAdapter.ViewHolder> {

    interface OnLandmarkClickListener {
        void onLandmarkClick(Landmark landmark, int position);
    }

    private final OnLandmarkClickListener onClickListener;
    private final LayoutInflater inflater;
    private final List<Landmark> landmarks;
    private final float fontSize;

    LandmarkAdapter(Context context, List<Landmark> landmarks, OnLandmarkClickListener onClickListener) {
        this.onClickListener = onClickListener;
        this.landmarks = landmarks;
        this.inflater = LayoutInflater.from(context);

        // Получаем сохранённый размер шрифта
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        this.fontSize = FontSizeHelper.getFontSize(prefs);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Landmark landmark = landmarks.get(position);
        holder.imageView.setImageResource(landmark.getImageResource());
        holder.nameView.setText(landmark.getName());

        // ⭐ ПРИМЕНЯЕМ РАЗМЕР ШРИФТА К ТЕКСТУ
        holder.nameView.setTextSize(fontSize);

        holder.itemView.setOnClickListener(v -> {
            onClickListener.onLandmarkClick(landmark, position);
        });
    }

    @Override
    public int getItemCount() {
        return landmarks.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final ImageView imageView;
        final TextView nameView;

        ViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.imageViewLandmark);
            nameView = view.findViewById(R.id.textViewName);
        }
    }
}