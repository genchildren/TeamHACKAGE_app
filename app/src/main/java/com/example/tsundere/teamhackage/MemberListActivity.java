package com.example.tsundere.teamhackage;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import java.util.ArrayList;

//Активити отображающая список участников команды в виде RecyclerView наполненного элементами CardView

public class MemberListActivity extends AppCompatActivity {

    final private int ADD_MEMBER_REQUEST_CODE = 1; //Код запроса на добавление нового участника
    private static ArrayList<Integer> members; //Список идентификаторов участников
    private RecyclerView rv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_list);
        members = new ArrayList<>();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Переход к активити добавления нового участника
                Intent addMemberIntent = new Intent(MemberListActivity.this, AddMemberActivity.class);
                startActivityForResult(addMemberIntent, ADD_MEMBER_REQUEST_CODE);
            }
        });
        //Получаем программный доступ к recyclerView, устанавливаем ему layoutManager
        rv = findViewById(R.id.rv);
        rv.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(layoutManager);
    }

    //Обновляем экран с участниками на основе информации в базе данных
    @Override
    protected void onStart() {
        super.onStart();
        ReadableDBHelper readableDBHelper = new ReadableDBHelper(this); //Получаем доступ к базе данных для чтения
        members = readableDBHelper.getIdList(); //Получаем из базы данных список идентификаторов участников
        //Создаем и устанавливаем адаптер на основе списка идентификаторов
        CustomRecyclerAdapter adapter = new CustomRecyclerAdapter(members);
        rv.setAdapter(adapter);
        readableDBHelper.close();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_MEMBER_REQUEST_CODE && resultCode == RESULT_OK) { //Если мы получили успешный ответ от активити добавления нового пользователя
            members.add(data.getIntExtra("member_id", 0)); //Добавляем нового пользователя в список
        }
    }
}
