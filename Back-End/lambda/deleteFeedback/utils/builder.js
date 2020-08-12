module.exports = {

    builder: function(params) {
        return "DELETE FROM feedback"
            + " WHERE feedback.user_id = '" + params.user_id + "'"
            + " AND feedback.review_id = '" + params.review_id + "'";
    }

};
