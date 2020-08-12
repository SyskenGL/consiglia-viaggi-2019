package it.isw.cvoffice.dao.concrete.lambda;

import it.isw.cvoffice.dao.NotificationDAO;
import it.isw.cvoffice.dao.concrete.lambda.exceptions.OperatorNotAuthenticatedException;
import it.isw.cvoffice.models.Operator;
import it.isw.cvoffice.utils.aws.lambda.LambdaInvoker;
import it.isw.cvoffice.utils.aws.lambda.Payload;
import it.isw.cvoffice.utils.aws.lambda.callbacks.LambdaResultHandler;
import it.isw.cvoffice.utils.aws.lambda.enumerations.PayloadType;


public class NotificationDAOLambda implements NotificationDAO {

    @Override
    public void postNotification(String reviewId,
                                 String userId,
                                 String title,
                                 String message,
                                 String type,
                                 Object resultHandler) {
        if(!Operator.isLoggedIn()) {
            throw new OperatorNotAuthenticatedException("operator is not authenticated");
        }
        Payload payload = new Payload(PayloadType.POST_NOTIFICATION);
        payload.setValue("review_id", reviewId);
        payload.setValue("user_id", userId);
        payload.setValue("title", title);
        payload.setValue("message", message);
        payload.setValue("type", type);
        LambdaInvoker lambdaInvoker = new LambdaInvoker(Operator.getInstance().getCredentials());
        lambdaInvoker.invoke(payload, (LambdaResultHandler) resultHandler);
    }

}