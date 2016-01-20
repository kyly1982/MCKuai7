package com.tars.mcdao;


import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class MCDaoGenerator {
    public static void main(String[] args) throws Exception {
        // 创建一个用于添加实体（Entity）的模式（Schema）对象
        // 两个参数分别代表：数据库版本号与自动生成代码的包路径
        Schema schema = new Schema(1, "com.mckuai.imc");
        //也可以分别指定生成的 Bean 与 DAO 类所在的目录，只要如下所示
        //Schema schema = new Schema(1, "me.itangqi.bean");
        //schema.setDefaultJavaPackageDao("me.itangqi.dao");
        //模式（Schema）同时也拥有两个默认的 flags，分别用来标示 entity 是否是 activie 以及是否使用 keep sections
        //schema2.enableActiveEntitiesByDefault();
        //schema2.enableKeepSectionsByDefault();
        //拥有了一个 Schema 对象后，你便可以使用它添加实体（Entities）了.
        addTables(schema);
        new DaoGenerator().generateAll(schema, "F:\\Repository\\MCKuai7\\app\\src\\main\\java-gen");
    }


    private static void addTables(Schema schema) {
        Entity user, token, type, forum;
        token = addTokenTable(schema);
        type = addTypeTable(schema);
        user = addUserTable(schema);
        forum = addForumTable(schema);
        addMap(token, user, type, forum);
    }

    private static void addMap(Entity token, Entity user, Entity type, Entity forum) {

    }

    private static Entity addUserTable(Schema schema) {
        Entity user = schema.addEntity("User");
        user.setTableName("User");
        user.addIdProperty().primaryKey();//id
        user.addIntProperty("score").columnName("score");//积分
        user.addIntProperty("isServerActor").columnName("isServerActor");// 是否是腐竹或者名人 1:腐竹 2:名人 3:腐竹申请 4:名人申请
        user.addIntProperty("postCount").columnName("postCount");// 发帖数
        user.addIntProperty("chatRoomNum").columnName("chatRoomNum");//小屋数
        user.addIntProperty("dynamicNum").columnName("dynamicNum");//动态数
        user.addIntProperty("messageNum").columnName("messageNum");// 消息数
        user.addIntProperty("workNum").columnName("workNum");// 作品数
        user.addIntProperty("level").columnName("level");// 当前等级
        user.addIntProperty("token").columnName("token");//tokenId
        user.addFloatProperty("process").columnName("process");// 等级积分进度

        user.addStringProperty("name").columnName("name").notNull();// 登录后的openid
        user.addStringProperty("nickName").columnName("nickName");//昵称，显示用
        user.addStringProperty("cover").columnName("cover");// 头像
        user.addStringProperty("gender").columnName("gender");// 性别
        user.addStringProperty("city").columnName("city");// 定位
        return user;
    }

    private static Entity addTokenTable(Schema schema) {
        Entity token = schema.addEntity("Token");
        token.setTableName("Token");
        token.addIdProperty().primaryKey();//id
        token.addIntProperty("type").columnName("type");//类型，0为自有,1为QQ，2为微信
        token.addLongProperty("birthday").columnName("birthday");//token生成日期
        token.addLongProperty("expires").columnName("expires");//token过期日期
        token.addStringProperty("token").columnName("token").notNull();//token
        return token;
    }

    private static Entity addTypeTable(Schema schema) {
        Entity type = schema.addEntity("Type");
        type.setTableName("Type");
        type.addIdProperty().primaryKey();//id
        type.addIntProperty("subId").columnName("subId");//子id
        type.addStringProperty("name").columnName("name").notNull();//名称
        return type;
    }

    private static Entity addForumTable(Schema schema) {
        Entity forum = schema.addEntity("Forum");
        forum.setTableName("Forum");
        forum.addIdProperty().primaryKey();//id
        forum.addIntProperty("postCount").columnName("postCount");//帖子总数
        forum.addStringProperty("name").columnName("name");//版块名称
        forum.addStringProperty("cover").columnName("cover");//封面
        forum.addStringProperty("type").columnName("type");//子类id集合
        return forum;
    }


}
