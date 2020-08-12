package it.isw.cvoffice.utils.aws.cognito;

import software.amazon.awssdk.regions.Region;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public abstract class Cognito {

    public static final String USER_POOL_ID;
    public static final String IDENTITY_POOL_ID;
    public static final String CLIENT_ID;
    public static final String CLIENT_SECRET;
    public static final String PROVIDER = "cognito-idp.%s.amazonaws.com/%s";
    public static final Region REGION;

    private static final String CONFIG_FILE_NAME = "config/aws.properties";


    static {
        Properties properties = new Properties();
        InputStream inputStream = Cognito.class.getClassLoader().getResourceAsStream(CONFIG_FILE_NAME);
        if(inputStream != null) {
            try {
                properties.load(inputStream);
                USER_POOL_ID = properties.getProperty("userPoolId");
                IDENTITY_POOL_ID = properties.getProperty("identityPoolId");
                CLIENT_ID = properties.getProperty("clientId");
                CLIENT_SECRET = properties.getProperty("clientSecret");
                REGION = Region.of(properties.getProperty("region"));
                inputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(CONFIG_FILE_NAME + " loading failed");
            }
        } else {
            throw new RuntimeException(CONFIG_FILE_NAME + " file not found");
        }
    }

}