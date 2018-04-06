package com.example.tsundere.teamhackage;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;

public class DBHelper extends SQLiteOpenHelper {
    private static final String LOG_TAG = DBHelper.class.getSimpleName();

    /**
     *  Константа, содержащая версию БД
     */
    private static final int DB_VERSION = 1;

    /**
     *  Константа, содержащая название файла БД
     */
    private static final String DB_NAME = "members_info.db";

    /**
     *  Константы с названиями полей таблицы БД
     */
    public static final String TABLE_NAME = "members";
    public static final String FIRST_NAME = "firstName";
    public static final String SECOND_NAME = "secondName";
    public static final String PATRONYMIC = "patronymic";
    public static final String GROUP = "educationalGroup";
    public static final String ABOUT_ME = "aboutMe";
    public static final String PHOTO_BYTE_ARRAY = "photo";

    private Context context;


    /**
     *  -- Конструктор класса {@link DBHelper}
     * @param context - контекст
     */
    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    /**
     *  Создание БД, если её ещё нет
     * @param db - БД
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +  //  -- Индивидуальный ID участника --
                FIRST_NAME + " TEXT NOT NULL," +            //  -- Имя участника --
                SECOND_NAME + " TEXT NOT NULL," +           //  -- Фамилия участника --
                PATRONYMIC + " TEXT NOT NULL," +            //  -- Отчество участника --
                GROUP + " TEXT NOT NULL," +                 //  -- Группа участника --
                ABOUT_ME + " TEXT NOT NULL," +              //  -- Информация об участнике --
                PHOTO_BYTE_ARRAY + " BLOB NOT NULL);");     //  -- Фото участника --


        //  -- Добавление в БД информации об участниках hackage --
        ContentValues cvVlP = new ContentValues();
        cvVlP.put(DBHelper.FIRST_NAME, "Владислав");
        cvVlP.put(DBHelper.SECOND_NAME, "Пинчук");
        cvVlP.put(DBHelper.PATRONYMIC, "Александрович");
        cvVlP.put(DBHelper.GROUP, "ИУ9-23Б");
        cvVlP.put(DBHelper.ABOUT_ME, "Тимлид который ничего не умеет");

        ContentValues cvDiB = new ContentValues();
        cvDiB.put(DBHelper.FIRST_NAME, "Дмитрий");
        cvDiB.put(DBHelper.SECOND_NAME, "Бокарев");
        cvDiB.put(DBHelper.PATRONYMIC, "Вячеславович");
        cvDiB.put(DBHelper.GROUP, "ИУ9-23Б");
        cvDiB.put(DBHelper.ABOUT_ME, "Says he`s going to help but he`s not");

        ContentValues cvKiS = new ContentValues();
        cvKiS.put(DBHelper.FIRST_NAME, "Кирилл");
        cvKiS.put(DBHelper.SECOND_NAME, "Снегур");
        cvKiS.put(DBHelper.PATRONYMIC, "Викторович");
        cvKiS.put(DBHelper.GROUP, "ИУ9-23Б");
        cvKiS.put(DBHelper.ABOUT_ME, "Has no idea what`s going on the whole time");

        ContentValues cvVaZ = new ContentValues();
        cvVaZ.put(DBHelper.FIRST_NAME, "Вадим");
        cvVaZ.put(DBHelper.SECOND_NAME, "Жданов");
        cvVaZ.put(DBHelper.PATRONYMIC, "Андреевич");
        cvVaZ.put(DBHelper.GROUP, "ИУ9-23Б");
        cvVaZ.put(DBHelper.ABOUT_ME, "Dissapear at the very beginning and doesn`t show up again til the very end");

        try { //  -- Получение фото --
            cvVlP.put(DBHelper.PHOTO_BYTE_ARRAY,
                    ImageDecoder.bitmapToByteArray(BitmapFactory.decodeStream(context.getAssets().open("vladik.jpg"))));
            cvDiB.put(DBHelper.PHOTO_BYTE_ARRAY,
                    ImageDecoder.bitmapToByteArray(BitmapFactory.decodeStream(context.getAssets().open("dima.jpg"))));
            cvKiS.put(DBHelper.PHOTO_BYTE_ARRAY,
                    ImageDecoder.bitmapToByteArray(BitmapFactory.decodeStream(context.getAssets().open("kirill.jpg"))));
            cvVaZ.put(DBHelper.PHOTO_BYTE_ARRAY,
                    ImageDecoder.bitmapToByteArray(BitmapFactory.decodeStream(context.getAssets().open("vadim.jpg"))));
        } catch(Exception e) {
            Toast.makeText(context, "Файл изображения не найден", Toast.LENGTH_SHORT).show();
        }
        db.insert(DBHelper.TABLE_NAME, null, cvVlP);
        db.insert(DBHelper.TABLE_NAME, null, cvDiB);
        db.insert(DBHelper.TABLE_NAME, null, cvKiS);
        db.insert(DBHelper.TABLE_NAME, null, cvVaZ);

    }

    /**
     *  Обновление БД
     * @param db - БД
     * @param oldVersion - старая версия
     * @param newVersion - новая версия
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}