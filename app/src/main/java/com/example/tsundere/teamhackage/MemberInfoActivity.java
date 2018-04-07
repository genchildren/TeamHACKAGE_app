package com.example.tsundere.teamhackage;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

//Активити для отображения подробной информации об участнике

public class MemberInfoActivity extends AppCompatActivity {

    private TextView fullName, about, group;
    private ImageView memberPhoto;
    private int id;
    final private int TO_EDIT_ACTIVITY_REQUEST = 123;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_info);

        FloatingActionButton fab = findViewById(R.id.fab);
        id = getIntent().getIntExtra("member_id", 0); //Получаем идентификатор участника, информацию о котором мы хотим отобразить
        ReadableDBHelper database = new ReadableDBHelper(this, id); //Получаем доступ к базе данных для чтения

        //Получаем и отображаем информацию в полях
        fullName = findViewById(R.id.fullName);
        about = findViewById(R.id.aboutInfo);
        group = findViewById(R.id.group);
        memberPhoto = findViewById(R.id.memberPhotoBig);

        String name = database.getMemberFirstName() + " " + database.getMemberSecondName() + " " + database.getMemberPatronymic();
        fullName.setText(name);
        about.setText(database.getMemberAboutMe());
        group.setText(database.getMemberGroup());
        memberPhoto.setImageBitmap(ImageDecoder.bitmapFromByteArrayHighQ(database.getImageArray()));
        database.close();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Запуск активити редактирования информации об участнике
                Intent toEditActivityIntent = new Intent(MemberInfoActivity.this, EditMemberActivity.class);
                toEditActivityIntent.putExtra("member_id", id);
                startActivityForResult(toEditActivityIntent,TO_EDIT_ACTIVITY_REQUEST);
            }
        });

    }

    //Получение результата выполнения активити редактирования участника
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TO_EDIT_ACTIVITY_REQUEST) {
            if (data!=null && data.getExtras() != null && data.getExtras().containsKey("delete")) {
                finish();
            } else if (resultCode == RESULT_OK) {
                ReadableDBHelper database = new ReadableDBHelper(this, id);
                byte[] imageByteArray = database.getImageArray();
                Bitmap imageBitmap = ImageDecoder.bitmapFromByteArrayHighQ(imageByteArray);
                memberPhoto.setImageBitmap(imageBitmap);
                String name = database.getMemberFirstName() + " " + database.getMemberSecondName() + " " + database.getMemberPatronymic();
                fullName.setText(name);
                about.setText(database.getMemberAboutMe());
                group.setText(database.getMemberGroup());
                database.close();
            }
        }
    } 

}

