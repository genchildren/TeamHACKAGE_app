package com.example.tsundere.teamhackage;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class EditMemberActivity extends AppCompatActivity {
    int id;
    TextView firstName, lastName, fatherName, group, about;
    byte[] imgByteArray;
    final private int RESULT_LOAD_IMG = 665; //Код для передачи управления галерее при получении аватарки участника команды

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_member);
        //Получаем и устанавливаем информацию об участнике которая на данный момент лежит в базе
        firstName = findViewById(R.id.firstNameEdit);
        lastName = findViewById(R.id.lastNameEdit);
        fatherName = findViewById(R.id.fatherNameEdit);
        group = findViewById(R.id.groupEdit);
        about = findViewById(R.id.aboutEdit);

        id = getIntent().getIntExtra("member_id", 0);
        ReadableDBHelper db = new ReadableDBHelper(this, id); //Получаем доступ к базе данных для чтения информации о конкретном участнике

        firstName.setText(db.getMemberFirstName());
        lastName.setText(db.getMemberSecondName());
        fatherName.setText(db.getMemberPatronymic());
        group.setText(db.getMemberGroup());
        about.setText(db.getMemberAboutMe());
        imgByteArray = db.getImageArray();
        db.close();

    }

    //Произвести запрос к галерее на получение изображение
    public void uploadImageOnClick(View view) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
    }

    //Получение результата от галереи
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            try { //Получаем и сжимаем изображение

                Uri imgUri = data.getData();
                InputStream imageStream = getContentResolver().openInputStream(imgUri);

                BitmapFactory.Options bmFactoryOptions = new BitmapFactory.Options();
                bmFactoryOptions.inJustDecodeBounds = true;

                BitmapFactory.decodeStream(imageStream, null, bmFactoryOptions); //Получаем характеристики изображения
                imageStream.close();

                int maxDimSize = Math.max(bmFactoryOptions.outWidth, bmFactoryOptions.outHeight);
                int scale = Math.max(1, Math.round((float)maxDimSize / (float)1280)); //Подсчитываем коэффициент сжатия которое необходимо произвести над изображением

                bmFactoryOptions = new BitmapFactory.Options();
                bmFactoryOptions.inSampleSize = scale; //Устанавливаем коэффициент сжатия
                bmFactoryOptions.inPreferredConfig = Bitmap.Config.RGB_565; //Устанавливаем цветовой конфиг

                imageStream = getContentResolver().openInputStream(imgUri);

                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream, null, bmFactoryOptions); //Получаем изображение в сжатом виде в качестве Bitmap объекта
                imgByteArray = ImageDecoder.bitmapToByteArray(selectedImage); //Преобразуем изображение в байтовый массив и сохраняем в приватном поле

            } catch (FileNotFoundException e) {
                Toast.makeText(this, "Не удалось загрузить фото", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Toast.makeText(this, "Ошибка закрытия потока вывода", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean checkName(String name) { //Проверка что строка состоит только из русских и латинских букв
        for (int i = 0; i<name.length(); ++i) {
            char curChar = Character.toLowerCase(name.charAt(i));
            if ((curChar < 'a' || curChar > 'z') && (curChar < 'а' || curChar > 'я')) {
                return false;
            }
        }
        return true;
    }

    //Проверяем введенные пользователем имя, фамилию, отчество, группу на корректность
    private boolean checkInputData(String firstName, String lastName, String fatherName, String group, String about) {
        return !firstName.isEmpty() && !lastName.isEmpty() &&
                !fatherName.isEmpty() && !group.isEmpty() && !about.isEmpty() &&
                checkName(firstName) && checkName(lastName) && checkName(fatherName);
    }

    public void saveEditOnClick(View view) { // Сохранение внесенных изменений об участнике в базе данных
        WritableDBHelper writableDB = new WritableDBHelper(this);

        String firstNameValue = firstName.getText().toString(),
                lastNameValue = lastName.getText().toString(),
                fatherNameValue = fatherName.getText().toString(),
                groupValue = group.getText().toString(),
                aboutValue = about.getText().toString();

        if (checkInputData(firstNameValue, lastNameValue, fatherNameValue, groupValue, aboutValue)) {

            writableDB.updateMember(id, firstName.getText().toString(), lastName.getText().toString(),
                    fatherName.getText().toString(), group.getText().toString(), about.getText().toString(), imgByteArray);

            writableDB.close();
            setResult(RESULT_OK);
            finish();
        } else { //Если данные не удовлетворяют требованиям
            Toast.makeText(
                    EditMemberActivity.this,
                    "Ошибка редактирования участника, проверьте введенные данные и попробуйте снова",
                    Toast.LENGTH_SHORT).show();
        }
    }

    //Отмена редактирования
    public void rejectEditOnClick(View view) {
        onBackPressed();
    }

    public void deleteMemberOnClick(View view) { //Удаление участника с вызовом AlertDialog

        AlertDialog.Builder ad = new AlertDialog.Builder(this);
        ad.setTitle("Удаление участника");
        ad.setMessage("Вы действительно хотите удалить участника?");
        final Context context = ad.getContext();
        ad.setPositiveButton("Да", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                WritableDBHelper writableDB = new WritableDBHelper(context);
                writableDB.deleteMember(id);
                Intent toMemberListIntent = new Intent(context, MemberListActivity.class);
                Toast.makeText(context, "Участник был успешно удален", Toast.LENGTH_SHORT).show();
                writableDB.close();
                startActivity(toMemberListIntent);
            }
        });
        ad.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {}
        });
        ad.setCancelable(true);
        ad.show();
    }
}
