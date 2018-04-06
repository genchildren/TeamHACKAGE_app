package com.example.tsundere.teamhackage;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/* Активити для добавления нового участника команды */

public class AddMemberActivity extends AppCompatActivity {

    final private int RESULT_LOAD_IMG = 665; //Код для передачи управления галерее при получении аватарки участника команды
    private byte[] imgByteArray; // Байтовый массив хранящий аватарку участника в пригодном для хранения в базе данных виде

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member);
        try {
            imgByteArray = ImageDecoder.bitmapToByteArray(BitmapFactory.decodeStream(this.getAssets().open("default_avatar.png")));
        } catch (IOException e) {
            Log.e("DefaultImageError", "DEFAULT IMAGE NOT FOUND ERROR");
        }
    }

    public void addCancelOnClick(View view) {
        onBackPressed();
    }

    public void addApplyOnClick(View view) {
        EditText firstNameEditText = findViewById(R.id.firstName),
                 lastNameEditText = findViewById(R.id.lastName),
                 fatherNameEditText = findViewById(R.id.fatherName),
                 groupEditText = findViewById(R.id.groupTitle),
                 aboutEditText = findViewById(R.id.aboutTitle);

        // Получение значений полей введенных пользователем
        String firstName = firstNameEditText.getText().toString(),
               lastName = lastNameEditText.getText().toString(),
               fatherName = fatherNameEditText.getText().toString(),
               group = groupEditText.getText().toString(),
               about = aboutEditText.getText().toString();


        if (checkInputData(firstName, lastName, fatherName, group, about)) { //Если данные удовлетворяют условиям

            WritableDBHelper database = new WritableDBHelper(this); //Получаем доступ к Writable базе данных для добавления нового участника
            long id = database.addMember(firstName, lastName, fatherName, group, about, imgByteArray); //Добавление участника в базу данных и получение его идентификатора

            Toast.makeText(this, "Новый участник успешно добавлен", Toast.LENGTH_SHORT).show();

            Intent returnDataIntent = new Intent();

            returnDataIntent.putExtra("member_id", id); //В возвращаемых значениях сохраняем идентификатор добавленного участника
            database.close();

            setResult(RESULT_OK, returnDataIntent);
            finish();

        } else { //Если данные не удовлетворяют требованиям
            Toast.makeText(
                    AddMemberActivity.this,
                    "Ошибка добавления нового участника, проверьте введенные данные и попробуйте снова",
                    Toast.LENGTH_SHORT).show();
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

    //Произвести запрос к галерее на получение изображения
    public void getImageOnClick(View view) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
    }

    //Получение результата от галереи
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) { //Если запрос был успешно обработан
            try {
                //Получаем и сжимаем изображение
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
                Toast.makeText(this, "Не удалось загрузить изображение", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Toast.makeText(this, "Ошибка закрытия потока вывода", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
