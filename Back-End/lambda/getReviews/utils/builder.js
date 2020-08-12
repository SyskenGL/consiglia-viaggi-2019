const STRING = require('./string.json');


function addFilters(filters) {
    let query = "";
    if(filters) {
        if(filters.review_id) {
            query += "review.review_id = '" + filters.review_id + "' AND ";
        }
        if(filters.accommodation_facility_id) {
            query += "review.accommodation_facility_id = '" + filters.accommodation_facility_id + "' AND ";
        }
        if(filters.reviewer_id) {
            query += "review.user_id = '" + filters.user_id + "' AND ";
        }
        if(filters.rating && filters.rating.length > 0) {
            query += "review.rating IN (";
            for(let ratingIndex = 0; ratingIndex < filters.rating.length; ratingIndex++) {
                query += "'" + filters.rating[ratingIndex] + "', ";
            }
            query = query.replace(/, $/, ")") + " AND ";
        }
        if(filters.season && filters.season.length > 0) {
            let seasonQuery = "(";
            for (let seasonIndex = 0; seasonIndex < filters.season.length; seasonIndex++) {
                if (filters.season[seasonIndex].toUpperCase() === "SUMMER") {
                    seasonQuery += "(DATE_FORMAT(review.date_of_stay, \"%m\") IN (6,7,8)) OR ";
                } else if (filters.season[seasonIndex].toUpperCase() === "WINTER") {
                    seasonQuery += "(DATE_FORMAT(review.date_of_stay, \"%m\") IN (12,1,2)) OR ";
                } else if (filters.season[seasonIndex].toUpperCase() === "SPRING") {
                    seasonQuery += "(DATE_FORMAT(review.date_of_stay, \"%m\") IN (3,4,5)) OR ";
                } else if (filters.season[seasonIndex].toUpperCase() === "AUTUMN") {
                    seasonQuery += "(DATE_FORMAT(review.date_of_stay, \"%m\") IN (9,10,11)) OR ";
                }
            }
            seasonQuery = seasonQuery.replace(/ OR $/, ")");
            if(!(seasonQuery === "(")) {
                query += seasonQuery + " AND ";
            }
        }
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


function addSortingKeys(sortingKeys) {
    let query = "";
    if(sortingKeys) {
        if(sortingKeys.title) {
            query += "review.title " + sortingKeys.title.toUpperCase() + ", ";;
        }
        if(sortingKeys.rating) {
            query += "review.rating " + sortingKeys.rating.toUpperCase() + ", ";;
        }
        if(sortingKeys.date_of_stay) {
            query += "review.date_of_stay " + sortingKeys.date_of_stay.toUpperCase() + ", ";;
        }
        if(sortingKeys.publication_date) {
            query += "review.publication_date " + sortingKeys.publication_date.toUpperCase() + ", ";;
        }
        if(sortingKeys.total_likes) {
            query += "review.total_likes " + sortingKeys.total_likes.toUpperCase() + ", ";;
        }   
        if(sortingKeys.total_dislikes) {
            query += "review.total_dislikes " + sortingKeys.total_dislikes.toUpperCase() + ", ";
        }
    }
    return query.replace(/, $/, "");
}


module.exports = {

    builder: function(params) {
        let query = STRING.query + ", ";
        if(params.filters.user_id) {
            query += "IF ((SELECT COUNT(*) FROM feedback"
                  + " WHERE feedback.review_id = review.review_id"
                  + " AND feedback.user_id = '" + params.filters.user_id + "'"
                  + " ) > 0, (SELECT feedback.type FROM feedback"
                  + " WHERE feedback.user_id = '" + params.filters.user_id + "'"
                  + " AND feedback.review_id = review.review_id), null) AS feedback";
        } else {
            query += "null AS feedback";
        }
        query += " FROM review JOIN accommodation_facility USING (accommodation_facility_id)";
        let filters = addFilters(params.filters);
        let sortingKeys = addSortingKeys(params.sort);
        if(!(filters === "")) {
            query += " WHERE " + filters;
        }
        if(!(sortingKeys === "")) {
            query += " ORDER BY " + sortingKeys;
        }
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

