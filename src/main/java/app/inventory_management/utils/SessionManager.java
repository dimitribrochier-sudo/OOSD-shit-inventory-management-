package app.inventory_management.utils;

import app.inventory_management.models.User;

//this allows teh dashboard to not take an object and basically keeps teh user logged in with
public class SessionManager {
    private static SessionManager instance;
    private User currentUser;

    //a private constructor for singleton
    private SessionManager(){}

    //Synchronized prevents multipleinstances simulatneaously (spaming login)
    public static synchronized SessionManager getInstance(){
        if(instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    //for login creates the user
    public void login(User user) {
        this.currentUser = user;
    }

    //for getting a user
    public User getCurrentUser() {
        return currentUser;
    }

    //returns null if no user logged thus in dashboard this will force user to go back to login
    public boolean isLoggedIn() {
        return currentUser != null;
    }

    //returns null and so dashboard is sent to a login and currentUser is null
    public void logout() {
        this.currentUser = null;
    }

}
