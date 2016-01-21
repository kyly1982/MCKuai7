package com.mckuai.imc;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/**
 * DAO for table "User".
 */
public class UserDao extends AbstractDao<User, Long> {

    public static final String TABLENAME = "User";

    /**
     * Properties of entity User.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Integer.class, "id", true, "_id");
        public final static Property Score = new Property(1, Integer.class, "score", false, "score");
        public final static Property IsServerActor = new Property(2, Integer.class, "isServerActor", false, "isServerActor");
        public final static Property PostCount = new Property(3, Integer.class, "postCount", false, "postCount");
        public final static Property ChatRoomNum = new Property(4, Integer.class, "chatRoomNum", false, "chatRoomNum");
        public final static Property DynamicNum = new Property(5, Integer.class, "dynamicNum", false, "dynamicNum");
        public final static Property MessageNum = new Property(6, Integer.class, "messageNum", false, "messageNum");
        public final static Property WorkNum = new Property(7, Integer.class, "workNum", false, "workNum");
        public final static Property Level = new Property(8, Integer.class, "level", false, "level");
        public final static Property Token = new Property(9, Integer.class, "token", false, "token");
        public final static Property Process = new Property(10, Float.class, "process", false, "process");
        public final static Property Name = new Property(11, String.class, "name", false, "name");
        public final static Property NickName = new Property(12, String.class, "nickName", false, "nickName");
        public final static Property Cover = new Property(13, String.class, "cover", false, "cover");
        public final static Property Gender = new Property(14, String.class, "gender", false, "gender");
        public final static Property City = new Property(15, String.class, "city", false, "city");
    }


    public UserDao(DaoConfig config) {
        super(config);
    }

    public UserDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /**
     * Creates the underlying database table.
     */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists ? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"User\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: id
                "\"score\" INTEGER," + // 1: score
                "\"isServerActor\" INTEGER," + // 2: isServerActor
                "\"postCount\" INTEGER," + // 3: postCount
                "\"chatRoomNum\" INTEGER," + // 4: chatRoomNum
                "\"dynamicNum\" INTEGER," + // 5: dynamicNum
                "\"messageNum\" INTEGER," + // 6: messageNum
                "\"workNum\" INTEGER," + // 7: workNum
                "\"level\" INTEGER," + // 8: level
                "\"token\" INTEGER," + // 9: token
                "\"process\" REAL," + // 10: process
                "\"name\" TEXT NOT NULL ," + // 11: name
                "\"nickName\" TEXT," + // 12: nickName
                "\"cover\" TEXT," + // 13: cover
                "\"gender\" TEXT," + // 14: gender
                "\"city\" TEXT);"); // 15: city
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"User\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, User entity) {
        stmt.clearBindings();

        int id = entity.getId();
        if (id != 0) {
            stmt.bindLong(1, id);
        }

        Integer score = entity.getScore();
        if (score != null) {
            stmt.bindLong(2, score);
        }

        Integer isServerActor = entity.getIsServerActor();
        if (isServerActor != null) {
            stmt.bindLong(3, isServerActor);
        }

        Integer postCount = entity.getPostCount();
        if (postCount != null) {
            stmt.bindLong(4, postCount);
        }

        Integer chatRoomNum = entity.getChatRoomNum();
        if (chatRoomNum != null) {
            stmt.bindLong(5, chatRoomNum);
        }

        Integer dynamicNum = entity.getDynamicNum();
        if (dynamicNum != null) {
            stmt.bindLong(6, dynamicNum);
        }

        Integer messageNum = entity.getMessageNum();
        if (messageNum != null) {
            stmt.bindLong(7, messageNum);
        }

        Integer workNum = entity.getWorkNum();
        if (workNum != null) {
            stmt.bindLong(8, workNum);
        }

        Integer level = entity.getLevel();
        if (level != null) {
            stmt.bindLong(9, level);
        }

        Integer token = entity.getToken();
        if (token != null) {
            stmt.bindLong(10, token);
        }

        Float process = entity.getProcess();
        if (process != null) {
            stmt.bindDouble(11, process);
        }
        stmt.bindString(12, entity.getName());

        String nickName = entity.getNickName();
        if (nickName != null) {
            stmt.bindString(13, nickName);
        }

        String cover = entity.getCover();
        if (cover != null) {
            stmt.bindString(14, cover);
        }

        String gender = entity.getGender();
        if (gender != null) {
            stmt.bindString(15, gender);
        }

        String city = entity.getCity();
        if (city != null) {
            stmt.bindString(16, city);
        }
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }

    /** @inheritdoc */
    @Override
    public User readEntity(Cursor cursor, int offset) {
        User entity = new User( //
                cursor.isNull(offset + 0) ? null : cursor.getInt(offset + 0), // id
                cursor.isNull(offset + 1) ? null : cursor.getInt(offset + 1), // score
                cursor.isNull(offset + 2) ? null : cursor.getInt(offset + 2), // isServerActor
                cursor.isNull(offset + 3) ? null : cursor.getInt(offset + 3), // postCount
                cursor.isNull(offset + 4) ? null : cursor.getInt(offset + 4), // chatRoomNum
                cursor.isNull(offset + 5) ? null : cursor.getInt(offset + 5), // dynamicNum
                cursor.isNull(offset + 6) ? null : cursor.getInt(offset + 6), // messageNum
                cursor.isNull(offset + 7) ? null : cursor.getInt(offset + 7), // workNum
                cursor.isNull(offset + 8) ? null : cursor.getInt(offset + 8), // level
                cursor.isNull(offset + 9) ? null : cursor.getInt(offset + 9), // token
                cursor.isNull(offset + 10) ? null : cursor.getFloat(offset + 10), // process
                cursor.getString(offset + 11), // name
                cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12), // nickName
                cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13), // cover
                cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14), // gender
                cursor.isNull(offset + 15) ? null : cursor.getString(offset + 15) // city
        );
        return entity;
    }

    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, User entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getInt(offset + 0));
        entity.setScore(cursor.isNull(offset + 1) ? null : cursor.getInt(offset + 1));
        entity.setIsServerActor(cursor.isNull(offset + 2) ? null : cursor.getInt(offset + 2));
        entity.setPostCount(cursor.isNull(offset + 3) ? null : cursor.getInt(offset + 3));
        entity.setChatRoomNum(cursor.isNull(offset + 4) ? null : cursor.getInt(offset + 4));
        entity.setDynamicNum(cursor.isNull(offset + 5) ? null : cursor.getInt(offset + 5));
        entity.setMessageNum(cursor.isNull(offset + 6) ? null : cursor.getInt(offset + 6));
        entity.setWorkNum(cursor.isNull(offset + 7) ? null : cursor.getInt(offset + 7));
        entity.setLevel(cursor.isNull(offset + 8) ? null : cursor.getInt(offset + 8));
        entity.setToken(cursor.isNull(offset + 9) ? null : cursor.getInt(offset + 9));
        entity.setProcess(cursor.isNull(offset + 10) ? null : cursor.getFloat(offset + 10));
        entity.setName(cursor.getString(offset + 11));
        entity.setNickName(cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12));
        entity.setCover(cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13));
        entity.setGender(cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14));
        entity.setCity(cursor.isNull(offset + 15) ? null : cursor.getString(offset + 15));
    }

    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(User entity, long rowId) {
        entity.setId((int) rowId);
        return rowId;
    }


    /** @inheritdoc */
    @Override
    public Long getKey(User entity) {
        if (entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override
    protected boolean isEntityUpdateable() {
        return true;
    }

}
