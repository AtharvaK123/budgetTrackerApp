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
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link logIn#newInstance} factory method to
 * create an instance of this fragment.
 */
public class logIn extends Fragment {

    // Fragment initialization parameters
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public logIn() {
        // Required empty public constructor
    }

    public static logIn newInstance(String param1, String param2) {
        logIn fragment = new logIn();
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
        View view = inflater.inflate(R.layout.fragment_log_in, container, false);

        TextView mName = view.findViewById(R.id.goToLogIn);
        mName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).ToSignUp();
            }
        });

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        EditText uName = view.findViewById(R.id.username1);
        EditText password = view.findViewById(R.id.password1);
        Button register = view.findViewById(R.id.login);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = uName.getText().toString().trim();
                String pass = password.getText().toString().trim();

                if (userName.isEmpty() || pass.isEmpty()) {
                    Toast.makeText(getActivity(), "Please enter both username and password", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check if username and password match
                Query query = db.collection("users").whereEqualTo("username", userName);
                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            boolean userFound = false;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String dbPassword = document.getString("password");
                                if (dbPassword != null && dbPassword.equals(pass)) {
                                    userFound = true;
                                    ((MainActivity)getActivity()).ToMyHome();
                                    ((MainActivity)getActivity()).setUser(userName);
                                    break;
                                }
                            }
                            if (!userFound) {
                                Toast.makeText(getActivity(), "Invalid Username or Password.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.w("onClick", "Error checking username", task.getException());
                            Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        return view;
    }
}
