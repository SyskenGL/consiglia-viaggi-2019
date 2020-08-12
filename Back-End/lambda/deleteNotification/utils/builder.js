module.exports = {

    builder: function(params) {
        return "DELETE FROM notification" 
        	+ " WHERE notification.user_id = '" + params.user_id + "'"
            + " AND notification.review_id = '" + params.review_id + "'";
    }

};
