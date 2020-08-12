module.exports = {

    builder: function(params) {
        return "DELETE FROM favorite"
            + " WHERE favorite.user_id = '" + params.user_id + "'"
            + " AND favorite.accommodation_facility_id = '" + params.accommodation_facility_id + "'";
    }

};
