module.exports = {

    builder: function(params) {
        return "INSERT INTO notification (review_id, user_id, title, message, type) VALUES ("
            + "'" + params.review_id + "', "
            + "'" + params.user_id + "', "
            + "\"" + params.title + "\", "
            + "\"" + params.message + "\", "
            + "'" + params.type.toLowerCase() + "');"
    }

}


