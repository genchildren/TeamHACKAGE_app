package com.example.tsundere.teamhackage;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class EditMemberActivity extends AppCompatActivity {
    int id;
    TextView firstName, lastName, fatherName, group, about;
    byte[] imgByteArray;
    final private int RESULT_LOAD_IMG = 1; //Код идетифицирующий передачу управления галерее при получении аватарки участника команды
    final private int RESULT_TAKE_PICTURE = 2; //Код идетифицирующий передачу управления камере при получении аватарки участника команды
    final private String PROVIDER_AUTHORITY = "com.example.tsundere.teamhackage.provider"; //Идентифицируем FileProvider
    final private String MEMBER_ID = "member_id";

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

        id = getIntent().getIntExtra(MEMBER_ID, 0);
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
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(EditMemberActivity.this);
        String[] arr = new String[] {"Choose from gallery", "Take a picture"};
        builder.setTitle("Photo")
                .setItems(arr, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0) {
                            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                            photoPickerIntent.setType("image/*");
                            startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
                        } else {
                            File path = new File(getFilesDir(), ".");
                            if (!path.exists()) path.mkdirs();
                            File image = new File(path, "hackage_avatar.jpg");
                            Uri imageUri = FileProvider.getUriForFile(EditMemberActivity.this, PROVIDER_AUTHORITY, image);
                            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                            startActivityForResult(takePictureIntent, RESULT_TAKE_PICTURE);
                        }
                    }
                });
        builder.create();
        builder.show();
    }

    //Получение результата от галереи
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            try {
                if (requestCode == RESULT_LOAD_IMG) { //Если получаем результат от галереи
                    //Получаем и сжимаем изображение
                    Uri imgUri = data.getData();
                    InputStream imageStream = getContentResolver().openInputStream(imgUri);

                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;

                    BitmapFactory.decodeStream(imageStream, null, options); //Получаем характеристики изображения

                    imageStream.close();

                    int maxDimSize = Math.max(options.outWidth, options.outHeight);
                    int scale = Math.max(1, Math.round((float) maxDimSize / (float) 1280)); //Подсчитываем коэффициент сжатия которое необходимо произвести над изображением

                    options = new BitmapFactory.Options();
                    options.inSampleSize = scale; //Устанавливаем коэффициент сжатия
                    options.inPreferredConfig = Bitmap.Config.RGB_565; //Устанавливаем цветовой конфиг

                    imageStream = getContentResolver().openInputStream(imgUri);

                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream, null, options); //Получаем изображение в сжатом виде в качестве Bitmap объекта
                    imgByteArray = ImageDecoder.bitmapToByteArray(selectedImage); //Преобразуем изображение в байтовый массив и сохраняем в приватном поле

                } else { //Если получаем результат от камеры
                    File path = new File(getFilesDir(), ".");
                    File imageFile = new File(path, "hackage_avatar.jpg"); //Находим полученную фотографию

                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;

                    BitmapFactory.decodeFile(imageFile.getAbsolutePath(),options);

                    int maxDimSize = Math.max(options.outWidth, options.outHeight);
                    int scale = Math.max(1, Math.round((float) maxDimSize / (float) 1280));

                    options = new BitmapFactory.Options();
                    options.inSampleSize = scale;
                    options.inPreferredConfig = Bitmap.Config.RGB_565;

                    Bitmap imageBitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options); //Декодируем фотографию в соответствии с настройками сжатия
                    imgByteArray = ImageDecoder.bitmapToByteArray(imageBitmap);
                }

            } catch (FileNotFoundException e) {
                Toast.makeText(this, "Can't upload photo", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Toast.makeText(this, "Output stream closing error", Toast.LENGTH_SHORT).show();
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
                    "Can't edit member, please check that your input data is correct and try again",
                    Toast.LENGTH_SHORT).show();
        }
    }

    //Отмена редактирования
    public void rejectEditOnClick(View view) {
        onBackPressed();
    }

    public void deleteMemberOnClick(View view) { //Удаление участника с вызовом AlertDialog

        AlertDialog.Builder ad = new AlertDialog.Builder(this);
        ad.setTitle("Delete member");
        ad.setMessage("Are you sure that you want to delete this member?");
        final Context context = ad.getContext();
        ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                WritableDBHelper writableDB = new WritableDBHelper(context);
                writableDB.deleteMember(id);
                Intent toMemberListIntent = new Intent(context, MemberListActivity.class);
                Toast.makeText(context, "Member deleted successfully", Toast.LENGTH_SHORT).show();
                writableDB.close();
                toMemberListIntent.putExtra("delete", true);
                setResult(RESULT_OK, toMemberListIntent);
                finish();
            }
        });
        ad.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {}
        });
        ad.setCancelable(true);
        ad.show();
    }
}
