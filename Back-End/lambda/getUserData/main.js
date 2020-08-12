const MYSQL = require('./node_modules/mysql');
const CONFIG = require('./config/config.json');
const UTILITIES = require('./utils/utilities.js');
const BUILDER = require('./utils/builder.js');


exports.handler = (event, context, callback) => {

	let connection = UTILITIES.getMySqlConnection({
        "host": CONFIG.host,
        "user": CONFIG.user,
        "password": CONFIG.password,
        "database": CONFIG.database,
        "multipleStatements": CONFIG.multipleStatements
    });

    let cognitoIdentityProvider = UTILITIES.getCognitoIdentityProvider({
    	"accessKeyId": CONFIG.accessKeyId,
    	"secretAccessKey": CONFIG.secretAccessKey,
    	"region": CONFIG.region
    });

    connection.connect(function(error) {
    	if(error) {
    		callback(error);
    	} else {
    		cognitoIdentityProvider.adminGetUser({
    			"UserPoolId": CONFIG.userPoolId,
    			"Username": event.parameters.filters.user_id
    		}, function(error, data) {
    			if(error) {
    				connection.destroy();
    				callback(error);
    			} else {
					let user = UTILITIES.extractUserData(data);
    				let query = BUILDER.builder(user.user_id);
    				connection.query(query, function(error, result, fields) {
    					if(error) {
    						connection.destroy();
    						callback(error);
    					} else {
							console.log(result);
    						user.total_reviews = result[0].total_reviews;
    						user.total_favorites = result[0].total_favorites;
    						connection.end();
    						callback(null, {
    							"status": "success",
    							"message": "user data successfully retrieved",
    							"data": {
    								"records": "1",
    								"retrieved": "1",
    								"user": user
    							}
    						});
    					}
    				});
    			}
    		});
    	}
    });

}
