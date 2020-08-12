const CONFIG = require('./config/config.json');
const UTILITIES = require('./utils/utilities.js');



exports.handler = (event, context, callback) => {

    let cognitoIdentityProvider = UTILITIES.getCognitoIdentityProvider({
        "accessKeyId": CONFIG.accessKeyId,
        "secretAccessKey": CONFIG.secretAccessKey,
        "region": CONFIG.region
    });

	cognitoIdentityProvider.adminGetUser({
		"UserPoolId": CONFIG.userPoolId,
		"Username": event.parameters.filters.user_id
	}, function(error, data) {
		if (error) {
			callback(null, {
				"status": "success",
				"data": "false"
			});
		} else {
			callback(null, {
				"status": "success",
				"data": "true"
			});
		}
	});
	
}