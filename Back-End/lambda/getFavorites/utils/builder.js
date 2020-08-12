module.exports = {

    builder: function(params) {
        let query = "SELECT *, null AS matched_in, null AS total_matched_reviews, null AS matched_review_sample,"
            + " IF((SELECT COUNT(*) FROM review"
            + " WHERE review.accommodation_facility_id = accommodation_facility.accommodation_facility_id"
            + " AND review.user_id = '" + params.filters.user_id + "') > 0, 'true', 'false') AS commented,"
            + " IF((SELECT COUNT(*) FROM favorite"
            + " WHERE favorite.accommodation_facility_id = accommodation_facility.accommodation_facility_id"
            + " AND favorite.user_id = '" + params.filters.user_id + "') > 0, 'true', 'false') AS favorite"
            + ", (SELECT COUNT(*) FROM review" 
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
            + " AND review.rating = 1 AND review.status = 'approved') AS total_one_stars_reviews"
            + " FROM accommodation_facility JOIN favorite USING (accommodation_facility_id)"
            + " WHERE favorite.user_id = '" + params.filters.user_id + "'";
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