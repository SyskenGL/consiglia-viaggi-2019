module.exports = {

    builder: function(params) {
        return "UPDATE notification" 
        	+ " SET notification.status = 'marked'"
            + " WHERE notification.user_id = '" + params.user_id + "'";
    }

};
