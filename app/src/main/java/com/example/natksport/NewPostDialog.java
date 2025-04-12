package com.example.natksport;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class NewPostDialog extends DialogFragment {

    private DatabaseReference databaseReference;
    private Uri selectedImageUri;
    private ProgressBar progressBar;
    private ImageView imagePreview;
    private Post post;
    public void setPost(Post post) {
        this.post = post;
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_new_post);
        EditText editTextDescription = dialog.findViewById(R.id.edit_text_description);
        Button buttonSelectImage = dialog.findViewById(R.id.button_select_image);
        Button buttonSave = dialog.findViewById(R.id.button_save);
        progressBar = dialog.findViewById(R.id.progress_bar);
        imagePreview = dialog.findViewById(R.id.image_preview);
        databaseReference = FirebaseDatabase.getInstance().getReference("Novosti");
        if (post != null) {
            editTextDescription.setText(post.getDescription());
            imagePreview.setImageBitmap(decodeBase64(post.getImageBase64()));
        }
        buttonSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Выберите фото"), 100);
            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String description = editTextDescription.getText().toString();
                if (post != null) {
                    updatePost(post.getId(), description, selectedImageUri);
                } else {
                    if (selectedImageUri != null) {
                        savePost(description, selectedImageUri);
                    } else {
                        Toast.makeText(getActivity(), "Пожалуйста, выберите изображение", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        return dialog;
    }
    private Bitmap decodeBase64(String input) {
        byte[] decodedBytes = Base64.decode(input, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }
    private void updatePost(String postId, String description, Uri imageUri) {
        progressBar.setVisibility(View.VISIBLE);
        DatabaseReference postRef = databaseReference.child(postId);
        postRef.child("description").setValue(description);
        if (imageUri != null) {
            String base64Image = encodeImageToBase64(imageUri);
            if (base64Image != null) {
                postRef.child("imageBase64").setValue(base64Image);
            }
        }
        postRef.child("date").setValue(java.text.DateFormat.getDateTimeInstance().format(new java.util.Date()))
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        Toast.makeText(getActivity(), "Пост успешно обновлён!", Toast.LENGTH_SHORT).show();
                        dismiss();
                    } else {
                        Toast.makeText(getActivity(), "Не удалось обновить пост", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == AppCompatActivity.RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            imagePreview.setImageURI(selectedImageUri);
            imagePreview.setVisibility(View.VISIBLE);
        }
    }

    private void savePost(String description, Uri imageUri) {
        progressBar.setVisibility(View.VISIBLE);

        String base64Image = encodeImageToBase64(imageUri);

        if (base64Image != null) {
            int id = ((int) (System.currentTimeMillis() % 10000000000L));
            if (String.valueOf(id).length() < 10) {
                id += 1000000000;
            }
            String currentDate = java.text.DateFormat.getDateTimeInstance().format(new java.util.Date());
            Post post = new Post(String.valueOf(id), description, base64Image, currentDate);
            databaseReference.child(String.valueOf(id)).setValue(post)
                    .addOnCompleteListener(task -> {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(), "Пост успешно сохранён!", Toast.LENGTH_SHORT).show();
                            dismiss();
                        } else {
                            Toast.makeText(getActivity(), "Не удалось сохранить пост", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(getActivity(), "Не удалось сохранить пост", Toast.LENGTH_SHORT).show();
        }
    }

    private String encodeImageToBase64(Uri imageUri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
       
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            float aspectRatio = (float) width / (float) height;
            int newWidth = 800;
            int newHeight = Math.round(newWidth / aspectRatio);
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            scaledBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] imageBytes = baos.toByteArray();
            return Base64.encodeToString(imageBytes, Base64.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "Ошибка при загрузке изображения", Toast.LENGTH_SHORT).show();
        }
        return null;
    }
}
