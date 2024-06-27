package com.example.afinal;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class transactions extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public transactions() {
        // Required empty public constructor
    }

    public static transactions newInstance(String param1, String param2) {
        transactions fragment = new transactions();
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
        View view = inflater.inflate(R.layout.fragment_transactions, container, false);

        Spinner spin = view.findViewById(R.id.transactionsOptions);
        Button exit = view.findViewById(R.id.exitTransactions);
        CalendarView cal = view.findViewById(R.id.calendarView);
        EditText amount = view.findViewById(R.id.amount);
        EditText name = view.findViewById(R.id.name);
        Button save = view.findViewById(R.id.saveTransaction);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        setSpinner(spin, exit);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTransaction(db, spin, cal, amount.getText().toString(), name.getText().toString());
            }
        });

        return view;
    }

    private void setSpinner(Spinner spin, Button exit){
        String[] transactionTypes = new String[] {
                "Choose a type:",
                "Withdrawal",
                "Deposit"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_item,
                transactionTypes
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(adapter);

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).ToMyHome();
            }
        });
    }

    private void saveTransaction(FirebaseFirestore db, Spinner spin, CalendarView cal, String amount, String name) {
        String transactionType = spin.getSelectedItem().toString();

        if (transactionType.equals("Choose a type:")) {
            Toast.makeText(getContext(), "Please select a transaction type", Toast.LENGTH_SHORT).show();
            return;
        }

        Date transactionDate = new Date(cal.getDate());

        if (amount.isEmpty()) {
            Toast.makeText(getContext(), "Please enter an amount", Toast.LENGTH_SHORT).show();
            return;
        }

        if (name.isEmpty()) {
            Toast.makeText(getContext(), "Please enter a name", Toast.LENGTH_SHORT).show();
            return;
        }

        String currentUser = ((MainActivity)getActivity()).getUser();

        Map<String, Object> transactionData = new HashMap<>();
        transactionData.put("type", transactionType);
        transactionData.put("date", transactionDate);
        transactionData.put("amount", amount);

        CollectionReference userTransactionsRef = db.collection("transactions")
                .document(currentUser)
                .collection("user_transactions");

        userTransactionsRef.document(name)
                .set(transactionData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(), "Transaction saved successfully", Toast.LENGTH_SHORT).show();
                        ((MainActivity)getActivity()).ToMyHome();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Failed to save transaction", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
