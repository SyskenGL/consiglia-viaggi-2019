module.exports = {

    builder: function(params) {
        let query = "UPDATE accommodation_facility "
            + "SET accommodation_facility.total_views = accommodation_facility.total_views+1 "
            + "WHERE accommodation_facility.accommodation_facility_id = '" + params.accommodation_facility_id + "'; ";
        if(params.user_id) {
            query += "REPLACE INTO temporary_history (accommodation_facility_id, user_id) VALUES("
                + "'" + params.accommodation_facility_id + "', "
                + "'" + params.user_id + "');";
        }
        return query;
    }

};