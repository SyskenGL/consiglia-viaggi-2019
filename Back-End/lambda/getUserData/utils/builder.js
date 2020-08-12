module.exports = {

	builder: function(userId) {
		return "SELECT (SELECT COUNT(*) FROM review"
			+ " WHERE review.user_id = '" + userId + "' AND review.status = 'approved') AS total_reviews,"
			+ " (SELECT COUNT(*) FROM favorite"
			+ " WHERE favorite.user_id = '" + userId + "') AS total_favorites";
	}

}