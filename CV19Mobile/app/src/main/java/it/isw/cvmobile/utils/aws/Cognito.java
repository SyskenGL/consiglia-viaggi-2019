package it.isw.cvmobile.utils.aws;

import android.content.Context;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Singleton;
import it.isw.cvmobile.utils.annotations.Completed;


@Completed
@Singleton
public class Cognito {


    public final static String USER_POOL_ID = "USER_POOL_ID";
    public final static String IDENTITY_POOL_ID = "IDENTITY_POOL_ID";
    public final static String CLIENT_ID = "CLIENT_ID";
    public final static String CLIENT_SECRET = null;
    public final static Regions REGION = Regions.EU_WEST_2;

    private static Cognito cognito;
    private final CognitoUserPool cognitoUserPool;
    private final CognitoCachingCredentialsProvider cognitoCachingCredentialsProvider;



    private Cognito(Context context) {
        cognitoUserPool = new CognitoUserPool(
                context,
                USER_POOL_ID,
                CLIENT_ID,
                CLIENT_SECRET,
                REGION
        );
        cognitoCachingCredentialsProvider = new CognitoCachingCredentialsProvider(
                context,
                IDENTITY_POOL_ID,
                REGION
        );
    }

    public CognitoUserPool getCognitoUserPool() {
        return cognitoUserPool;
    }

    public CognitoCachingCredentialsProvider getCognitoCachingCredentialsProvider() {
        return cognitoCachingCredentialsProvider;
    }

    public static Cognito getInstance(Context context) {
        if(cognito != null)
            return cognito;
        return (cognito = new Cognito(context));
    }

    public void setLogin(CognitoUserSession session) {
        cognitoCachingCredentialsProvider.clear();
        Map<String,String> login = new HashMap<>();
        login.put(
                "cognito-idp." + Region.getRegion(REGION) + ".amazonaws.com/" + USER_POOL_ID,
                session.getIdToken().getJWTToken()
        );
        cognitoCachingCredentialsProvider.setLogins(login);
    }

}