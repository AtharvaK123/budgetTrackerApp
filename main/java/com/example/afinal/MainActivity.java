package com.example.afinal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    FragmentContainerView fCV;
    String user;
    double budget;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fCV = findViewById(R.id.fragmentContainerView);
        getSupportFragmentManager().beginTransaction().replace(R.id.MainActivity, new signUp()).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();

        user = null;
        budget = 0.0;
    }

    public void ToLogIn(){
        getSupportFragmentManager().beginTransaction().replace(R.id.MainActivity, new logIn()).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
    }
    public void ToSignUp(){
        getSupportFragmentManager().beginTransaction().replace(R.id.MainActivity, new signUp()).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
    }
    public void ToMyHome(){
        getSupportFragmentManager().beginTransaction().replace(R.id.MainActivity, new my_home()).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
    }
    public void ToTransactions(){
        getSupportFragmentManager().beginTransaction().replace(R.id.MainActivity, new transactions()).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
    }
    public void toStats(){
        getSupportFragmentManager().beginTransaction().replace(R.id.MainActivity, new stats()).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
    }
    public void setUser(String username){
        user = username;
    }
    public String getUser(){
        return user;
    }
    public void setBudget(double username){
        budget = username;
    }
    public double getBudget(){
        return budget;
    }
}