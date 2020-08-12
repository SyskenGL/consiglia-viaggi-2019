const CONFIG = require('./config/config.json');
const UTILITIES = require('./utils/utilities.js');
const AWS = require('./node_modules/aws-sdk');
const S3 = new AWS.S3();


exports.handler = (event, context, callback) => {

    let cognitoIdentityProvider = UTILITIES.getCognitoIdentityProvider({
        "accessKeyId": CONFIG.accessKeyId,
        "secretAccessKey": CONFIG.secretAccessKey,
        "region": CONFIG.region
    });

    let baseUrl = "https://" + CONFIG.bucketS3 + ".s3." + CONFIG.region + ".amazonaws.com/";

    let avatars = [
        "users/default_image_01.png",
        "users/default_image_02.png",
        "users/default_image_03.png",
        "users/default_image_04.png",
        "users/default_image_05.png",
        "users/default_image_06.png",
        "users/default_image_07.png",
        "users/default_image_08.png",
        "users/default_image_09.png",
        "users/default_image_10.png",
        "users/default_image_11.png",
        "users/default_image_12.png",
        "users/default_image_13.png",
        "users/default_image_14.png",
        "users/default_image_15.png",
        "users/default_image_16.png",
        "users/default_image_17.png",
        "users/default_image_18.png",
        "users/default_image_19.png",
        "users/default_image_20.png"
    ];

	let avatarUrl = "users/user_" + Date.now() + ".png"; 

    cognitoIdentityProvider.adminUpdateUserAttributes({
        "UserAttributes": [{
            "Name": "picture",
            "Value": baseUrl + avatarUrl
        }],
        "UserPoolId": CONFIG.userPoolId,
        "Username": event.request.userAttributes.sub
    }, function(error, data) {
		if (error) {
			callback(error);
		} else {
            let params = {
                "Bucket": CONFIG.bucketS3,
                "CopySource": CONFIG.bucketS3 + "/" + avatars[Math.floor(Math.random()*avatars.length)],
                "Key": avatarUrl
            };
			S3.copyObject(params, function(error, data) {
                if(error) {
                    callback(error);
                } else {
                    callback(null, event);
                }
			});
		} 
    }); 
}












