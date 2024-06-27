package com.example.afinal;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link my_home#newInstance} factory method to
 * create an instance of this fragment.
 */
public class my_home extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public my_home() {
        // Required empty public constructor
    }

    public static my_home newInstance(String param1, String param2) {
        my_home fragment = new my_home();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_my_home, container, false);

        TextView mName = (TextView) view.findViewById(R.id.showU);
        Button logout = (Button) view.findViewById(R.id.logOut);
        Button toTransactions = (Button) view.findViewById(R.id.toTransactions);
        Button toStats = (Button) view.findViewById(R.id.toStats);
        EditText budget = (EditText) view.findViewById(R.id.budget);
        Button saveBudget = (Button) view.findViewById(R.id.saveBudget);

        mName.setText("Hello, " + ((MainActivity)getActivity()).getUser().toString());

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).ToSignUp();
            }
        });

        toTransactions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).ToTransactions();
            }
        });

        toStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).toStats();
            }
        });

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String username = ((MainActivity)getActivity()).getUser().toString();

        checkIfBudgetExists(db, username, budget);

        saveBudget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String budgetValue = budget.getText().toString();
                if (!budgetValue.isEmpty()) {
                    saveBudgetToFirestore(db, username, budgetValue);
                } else {
                    Toast.makeText(getActivity(), "Please enter a budget", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }
    public void checkIfBudgetExists(FirebaseFirestore db, String username, EditText budget) {
        db.collection("users")
                .whereEqualTo("username", username)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (!task.getResult().isEmpty()) {
                                DocumentSnapshot document = task.getResult().getDocuments().get(0);
                                if (document.contains("budget")) {
                                    String existingBudget = document.getString("budget");
                                    ((MainActivity)getActivity()).setBudget(Double.parseDouble(existingBudget));
                                    budget.setText(existingBudget);
                                }
                            } else {
                                Log.e("checkIfBudgetExists", "User document not found");
                            }
                        } else {
                            Log.e("checkIfBudgetExists", "Error querying user document", task.getException());
                        }
                    }
                });
    }
    private void saveBudgetToFirestore(FirebaseFirestore db, String username, String budgetValue) {
        db.collection("users")
                .whereEqualTo("username", username)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (!task.getResult().isEmpty()) {
                                DocumentSnapshot document = task.getResult().getDocuments().get(0);
                                DocumentReference userRef = document.getReference();

                                userRef.update("budget", budgetValue)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(getActivity(), "Budget updated successfully", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getActivity(), "Error updating budget", Toast.LENGTH_SHORT).show();
                                                Log.e("my_home", "Error updating budget", e);
                                            }
                                        });
                            } else {
                                Toast.makeText(getActivity(), "User document not found", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.e("my_home", "Error querying user document", task.getException());
                        }
                    }
                });
    }
}

