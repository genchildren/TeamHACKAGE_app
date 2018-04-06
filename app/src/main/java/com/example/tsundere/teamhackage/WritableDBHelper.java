package com.example.tsundere.teamhackage;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 *  Класс {@link WritableDBHelper} для записи и изменения информации в БД
 *  @version 1
 */
public class WritableDBHelper {
    /** Поле БД для записи и изменения информации */
    private SQLiteDatabase dbForWrite;

    /**
     *  Конструктор класса. Открывает БД для записи и изменения информации
     * @param context - контекст
     */
    public WritableDBHelper (Context context) {
        SQLiteOpenHelper db = new DBHelper(context);
        dbForWrite = db.getWritableDatabase();
    }

    /**
     *  Метод для добавления участника
     * @param firstName - имя участника
     * @param secondName - фамилия участника
     * @param patronymic - отчество участника
     * @param group - группа участника
     * @param aboutMe - информация об участнике
     * @param photoByteArray - фото участника
     * @return возвращает ID нового участника
     */
    public long addMember(String firstName, String secondName, String patronymic, String group, String aboutMe, byte[] photoByteArray) {
        ContentValues cv = new ContentValues();
        cv.put(DBHelper.FIRST_NAME, firstName);
        cv.put(DBHelper.SECOND_NAME, secondName);
        cv.put(DBHelper.PATRONYMIC, patronymic);
        cv.put(DBHelper.GROUP, group);
        cv.put(DBHelper.ABOUT_ME, aboutMe);
        cv.put(DBHelper.PHOTO_BYTE_ARRAY, photoByteArray);
        return dbForWrite.insert(DBHelper.TABLE_NAME, null, cv); //  -- Добавление поля в базу данных --
    }

    /**
     *  Метод для обновления информации об участнике
     * @param id - ID участника
     * @param firstName - имя участника
     * @param secondName - фамилия участника
     * @param patronymic - отчество участника
     * @param group - группа участника
     * @param aboutMe - информация об участнике
     * @param photoByteArray - фото участника
     * @return возвращает количество изменённых полей
     */
    public int updateMember(int id, String firstName, String secondName, String patronymic, String group, String aboutMe, byte[] photoByteArray) {
        ContentValues cv = new ContentValues();
        cv.put(DBHelper.FIRST_NAME, firstName);
        cv.put(DBHelper.SECOND_NAME, secondName);
        cv.put(DBHelper.PATRONYMIC, patronymic);
        cv.put(DBHelper.GROUP, group);
        cv.put(DBHelper.ABOUT_ME, aboutMe);
        cv.put(DBHelper.PHOTO_BYTE_ARRAY, photoByteArray);
        return dbForWrite.update(DBHelper.TABLE_NAME, cv, "_id = ?", new String[] {Integer.toString(id)}); //  -- Обновление поля в БД --
    }

    /**
     *  Метод для удаления участника
     * @param id - ID участника
     * @return возвращает количество удалённых полей
     */
    public int deleteMember(int id) {
        return dbForWrite.delete(DBHelper.TABLE_NAME, "_id = ?", new String[] {Integer.toString(id)}); //  -- Удаление поля в БД --
    }

    /**
     * Метод для прекращения записи и изменения информации в БД
     */
    public void close() {
        dbForWrite.close();
    }
}
