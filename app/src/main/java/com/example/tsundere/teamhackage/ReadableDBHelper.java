package com.example.tsundere.teamhackage;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 *  Класс {@link ReadableDBHelper} для получение информации из БД
 *  @version 1
 */
public class ReadableDBHelper {
    /** Поле курсор */
    private Cursor cursor;
    /** Поле БД для получения информации */
    private SQLiteDatabase dbForRead;

    /**
     *  Конструктор класса. Открывает БД для чтения и создаёт курсор с полями участника по полученому ID
     * @param context - контекст
     * @param id - ID участника
     */
    public ReadableDBHelper(Context context, int id) {
        SQLiteOpenHelper db = new DBHelper(context);
        dbForRead = db.getReadableDatabase(); //  -- Открытие БД для чтения --
        cursor = dbForRead.query(DBHelper.TABLE_NAME, null,  "_id = ?", new String[] {Integer.toString(id)}, null, null, null); //  -- Запрос в БД --
        cursor.moveToFirst(); //  -- Установка курсора на первое поле --
    }

    /**
     *  Конструктор класса. Открывает БД для чтения
     * @param context - контекст
     */
    public ReadableDBHelper(Context context) {
        SQLiteOpenHelper db = new DBHelper(context);
        dbForRead = db.getReadableDatabase();
    }

    /**
     *  Метод для получения курсора с ID всех участников
     * @return возвращает курсор с полями, содержащими ID всех участников
     */
    private Cursor getMembersCursor() {
        return dbForRead.query(DBHelper.TABLE_NAME, new String[] {"_id"},
                null, null, null, null, null);
    }

    /**
     *  Метод для получения списка с ID всех участников
     * @return возвращает список содержащий ID всех участников
     */
    public ArrayList<Integer> getIdList() {
        ArrayList<Integer> res = new ArrayList<>();
        Cursor cursor = getMembersCursor(); //  -- Получение курсора с ID всех участников
        if (cursor.moveToFirst()) { //  -- Установка курсора на первое поле --
            do {
                int id = cursor.getInt(0); //  -- Получение ID участника --
                res.add(id); //  -- Сохранение ID в списке --
            } while (cursor.moveToNext()); //  -- Переход к следующему полю, если оно существует --
        }
        return res;
    }

    /**
     *  Метод для получения ID участника
     * @return возвращает ID участника
     */
    public int getMemberID() {
        return cursor.getInt(0);
    }

    /**
     *  Метод для получения имени участника
     * @return возвращает имя участника
     */
    public String getMemberFirstName() {
        return cursor.getString(1); }

    /**
     *  Метод для получения фамилии участника
     * @return возвращает фамилию участника
     */
    public String getMemberSecondName() {
        return cursor.getString(2);
    }

    /**
     *  Метод для получения отчества участника
     * @return возвращает отчество участника
     */
    public String getMemberPatronymic() {
        return cursor.getString(3);
    }

    /**
     *  Метод для получения группы участника
     * @return возвращает группу участника
     */
    public String getMemberGroup() {
        return cursor.getString(4);
    }

    /**
     *  Метод для получения информации об участнике
     * @return возвращает информацию об участнике
     */
    public String getMemberAboutMe() {
        return cursor.getString(5);
    }

    /**
     *  Метод для получения фото участника
     * @return возвращает фото участника
     */
    public byte[] getImageArray() {
        return cursor.getBlob(6);
    }

    /**
     *  Метод для прекращения чтения БД
     */
    public void close() {
        dbForRead.close();
    }
}
