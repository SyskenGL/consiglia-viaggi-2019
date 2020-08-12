module.exports = {

    builder: function(params) {
        let query = "SELECT * FROM notification"
            + " WHERE notification.user_id = '" + params.filters.user_id + "'";
        if(params.filters.status) {
            query += " AND notification.status = '" + params.filters.status + "'";
        }
        let countQuery = query;
        query = query + " LIMIT ";
        if(params.index.offset) {
            query = query + params.index.offset + ", ";
        }
        if(params.index.limit) {
            query = query + params.index.limit;
        } else {
            query = query + "10";
        }
        query = query + "; SELECT COUNT(*) AS records FROM (" + countQuery + ") AS results;";
        return query;
    }

}