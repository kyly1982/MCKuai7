package com.mckuai.imc.Util.MCDao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.mckuai.imc.Bean.User;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;


// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "Users".
*/
public class UserDao extends AbstractDao<User, Long> {

    public static final String TABLENAME = "Users";

    /**
     * Properties of entity User.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Level = new Property(1, Integer.class, "level", false, "level");
        public final static Property Process = new Property(2, Float.class, "process", false, "process");
        public final static Property Name = new Property(3, String.class, "name", false, "name");
        public final static Property Nick = new Property(4, String.class, "nick", false, "nick");
        public final static Property Cover = new Property(5, String.class, "cover", false, "cover");
        public final static Property IsFriend = new Property(6, Boolean.class, "isFriend", false, "isFriend");
    };


    public UserDao(DaoConfig config) {
        super(config);
    }
    
    public UserDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"Users\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: id
                "\"level\" INTEGER," + // 1: level
                "\"process\" REAL," + // 2: process
                "\"name\" TEXT NOT NULL ," + // 3: name
                "\"nick\" TEXT," + // 4: nick
                "\"cover\" TEXT," + // 5: cover
                "\"isFriend\" INTEGER);"); // 6: isFriend
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"Users\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, User entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        Integer level = entity.getLevel();
        if (level != null) {
            stmt.bindLong(2, level);
        }
 
        Float process = entity.getProcess();
        if (process != null) {
            stmt.bindDouble(3, process);
        }
        stmt.bindString(4, entity.getName());
 
        String nick = entity.getNick();
        if (nick != null) {
            stmt.bindString(5, nick);
        }
 
        String cover = entity.getHeadImage();
        if (cover != null) {
            stmt.bindString(6, cover);
        }
 
        Boolean isFriend = entity.getIsFriend();
        if (isFriend != null) {
            stmt.bindLong(7, isFriend ? 1L: 0L);
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
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getInt(offset + 1), // level
            cursor.isNull(offset + 2) ? null : cursor.getFloat(offset + 2), // process
            cursor.getString(offset + 3), // name
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // nick
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // cover
            cursor.isNull(offset + 6) ? null : cursor.getShort(offset + 6) != 0 // isFriend
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, User entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setLevel(cursor.isNull(offset + 1) ? null : cursor.getInt(offset + 1));
        entity.setProcess(cursor.isNull(offset + 2) ? null : cursor.getFloat(offset + 2));
        entity.setName(cursor.getString(offset + 3));
        entity.setNick(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setHeadImage(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setIsFriend(cursor.isNull(offset + 6) ? null : cursor.getShort(offset + 6) != 0);
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(User entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(User entity) {
        if(entity != null) {
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
