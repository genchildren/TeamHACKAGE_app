package com.example.tsundere.teamhackage;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;


/* Активити для добавления нового участника команды */

public class AddMemberActivity extends AppCompatActivity {

    final private int RESULT_TAKE_PICTURE = 2; //Код идетифицирующий передачу управления камере при получении аватарки участника команды
    final private int RESULT_LOAD_IMG = 1; //Код идентифицирующий передачу управления галерее при получении аватарки участника команды
    final private String PROVIDER_AUTHORITY = "com.example.tsundere.teamhackage.provider"; //Идентифицируем FileProvider
    private byte[] imgByteArray; // Байтовый массив хранящий аватарку участника в пригодном для хранения в базе данных виде
    final private String MEMBER_ID = "member_id";

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

            Toast.makeText(this, "New member added successfully", Toast.LENGTH_SHORT).show();

            Intent returnDataIntent = new Intent();

            returnDataIntent.putExtra("MEMBER_ID", id); //В возвращаемых значениях сохраняем идентификатор добавленного участника
            database.close();

            setResult(RESULT_OK, returnDataIntent);
            finish();

        } else { //Если данные не удовлетворяют требованиям
            Toast.makeText(
                    AddMemberActivity.this,
                    "Can't add new member, please check that your input data is correct and try again",
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
        AlertDialog.Builder builder = new AlertDialog.Builder(AddMemberActivity.this);
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
                                Uri imageUri = FileProvider.getUriForFile(AddMemberActivity.this, PROVIDER_AUTHORITY, image);
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
        if (resultCode == RESULT_OK) { //Если запрос был успешно обработан
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

                } else { //Получаем результат от камеры

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
                Toast.makeText(this, "Can't upload image: image not found", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Toast.makeText(this, "Error: Can't close output stream", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
