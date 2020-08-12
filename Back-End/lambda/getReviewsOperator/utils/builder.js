const STRING = require('./string.json');


function addFilters(filters) {
    let query = "";
    if(filters) {
        if(filters.status && filters.status.length > 0) {
            query += "review.status IN (";
            for(let statusIndex = 0; statusIndex < filters.status.length; statusIndex++) {
                query += "'" + filters.status[statusIndex] + "', ";
            }
            query = query.replace(/, $/, ")") + " AND ";
        }
    }
    return query.replace(/AND $/, "");
}

module.exports = {
    builder: function(params) {
        let query = STRING.query;
        query += " FROM review JOIN accommodation_facility USING (accommodation_facility_id)";
        let filters = addFilters(params.filters);
        if(!(filters === "")) {
            query += " WHERE " + filters;
        }
		query += " ORDER BY review.review_id"; 
        let countQuery = query;
        query = query + " LIMIT ";
        if(params.index && params.index.offset) {
            query += params.index.offset + ", ";
        }
        if(params.index && params.index.limit) {
            query += params.index.limit;
        } else {
            query += "10";
        }
        query = query + "; SELECT COUNT(*) AS records FROM (" + countQuery + ") AS results;";
        return query;
    }
}