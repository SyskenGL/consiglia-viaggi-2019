const AWS = require('../node_modules/aws-sdk');


module.exports = {

    getCognitoIdentityProvider: function(params) {
        AWS.config.update({
            "accessKeyId": params.accessKeyId,
            "secretAccessKey": params.secretAccessKey,
            "region": params.region
        });
        return new AWS.CognitoIdentityServiceProvider();
    }

}