package com.blackbrick.wecare.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blackbrick.wecare.R;
import com.blackbrick.wecare.adapter.PostRecyclerAdapter;
import com.blackbrick.wecare.classes.post;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;


/**
 * A simple {@link Fragment} subclass.
 **/
public class BooksFragment extends Fragment {

    private RecyclerView recyclerView;
    private List<post> postList;

    private FirebaseFirestore firebaseFirestore;
    private PostRecyclerAdapter postRecyclerAdapter;

    public BooksFragment() {
        // Required empty public constructor

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_book, container,false);

        postList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.book_recycler_view);
        postRecyclerAdapter = new PostRecyclerAdapter(postList);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(postRecyclerAdapter);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("Posts").document("books Posts").collection("books").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                try {
                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                        if (doc.getType() == DocumentChange.Type.ADDED) {
                            post post = doc.getDocument().toObject(post.class);
                            postList.add(post);

                            postRecyclerAdapter.notifyDataSetChanged();
                        }
                    }
                } catch (Exception exception) {

                }
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

}
