package com.example.natksport;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CoachesAdapter extends RecyclerView.Adapter<CoachesAdapter.ViewHolder> {
    private List<Coach> coachList;

    public CoachesAdapter(List<Coach> coachList) {
        this.coachList = coachList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewFIO;
        public TextView textViewOpisanie;
        public ImageView imageViewCoach;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewFIO = itemView.findViewById(R.id.text_view_fio);
            textViewOpisanie = itemView.findViewById(R.id.text_view_opisanie);
            imageViewCoach = itemView.findViewById(R.id.image_view_coach);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_coach, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Coach coach = coachList.get(position);
        holder.textViewFIO.setText(coach.getFIO());

        // Получаем описание и заменяем два пробела на два переноса строки
        String opisanie = coach.getOpisanie().replace("  ", "\n\n");
        holder.textViewOpisanie.setText(opisanie);

        // Условие для установки изображения
        if (coach.getImage() != null && !coach.getImage().isEmpty()) {
            Bitmap bitmap = decodeBase64(coach.getImage());
            holder.imageViewCoach.setImageBitmap(bitmap);
        } else {
            // Установка дефолтного изображения, если поле пустое
            holder.imageViewCoach.setImageResource(R.drawable.logo);
        }
    }

    @Override
    public int getItemCount() {
        return coachList.size();
    }

    private Bitmap decodeBase64(String input) {
        byte[] decodedBytes = android.util.Base64.decode(input, android.util.Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }
}


