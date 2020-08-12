const STRING = require('./string.json');


function addFilters(filters) {
    let query = "";
    if(filters) {
        if(filters.accommodation_facility_id) {
            query += "accommodation_facility.accommodation_facility_id = '" 
                + filters.accommodation_facility_id + "' AND ";
        }
        if(filters.name) {
            query += "UPPER(accommodation_facility.name) = '" 
                + filters.name.toUpperCase() + "' AND ";
        }
        if(filters.type) {
            query += "UPPER(accommodation_facility.type) = '" 
                + filters.type.toUpperCase() + "' AND ";
        }
        if(filters.country) {
            query += "UPPER(accommodation_facility.country) = '" 
                + filters.country.toUpperCase() + "' AND ";
        }
        if(filters.administrative_area_level_1) {
            query += "UPPER(accommodation_facility.administrative_area_level_1) = '" 
                + filters.administrative_area_level_1.toUpperCase() + "' AND ";
        }
        if(filters.administrative_area_level_2) {
            query += "UPPER(accommodation_facility.administrative_area_level_2) = '" 
                + filters.administrative_area_level_2.toUpperCase() + "' AND ";
        }
        if(filters.administrative_area_level_3) {
            query += "UPPER(accommodation_facility.administrative_area_level_3) = '" 
                + filters.administrative_area_level_3.toUpperCase() + "' AND ";
        }
        if(filters.locality) {
            query += "UPPER(accommodation_facility.locality) = '" 
                + filters.locality.toUpperCase() + "' AND ";
        }
        if(filters.radius && filters.latitude && filters.longitude) {
            query += "CAST(calculate_distance_between_coords(accommodation_facility.latitude, accommodation_facility.longitude, "
                + "'" + filters.latitude + "', "
                + "'" + filters.longitude + "') AS DECIMAL(15,8)) <= "
                + "'" + filters.radius + "' AND ";
        }
        if (filters.rating && filters.rating.length > 0) {
            query += "accommodation_facility.rating IN (";
            for(let ratingIndex = 0; ratingIndex < filters.rating.length; ratingIndex++) {
                query += "'" + filters.rating[ratingIndex] + "', ";
            }
            query = query.replace(/, $/, ")") + " AND ";
        }
        if (filters.tags && filters.tags.length > 0) {
            query += "(";
            for (let tagIndex = 0; tagIndex < filters.tags.length; tagIndex++) {
                query += "UPPER(accommodation_facility.tags) LIKE "
                    + "'%" + filters.tags[tagIndex].toUpperCase() + "%' OR ";
            }
            query = query.replace(/ OR $/, ")") + " AND ";
        }
    }
    return query.replace(/AND $/, "");
}

function addSortingKeys(sortingKeys) {
    let query = "";
    if(sortingKeys) {
        if(sortingKeys.name) {
            query += "accommodation_facility.name " + sortingKeys.name.toUpperCase() + ", ";
        }
        if(sortingKeys.rating) {
            query += "accommodation_facility.rating " + sortingKeys.rating.toUpperCase() + ", ";
        }
        if(sortingKeys.total_favorites) {
            query += "accommodation_facility.total_favorites " + sortingKeys.total_favorites.toUpperCase() + ", ";
        }
        if(sortingKeys.views) {
            query += "accommodation_facility.total_views " + sortingKeys.total_views.toUpperCase() + ", ";
        }
    }
    return query.replace(/, $/, "");
}


