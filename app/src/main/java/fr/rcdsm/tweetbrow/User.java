package fr.rcdsm.tweetbrow;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by rcdsm on 27/04/15.
 */
public class User extends RealmObject {

    @PrimaryKey
    private long id;

    private String token;

    private String pseudo;
    private String login;

    private boolean followed;

    private static User INSTANCE = null;

    public static User getInstance(){
        if(INSTANCE == null){
            INSTANCE = new User();
        }
        return INSTANCE;
    }

    public User(){
    }

    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getLogin() {
        return this.login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public boolean isFollowed() {
        return followed;
    }

    public void setFollowed(boolean followed) {
        this.followed = followed;
    }
}


