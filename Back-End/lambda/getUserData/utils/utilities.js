const MYSQL = require('../node_modules/mysql');
const AWS = require('../node_modules/aws-sdk');


    
function convertDateFormat(date) {
	let months = [
		"January",
		"February",
		"March",
		"April",
		"May",
		"June",
		"July",
		"August",
		"September",
		"October",
		"November",
		"December"
	];
	return date.getDate() + " " + months[date.getMonth()] + " " + date.getFullYear();
}


module.exports = {

    getMySqlConnection: function(params) {
        return MYSQL.createConnection(params);
    },


    getCognitoIdentityProvider: function(params) {
        AWS.config.update({
            "accessKeyId": params.accessKeyId,
            "secretAccessKey": params.secretAccessKey,
            "region": params.region
        });
        return new AWS.CognitoIdentityServiceProvider();
    },
	
	extractUserData: function(user) {
        let userData = {
            "user_id": "",
            "nickname": "",
            "preferred_username": "",
            "given_name": "",
            "family_name": "",
            "email": "",
            "picture": "",
            "create_date": convertDateFormat(user.UserCreateDate)
        }
        for (let attributeIndex = 0; attributeIndex < user.UserAttributes.length; attributeIndex++) {
            if (user.UserAttributes[attributeIndex].Name === "sub") {
                userData.user_id = user.UserAttributes[attributeIndex].Value;
            } else if (user.UserAttributes[attributeIndex].Name === "nickname") {
                userData.nickname = user.UserAttributes[attributeIndex].Value;
            } else if (user.UserAttributes[attributeIndex].Name === "preferred_username") {
                userData.preferred_username = user.UserAttributes[attributeIndex].Value;
            } else if (user.UserAttributes[attributeIndex].Name === "given_name") {
                userData.given_name = user.UserAttributes[attributeIndex].Value;
            } else if (user.UserAttributes[attributeIndex].Name === "family_name") {
                userData.family_name = user.UserAttributes[attributeIndex].Value;
            } else if (user.UserAttributes[attributeIndex].Name === "email") {
                userData.email = user.UserAttributes[attributeIndex].Value;
            } else if (user.UserAttributes[attributeIndex].Name === "picture") {
                userData.picture = user.UserAttributes[attributeIndex].Value;
            }
        }
        return userData;
	}

}