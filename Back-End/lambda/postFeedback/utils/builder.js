module.exports = {

    builder: function(params) {
        return "REPLACE INTO feedback (user_id, review_id, type) VALUES("
           	+ "'" + params.user_id + "', "
            + "'" + params.review_id + "', "
            + "'" + params.type.toLowerCase() + "')";
    }

};
