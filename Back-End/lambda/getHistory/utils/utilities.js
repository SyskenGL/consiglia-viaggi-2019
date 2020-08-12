const MYSQL = require('../node_modules/mysql');
const CONFIG = require('../config/config.json');


module.exports = {

    getMySqlConnection: function(params) {
        return MYSQL.createConnection(params);
    },

    setImagesUrl: function(accommodationFacilities) {
        let filePath = "https://" + CONFIG.bucketS3 + ".s3." 
                                  + CONFIG.region 
                                  + ".amazonaws.com/accommodation_facilities/accommodation_facility_";
        for (let accommodationFacilityIndex = 0; accommodationFacilityIndex < accommodationFacilities.length; accommodationFacilityIndex++) {
            accommodationFacilities[accommodationFacilityIndex].images = [];
            for (let imageIndex = 0; imageIndex < accommodationFacilities[accommodationFacilityIndex].total_images; imageIndex++) {
                accommodationFacilities[accommodationFacilityIndex].images[imageIndex] = 
                    filePath + accommodationFacilities[accommodationFacilityIndex].accommodation_facility_id + "/image_" + (imageIndex+1) + ".png";
            }
            delete accommodationFacilities[accommodationFacilityIndex].total_images;
        }
    }

}