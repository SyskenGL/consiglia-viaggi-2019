package it.isw.cvmobile.utils.aws;

import android.content.Context;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferNetworkLossHandler;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import java.io.File;
import it.isw.cvmobile.utils.annotations.Completed;
import it.isw.cvmobile.utils.annotations.RequiresInternetConnection;


@Completed
public class S3Uploader {

    public final static String BUCKET_S3_NAME = "BUCKET_S3_NAME";

    private final TransferUtility transferUtility;
    private final Context context;



    public S3Uploader(Context context) {
        this.context = context;
        Cognito cognito = Cognito.getInstance(context);
        AmazonS3Client s3Client = new AmazonS3Client(
                cognito.getCognitoCachingCredentialsProvider(),
                Region.getRegion(Cognito.REGION)
        );
        AWSConfiguration configuration = AWSMobileClient.getInstance().getConfiguration();
        transferUtility = TransferUtility.builder()
                .context(context)
                .awsConfiguration(configuration)
                .s3Client(s3Client).build();
    }

    @RequiresInternetConnection
    public void upload(String url,
                       final File fileToUpload,
                       CannedAccessControlList cannedAccessControlList,
                       final TransferListener transferListener) {
        TransferNetworkLossHandler.getInstance(context);
        TransferObserver transferObserver = transferUtility.upload(
                BUCKET_S3_NAME,
                url,
                fileToUpload,
                cannedAccessControlList
        );
        transferObserver.setTransferListener(transferListener);
    }

}