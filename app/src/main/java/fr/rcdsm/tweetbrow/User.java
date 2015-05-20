package fr.rcdsm.tweetbrow;

/**
 * Created by rcdsm on 27/04/15.
 */
public class User {

    private String token;
    private String login;

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
        return INSTANCE.token;
    }

    public void setToken(String token) {
        INSTANCE.token = token;
    }

    public String getLogin() {
        return INSTANCE.login;
    }

    public void setLogin(String login) {
        INSTANCE.login = login;
    }

    public boolean isNotConnected(){
        return INSTANCE.getToken() == null;
    }

    public void disconnect(){
        INSTANCE.setToken(null);
        INSTANCE.setLogin(null);
    }
}
