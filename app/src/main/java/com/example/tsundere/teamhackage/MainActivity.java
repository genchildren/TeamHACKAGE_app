package com.example.tsundere.teamhackage;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

//Активити главного экрана приложения

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    //Переход к активити со списком участников
    public void toMemberListOnClickListener(View view) {
        Intent toMemberListIntent = new Intent(MainActivity.this, MemberListActivity.class);
        startActivity(toMemberListIntent);
    }

    //Запуск активити отображающей информацию о команде
    public void toAboutTeamOnClickListener(View view) {
        Intent toAboutIntent = new Intent(MainActivity.this, AboutTeamActivity.class);
        startActivity(toAboutIntent);
    }
}
