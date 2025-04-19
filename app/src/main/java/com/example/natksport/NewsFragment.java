package com.example.natksport;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class NewsFragment extends Fragment implements RoleAwareFragment {

    private DatabaseReference databaseReference;
    private RecyclerView recyclerView;
    private NewsAdapter newsAdapter;
    private List<Post> newsList;
    private String userRole;

    public NewsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);

        databaseReference = FirebaseDatabase.getInstance().getReference("Novosti");
        recyclerView = view.findViewById(R.id.recycler_view_news);
        newsList = new ArrayList<>();
        newsAdapter = new NewsAdapter(getActivity(), newsList, userRole);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(newsAdapter);

        loadNews();


        Button addButton = view.findViewById(R.id.button_add_news);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewPostDialog newPostDialog = new NewPostDialog();
                newPostDialog.show(getChildFragmentManager(), "NewPostDialog");
            }
        });

        updateButtonVisibility(addButton);

        return view;
    }

    private void updateButtonVisibility(Button addButton) {
        if ("Пользователь".equals(userRole)) {
            addButton.setVisibility(View.GONE);
        } else {
            addButton.setVisibility(View.VISIBLE);
        }
    }

    private void loadNews() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                newsList.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Post post = postSnapshot.getValue(Post.class);
                    if (post != null) {
                        newsList.add(post);
                    }
                }
                newsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Ошибка загрузки новостей", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void setUserRole(String role) {
        this.userRole = role;
    }
}
