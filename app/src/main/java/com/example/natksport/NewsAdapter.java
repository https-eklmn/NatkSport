package com.example.natksport;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private Context context;
    private List<Post> newsList;

    public NewsAdapter(Context context, List<Post> newsList) {
        this.context = context;
        this.newsList = newsList;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.news_item, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        Post news = newsList.get(position);
        holder.descriptionTextView.setText(news.getDescription());
        holder.imageView.setImageBitmap(decodeBase64(news.getImageBase64()));
        holder.dateTextView.setText(news.getDate());

        // Обработчик для кнопки "Редактировать"
        holder.itemView.findViewById(R.id.button_edit).setOnClickListener(v -> {
            showEditDialog(news);
        });

        // Обработчик для кнопки "Удалить"
        holder.itemView.findViewById(R.id.button_delete).setOnClickListener(v -> {
            deletePost(news.getId());
        });
    }

    private void showEditDialog(Post post) {
        NewPostDialog dialog = new NewPostDialog();
        dialog.setPost(post);
        dialog.show(((AppCompatActivity) context).getSupportFragmentManager(), "EditPostDialog");
    }

    private void deletePost(String postId) {
        DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("Novosti").child(postId);
        postRef.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(context, "Пост удалён успешно!", Toast.LENGTH_SHORT).show();
                newsList.removeIf(post -> post.getId().equals(postId));
                notifyDataSetChanged();
            } else {
                Toast.makeText(context, "Не удалось удалить пост", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    static class NewsViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView descriptionTextView;
        TextView dateTextView; // Новое поле для даты

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.news_image);
            descriptionTextView = itemView.findViewById(R.id.news_description);
            dateTextView = itemView.findViewById(R.id.news_date); // Инициализация new TextView для даты
        }
    }

    private Bitmap decodeBase64(String input) {
        byte[] decodedBytes = Base64.decode(input, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }
}