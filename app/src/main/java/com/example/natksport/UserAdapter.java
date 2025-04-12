package com.example.natksport;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> userList;
    private OnUserClickListener onUserClickListener;

    public UserAdapter(List<User> userList, OnUserClickListener onUserClickListener) {
        this.userList = userList;
        this.onUserClickListener = onUserClickListener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.textViewFullName.setText(user.getFamilia() + " " + user.getImia() + " " + user.getOtchestvo());
        holder.textViewLogin.setText("Логин: " + user.getLogin());
        holder.textViewEmail.setText("E-mail: " + user.getPochta());
        holder.textViewRole.setText("Роль: " + user.getNaimenovanieRoli());

        holder.buttonEdit.setOnClickListener(v -> onUserClickListener.onEditClick(user));
        holder.buttonDelete.setOnClickListener(v -> onUserClickListener.onDeleteClick(user));
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewFullName, textViewLogin, textViewRole, textViewEmail;
        public Button buttonEdit, buttonDelete;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewFullName = itemView.findViewById(R.id.textViewFullName);
            textViewLogin = itemView.findViewById(R.id.textViewLogin);
            textViewRole = itemView.findViewById(R.id.textViewRole);
            textViewEmail = itemView.findViewById(R.id.textViewEmail);
            buttonEdit = itemView.findViewById(R.id.buttonEdit);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
        }
    }

    public interface OnUserClickListener {
        void onEditClick(User user);
        void onDeleteClick(User user);
    }
}