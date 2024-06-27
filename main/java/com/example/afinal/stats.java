package com.example.afinal;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link stats#newInstance} factory method to
 * create an instance of this fragment.
 */
public class stats extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public stats() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment stats.
     */
    // TODO: Rename and change types and number of parameters
    public static stats newInstance(String param1, String param2) {
        stats fragment = new stats();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stats, container, false);

        BarChart barChart = view.findViewById(R.id.lineChart);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Button exit = view.findViewById(R.id.exitStats);

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).ToMyHome();
            }
        });

        String username = ((MainActivity) getActivity()).getUser();
        CollectionReference transactionsRef = db.collection("transactions").document(username)
                .collection("user_transactions");

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
                                    ((MainActivity) getActivity()).setBudget(Double.parseDouble(existingBudget));
                                }
                            } else {
                                Log.e("checkIfBudgetExists", "User document not found");
                            }
                        } else {
                            Log.e("checkIfBudgetExists", "Error querying user document", task.getException());
                        }
                    }
                });

        transactionsRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<BarEntry> entries = new ArrayList<>();
                List<BarEntry> budgetEntries = new ArrayList<>();
                List<BarEntry> totalEntries = new ArrayList<>();
                ArrayList<String> labels = new ArrayList<>();
                double Total = 0.0;

                double budgetValue = ((MainActivity) getActivity()).getBudget();
                budgetEntries.add(new BarEntry(entries.size(), (float) budgetValue));
                labels.add("Budget");

                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    String amountStr = documentSnapshot.getString("amount");
                    try {
                        double amount = Double.parseDouble(amountStr);
                        String transactionType = documentSnapshot.getString("type");
                        Date date = documentSnapshot.getDate("date");

                        String dateString = date.toString();

                        if (transactionType.equals("Deposit")) {
                            amount *= -1;
                        }

                        Total += amount;

                        entries.add(new BarEntry(entries.size()+1, (float) amount));
                        labels.add(dateString);
                    } catch (NumberFormatException e) {
                        Log.e("Firestore", "Failed to parse amount: " + amountStr, e);
                    }
                }

                totalEntries.add(new BarEntry(entries.size()+1, (float) Total));
                labels.add("Total");

                BarDataSet budgetDataSet = new BarDataSet(budgetEntries, "Budget");
                budgetDataSet.setColor(Color.YELLOW);
                budgetDataSet.setValueTextSize(20f);

                BarDataSet dataSet = new BarDataSet(entries, "Transactions");
                dataSet.setValueTextSize(20f);

                BarDataSet totalDataSet = new BarDataSet(totalEntries, "Total");
                totalDataSet.setValueTextSize(20f);
                if (budgetValue < Total) {
                    totalDataSet.setColor(Color.RED);
                } else {
                    totalDataSet.setColor(Color.GREEN);
                }

                BarData barData = new BarData(budgetDataSet, dataSet, totalDataSet);

                XAxis xAxis = barChart.getXAxis();
                xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
                xAxis.setDrawGridLines(false);
                xAxis.setGranularity(1f);
                xAxis.setLabelCount(labels.size());

                barChart.setData(barData);
                barChart.setVisibleXRangeMaximum(2);
                barChart.invalidate();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("statsFragment", "Error fetching data: " + e.getMessage());
                Toast.makeText(getContext(), "Failed to fetch data", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}