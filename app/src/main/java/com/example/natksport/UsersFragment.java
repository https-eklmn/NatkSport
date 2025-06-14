package com.example.natksport;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import java.util.stream.Collectors;

public class UsersFragment extends Fragment implements UserAdapter.OnUserClickListener {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> userList;
    private EditText editTextSearch;
    private Spinner spinnerRoles;

    public UsersFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_users, container, false);

        editTextSearch = view.findViewById(R.id.editTextSearch);
        spinnerRoles = view.findViewById(R.id.spinnerRoles);
        recyclerView = view.findViewById(R.id.recyclerViewUsers);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        userList = new ArrayList<>();
        userAdapter = new UserAdapter(userList, this);
        recyclerView.setAdapter(userAdapter);

        loadUsersFromFirebase();

        return view;
    }

    private void loadUsersFromFirebase() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {

                        int idUser = Integer.parseInt(snapshot.getKey());
                        user.setIDUser(idUser);
                        userList.add(user);
                    }
                }
                userAdapter.notifyDataSetChanged();
                setupSearchAndSort();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setupSearchAndSort() {
        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterUsers(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.roles_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRoles.setAdapter(adapter);

        spinnerRoles.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                sortUsersByRole(parentView.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }
        });
    }

    private void filterUsers(String text) {
        List<User> filteredList = userList.stream()
                .filter(user -> user.getFamilia().toLowerCase().contains(text.toLowerCase()) ||
                        user.getImia().toLowerCase().contains(text.toLowerCase()) ||
                        user.getOtchestvo().toLowerCase().contains(text.toLowerCase()))
                .collect(Collectors.toList());
        userAdapter = new UserAdapter(filteredList, this);
        recyclerView.setAdapter(userAdapter);
    }

    private void sortUsersByRole(String role) {
        List<User> sortedList;
        if (role.equals("Все")) {
            sortedList = new ArrayList<>(userList);
        } else {
            sortedList = userList.stream()
                    .filter(user -> user.getNaimenovanieRoli().equals(role))
                    .collect(Collectors.toList());
        }
        userAdapter = new UserAdapter(sortedList, this);
        recyclerView.setAdapter(userAdapter);
    }

    @Override
    public void onEditClick(User user) {
        showEditDialog(user);
    }

    @Override
    public void onDeleteClick(User user) {
        deleteUser(user);
    }

    private void showEditDialog(User user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_user, null);
        builder.setView(dialogView);

        EditText editTextFamilia = dialogView.findViewById(R.id.editTextFamilia);
        EditText editTextImia = dialogView.findViewById(R.id.editTextImia);
        EditText editTextOtchestvo = dialogView.findViewById(R.id.editTextOtchestvo);
        EditText editTextLogin = dialogView.findViewById(R.id.editTextLogin);
        EditText editTextPochta = dialogView.findViewById(R.id.editTextPochta);
        Spinner spinnerRoles = dialogView.findViewById(R.id.spinnerRolesEdit);

        editTextFamilia.setText(user.getFamilia());
        editTextImia.setText(user.getImia());
        editTextOtchestvo.setText(user.getOtchestvo());
        editTextLogin.setText(user.getLogin());
        editTextPochta.setText(user.getPochta());

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.roles_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRoles.setAdapter(adapter);
        spinnerRoles.setSelection(adapter.getPosition(user.getNaimenovanieRoli()));


        AlertDialog dialog = builder.create();


        Button buttonSave = dialogView.findViewById(R.id.buttonSave);
        Button buttonCancel = dialogView.findViewById(R.id.buttonCancel);

        buttonSave.setOnClickListener(view -> {

            user.setFamilia(editTextFamilia.getText().toString());
            user.setImia(editTextImia.getText().toString());
            user.setOtchestvo(editTextOtchestvo.getText().toString());
            user.setLogin(editTextLogin.getText().toString());
            user.setPochta(editTextPochta.getText().toString());
            user.setNaimenovanieRoli(spinnerRoles.getSelectedItem().toString());

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User").child(String.valueOf(user.getIDUser()));
            databaseReference.setValue(user);
            dialog.dismiss();
        });

        buttonCancel.setOnClickListener(view -> {

            dialog.dismiss();
        });

        dialog.show();
    }

    private void deleteUser(User user) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User").child(String.valueOf(user.getIDUser()));
        databaseReference.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "Пользователь удалён", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Ошибка удаления пользователя", Toast.LENGTH_SHORT).show();
            }
        });
    }
}