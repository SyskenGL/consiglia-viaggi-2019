const STRING = require('./string.json');

module.exports = {

    builder: function(params) {
        return "UPDATE review" 
        	+ " SET review.status = '" + params.status + "'"
            + " WHERE review.review_id = '" + params.review_id + "';"
            + STRING.query + ", null AS feedback FROM review JOIN accommodation_facility USING (accommodation_facility_id) WHERE review.review_id = '" + params.review_id + "';"; 
    }

};