module.exports = {

    builder: function(params) {
        let query;
        let filters;
        let sortingKeys;
        if(params.filters.user_id) {
            query = "SELECT *, "
                + " IF((SELECT COUNT(*) FROM review"
                + " WHERE review.accommodation_facility_id = accommodation_facility.accommodation_facility_id"
                + " AND review.user_id = '" + params.filters.user_id + "') > 0, 'true', 'false') AS commented,"
                + " IF((SELECT COUNT(*) FROM favorite"
                + " WHERE favorite.accommodation_facility_id = accommodation_facility.accommodation_facility_id"
                + " AND favorite.user_id = '" + params.filters.user_id + "') > 0, 'true', 'false') AS favorite";
        } else {
            query = "SELECT *, null AS commented, null AS favorite"
        }
        query += ", (SELECT COUNT(*) FROM review" 
                + " WHERE review.accommodation_facility_id = accommodation_facility.accommodation_facility_id "
                + " AND review.rating = 5 AND review.status = 'approved') AS total_five_stars_reviews"
                + ", (SELECT COUNT(*) FROM review" 
                + " WHERE review.accommodation_facility_id = accommodation_facility.accommodation_facility_id "
                + " AND review.rating = 4 AND review.status = 'approved') AS total_four_stars_reviews"
                + ", (SELECT COUNT(*) FROM review" 
                + " WHERE review.accommodation_facility_id = accommodation_facility.accommodation_facility_id "
                + " AND review.rating = 3 AND review.status = 'approved') AS total_three_stars_reviews"
                + ", (SELECT COUNT(*) FROM review" 
                + " WHERE review.accommodation_facility_id = accommodation_facility.accommodation_facility_id "
                + " AND review.rating = 2 AND review.status = 'approved') AS total_two_stars_reviews"
                + ", (SELECT COUNT(*) FROM review" 
                + " WHERE review.accommodation_facility_id = accommodation_facility.accommodation_facility_id "
                + " AND review.rating = 1 AND review.status = 'approved') AS total_one_stars_reviews";
        if(params.keywords) {
            let parentSubQuery = "";
            let firstChildSubQuery = "(";
            let secondChildSubQuery = "(";
            let thirdChildSubQuery = "(";
            let tokens = params.keywords.split(" ");
            for(let tokenIndex = 0; tokenIndex < tokens.length; tokenIndex++) {
                firstChildSubQuery += "CONCAT(' ', Upper(accommodation_facility.name), ' ') LIKE '% " + tokens[tokenIndex] + " %' OR ";
                secondChildSubQuery += "CONCAT(' ', Upper(accommodation_facility.tags), ' ') LIKE '% " + tokens[tokenIndex] + " %' OR ";
                thirdChildSubQuery += "CONCAT(' ', Upper(review.description), ' ') LIKE '% " + tokens[tokenIndex] + " %' OR "
                                    + "CONCAT(' ', Upper(review.title), ' ') LIKE '% " + tokens[tokenIndex] + " %' OR "
            }
            firstChildSubQuery = firstChildSubQuery.replace(/ OR $/, ") ");
            secondChildSubQuery = secondChildSubQuery.replace(/ OR $/, ") ");
            thirdChildSubQuery = thirdChildSubQuery.replace(/ OR $/, ") ");
            parentSubQuery = STRING.query;
            parentSubQuery = parentSubQuery.replace(/\$1/g, firstChildSubQuery);
            parentSubQuery = parentSubQuery.replace(/\$2/g, secondChildSubQuery);
            parentSubQuery = parentSubQuery.replace(/\$3/g, thirdChildSubQuery);
            query += " FROM" + parentSubQuery;
        } else {
            query += ", null AS matched_in, 0 AS total_matched_reviews, null AS matched_review_sample FROM accommodation_facility";
        }
        filters = addFilters(params.filters);
        sortingKeys = addSortingKeys(params.sort);
        if(!(filters === "")) {
            query += " WHERE " + filters;
        }
        if(!(sortingKeys === "")) {
            query += " ORDER BY " + sortingKeys;
        }
        let countQuery = query;
        query = query + " LIMIT ";
        if (params.index.offset) {
            query += params.index.offset + ", ";
        }
        if (params.index.limit) {
            query += params.index.limit;
        } else {
            query += "10";
        }
        query += "; SELECT COUNT(*) AS records FROM (" + countQuery + ") AS accommodation_facility;";
        return query;
    }

}