package com.mckuai.imc.Util;

import com.mckuai.imc.Base.MCKuai;
import com.mckuai.imc.Bean.ForumInfo;
import com.mckuai.imc.Bean.MCUser;
import com.mckuai.imc.Forum;
import com.mckuai.imc.ForumDao;
import com.mckuai.imc.Token;
import com.mckuai.imc.TokenDao;
import com.mckuai.imc.Type;
import com.mckuai.imc.TypeDao;
import com.mckuai.imc.User;
import com.mckuai.imc.UserDao;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * Created by kyly on 2016/1/21.
 */
public class MCDaoHelper {
    /**
     * 向数据库中添加版块 <BR>
     * 如果板块已存在，则更新它 <BR>
     *
     * @param forumInfos 要添加的版块
     */
    public static void addForums(ArrayList<ForumInfo> forumInfos) {
        if (null != forumInfos && !forumInfos.isEmpty()) {
            ForumDao dao = MCKuai.instence.getDaoSession().getForumDao();
            for (ForumInfo forumInfo : forumInfos) {
                Forum forum = new Forum(forumInfo);
                dao.insertOrReplace(forum);
                //添加帖子类型
                addTypes(forumInfo.getIncludeType());
            }
        }
    }

    /**
     * 获取所有版块 <BR>
     *
     * @return 返回数据库中所保存的所有版块。如果没有则返回null
     */
    public static ArrayList<ForumInfo> getAllForums() {
        ForumDao dao = MCKuai.instence.getDaoSession().getForumDao();
        List<Forum> forums = dao.loadAll();
        if (null != forums && 0 < forums.size()) {
            ArrayList<ForumInfo> forumInfos = new ArrayList<>(forums.size());
            for (Forum forum : forums) {
                ForumInfo forumInfo = forum.toForumInfo();
                forumInfo.setIncludeType(getTypes(forum.getType().split(",")));//添加帖子类型
                forumInfos.add(forumInfo);
            }
            return forumInfos;
        }
        return null;
    }

    /**
     * 向数据库中添加新类型 <BR>
     * 如果数据库中已存在(id相同)，则更新它 <BR>
     *
     * @param types 要添加的类型列表
     */
    public static void addTypes(ArrayList<Type> types) {
        if (null != types && !types.isEmpty()) {
            TypeDao dao = MCKuai.instence.getDaoSession().getTypeDao();
            for (Type type : types) {
                dao.insertOrReplace(type);
            }
        }
    }

    /**
     * 从数据库中获取指定id的类型
     *
     * @param typeIds 类型id数组
     * @return 指定id的数组，如果没有则为null
     */
    public static ArrayList<Type> getTypes(String[] typeIds) {
        if (null != typeIds && 0 < typeIds.length) {
            TypeDao dao = MCKuai.instence.getDaoSession().getTypeDao();
            QueryBuilder<Type> qb = dao.queryBuilder();
            qb.where(TypeDao.Properties.Id.in(typeIds));
            return (ArrayList<Type>) qb.list();
        }
        return null;
    }

    /**
     * 添加其它用户,主要是给融云用 <BR>
     * 如果用户已存在，则更新其信息 <BR>
     *
     * @param mcUser 要添加的用户信息
     */
    public static void addUser(MCUser mcUser) {
        if (null != mcUser) {
            UserDao dao = MCKuai.instence.getDaoSession().getUserDao();
            dao.insertOrReplace(new User(mcUser));
        }
    }

    /**
     * 添加本地登录用户,需要更新其token <BR>
     *
     * @param mcUser  登录用户信息 <BR>
     * @param expires 有效期时长,如果为0,则为临时登录，不保存其token
     */
    public static void addLoginUser(MCUser mcUser, long expires) {
        if (null != mcUser) {
            User user = new User(mcUser);
            //添加token
            if (0 != expires) {
                long id = addToken(mcUser.getId(), mcUser.getToken(), 3, expires);
                user.setToken(id);
            }
            UserDao dao = MCKuai.instence.getDaoSession().getUserDao();
            dao.insertOrReplace(user);
        }
    }

    /**
     * 从数据库中获取拥有指定id的用户 <BR>
     *
     * @param userId
     * @return
     */
    public static MCUser getUser(int userId) {

        UserDao dao = MCKuai.instence.getDaoSession().getUserDao();
        de.greenrobot.dao.query.QueryBuilder<User> qb = dao.queryBuilder();
        qb.where(UserDao.Properties.Id.eq(userId));
        List<User> userList = qb.list();
        if (null != userList && 1 == userList.size() && null != userList.get(0)) {
            User user = userList.get(0);
            MCUser mcUser = userList.get(0).toMCUser();
            String token = getTokenByTokenId(user.getToken()).getToken();
            mcUser.setToken(token);
            return mcUser;
        } else {
            return null;
        }
    }

    /**
     * 向数据库中添加token <BR>
     * 如果此token已存在，则更新它 <BR>
     *
     * @param token token对象
     */
    private static void addToken(Token token) {
        TokenDao dao = MCKuai.instence.getDaoSession().getTokenDao();
        dao.insertOrReplace(token);
    }

    /**
     * 向数据库中添加token <BR>
     * 如果此token已存在，则更新它 <BR>
     *
     * @param userId   userId<BR>
     * @param tokenStr token<BR>
     * @param type     类型，0:自有;1:QQ;2:微信;3:融云<BR>
     * @param expires  截止有效期<BR>
     */
    private static long addToken(int userId, String tokenStr, int type, long expires) {
        TokenDao dao = MCKuai.instence.getDaoSession().getTokenDao();
        Token token;
        long time = System.currentTimeMillis();
        time = (time - time % 1000) / 1000;
        //检查token是否已存在
        QueryBuilder qb = dao.queryBuilder();
        qb.where(TokenDao.Properties.Token.eq(tokenStr), TokenDao.Properties.UserId.eq(userId), TokenDao.Properties.Token.eq(type));
        List<Token> tokens = qb.list();
        if (null != tokens && 1 == tokens.size()) {
            //已经存在了，更新其内容
            token = tokens.get(0);
            token.setBirthday(time - 1);
            token.setExpires(expires);
        } else {
            //不存在，构造一个新token
            token = new Token(type, time - 1, expires, tokenStr);
        }

        return dao.insertOrReplace(token);
    }

    private static ArrayList<Token> getTokenByUserId(int tokenId) {
        TokenDao dao = MCKuai.instence.getDaoSession().getTokenDao();
        QueryBuilder qb = dao.queryBuilder();
        qb.where(TokenDao.Properties.UserId.eq(tokenId));
        return (ArrayList<Token>) qb.list();
    }

    /**
     * 从数据库中获取指定的token <BR>
     *
     * @param id 要获取的token的ID
     * @return
     */
    public static Token getTokenByTokenId(Long id) {
        if (0 < id) {
            Token token = null;
            TokenDao dao = MCKuai.instence.getDaoSession().getTokenDao();
            de.greenrobot.dao.query.QueryBuilder<Token> qb = dao.queryBuilder();
            qb.where(TokenDao.Properties.Id.eq(id));
            return token;
        }

        return null;
    }
}
