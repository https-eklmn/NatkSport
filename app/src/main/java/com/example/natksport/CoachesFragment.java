package com.example.natksport;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class CoachesFragment extends Fragment {

    private RecyclerView recyclerView;
    private CoachesAdapter coachesAdapter;
    private List<Coach> coachList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_coaches, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_coache);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        coachList = new ArrayList<>();
        coachesAdapter = new CoachesAdapter(coachList);
        recyclerView.setAdapter(coachesAdapter);

        fetchCoachesFromFirebase();

        return view;
    }

    private void fetchCoachesFromFirebase() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Trener");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                coachList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Coach coac = snapshot.getValue(Coach.class);
                    coachList.add(coac);
                }
                coachesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}