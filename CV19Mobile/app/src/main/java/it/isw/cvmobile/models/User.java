package it.isw.cvmobile.models;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import javax.inject.Singleton;
import it.isw.cvmobile.utils.annotations.Completed;


@Completed
@Singleton
public class User {

    private static User user;
    private final String userId;
    private final String nickname;
    private final String givenName;
    private final String familyName;
    private final String email;
    private final String creationDate;
    private final String picture;
    private final CognitoUserSession cognitoUserSession;
    private String preferredUsername;
    private int totalReviews;
    private int totalFavorites;
    private static boolean loggedIn;



    private User(String userId,
                String nickname,
                String givenName,
                String familyName,
                String email,
                String creationDate,
                String picture,
                CognitoUserSession cognitoUserSession,
                String preferredUsername,
                int totalReviews,
                int totalFavorites) {
        this.userId = userId;
        this.nickname = nickname;
        this.givenName = givenName;
        this.familyName = familyName;
        this.email = email;
        this.creationDate = creationDate;
        this.picture = picture;
        this.cognitoUserSession = cognitoUserSession;
        this.preferredUsername = preferredUsername;
        this.totalReviews = totalReviews;
        this.totalFavorites = totalFavorites;
    }

    public static void initializeUser(String userId,
                                      String nickname,
                                      String givenName,
                                      String familyName,
                                      String email,
                                      String creationDate,
                                      String picture,
                                      CognitoUserSession cognitoUserSession,
                                      String preferredUsername,
                                      int totalReviews,
                                      int totalFavorites) {
        if(user == null) {
            user = new User(
                    userId,
                    nickname,
                    givenName,
                    familyName,
                    email,
                    creationDate,
                    picture,
                    cognitoUserSession,
                    preferredUsername,
                    totalReviews,
                    totalFavorites
            );
        }
    }

    public static void finalizeUser() {
        user = null;
        loggedIn = false;
    }

    public void setPreferredUsername(String preferredUsername) {
        this.preferredUsername = preferredUsername;
    }

    public void setTotalReviews(int totalReviews) {
        this.totalReviews = totalReviews;
    }

    public void setTotalFavorites(int totalFavorites) {
        this.totalFavorites = totalFavorites;
    }

    public static void setLoggedIn(boolean loggedIn) {
        User.loggedIn = loggedIn;
    }

    public String getUserId() {
        return userId;
    }

    public String getNickname() {
        return nickname;
    }

    public String getGivenName() {
        return givenName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public String getEmail() {
        return email;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public String getPicture() {
        return picture;
    }

    public CognitoUserSession getCognitoUserSession() {
        return cognitoUserSession;
    }

    public String getPreferredUsername() {
        return preferredUsername;
    }

    public int getTotalReviews() {
        return totalReviews;
    }

    public int getTotalFavorites() {
        return totalFavorites;
    }

    public static boolean isLoggedIn() {
        return loggedIn;
    }

    public static User getInstance() {
        return user;
    }

}




