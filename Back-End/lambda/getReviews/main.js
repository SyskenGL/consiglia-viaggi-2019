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
            let query = BUILDER.builder(event.parameters);
            connection.query(query, async function(error, result, fields) {
                if(error) {
                    connection.destroy();
                    callback(error);
                } else {    
                    UTILITIES.setImagesUrl(result[0]);
                    for(let reviewIndex = 0; reviewIndex < result[0].length; reviewIndex++) {
                        await UTILITIES.setReviewerData(result[0][reviewIndex], cognitoIdentityProvider, callback);
                    }
                    connection.end();
                    callback(null, {
                        "status": "success",
                        "message": "reviews successfully retrieved",
                        "data": {
                            "records": result[1][0].records,
                            "retrieved": result[0].length,
                            "reviews": result[0]
                        },
                        "query": query
                    });
                }
            });
        }
    });

}