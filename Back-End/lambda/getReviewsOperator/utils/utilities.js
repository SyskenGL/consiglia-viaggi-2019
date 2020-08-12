const MYSQL = require('../node_modules/mysql');
const CONFIG = require('../config/config.json')
const AWS = require('../node_modules/aws-sdk');



function extractReviewerInfo(reviewer) {
    let reviewerData = {
        "preferred_username": "",
        "nickname": "",
        "profile_picture": "",
        "given_name": "",
        "family_name": ""
    }
    for (let attributeIndex = 0; attributeIndex < reviewer.UserAttributes.length; attributeIndex++) {
        if (reviewer.UserAttributes[attributeIndex].Name === "preferred_username") {
            reviewerData.preferred_username = reviewer.UserAttributes[attributeIndex].Value;
        } else if(reviewer.UserAttributes[attributeIndex].Name === "nickname") {
            reviewerData.nickname = reviewer.UserAttributes[attributeIndex].Value;
        } else if(reviewer.UserAttributes[attributeIndex].Name === "picture") {
            reviewerData.profile_picture = reviewer.UserAttributes[attributeIndex].Value;
        } else if(reviewer.UserAttributes[attributeIndex].Name === "given_name") {
            reviewerData.given_name = reviewer.UserAttributes[attributeIndex].Value;
        } else if(reviewer.UserAttributes[attributeIndex].Name === "family_name") {
            reviewerData.family_name = reviewer.UserAttributes[attributeIndex].Value;
        }
    }
    return reviewerData;
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

    setImagesUrl: function(reviews) {
        let filePath = "https://" + CONFIG.bucketS3 + ".s3." 
                                  + CONFIG.region 
                                  + ".amazonaws.com/accommodation_facilities/accommodation_facility_";
        for (let reviewIndex = 0; reviewIndex < reviews.length; reviewIndex++) {
            reviews[reviewIndex].images = [];
            for (let imageIndex = 0; imageIndex < reviews[reviewIndex].total_images; imageIndex++) {
                reviews[reviewIndex].images[imageIndex] = filePath + reviews[reviewIndex].accommodation_facility_id  
                    + "/reviews/review_" + reviews[reviewIndex].review_id + "/image_" + (imageIndex+1) + ".png";
            }
            delete reviews[reviewIndex].total_images;
        }
    },

    setReviewerData: function(review, cognitoIdentityProvider, callback) {
        return new Promise(function(resolve, reject) {
            cognitoIdentityProvider.adminGetUser({
                "UserPoolId": CONFIG.userPoolId,
                "Username": review.user_id
            }, function(error, data) {
                if(error) {
                    callback(error);
                } else {
                    let reviewer = extractReviewerInfo(data);
                    if (reviewer.preferred_username === "nickname") {
                        review.reviewer_username = reviewer.nickname;
                    } else {
                        review.reviewer_username = reviewer.given_name + " " + reviewer.family_name;
                    }
                    review.reviewer_profile_picture = reviewer.profile_picture;
					review.reviewer_id = review.user_id;
                }
                delete review.user_id;
                resolve();
            });
        });
    }

}