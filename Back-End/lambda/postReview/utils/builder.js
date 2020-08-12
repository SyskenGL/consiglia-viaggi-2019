module.exports = {

    builder: function(params) {
        return "INSERT INTO review (title, description, rating, date_of_stay, publication_date, total_images, accommodation_facility_id, user_id) VALUES ("
            + "\"" + params.title + "\", "
            + "\"" + params.description + "\", "
            + "'" + params.rating + "', "
            + "'" + params.date_of_stay + "', "
            + "CURRENT_DATE, "
            + "'" + params.total_images + "', "
            + "'" + params.accommodation_facility_id + "', "
            + "'" + params.user_id + "'); "
            + "SELECT review.review_id FROM review "
            + "WHERE review.user_id = '" + params.user_id + "' "
            + "AND review.accommodation_facility_id = '" + params.accommodation_facility_id + "';"
    }

}


