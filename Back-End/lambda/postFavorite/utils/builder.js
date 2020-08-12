module.exports = {

	builder: function(params) {
		return "INSERT INTO favorite (accommodation_facility_id, user_id) VALUES ("
			+ "'" + params.accommodation_facility_id + "', "
			+ "'" + params.user_id + "')"
	}

}