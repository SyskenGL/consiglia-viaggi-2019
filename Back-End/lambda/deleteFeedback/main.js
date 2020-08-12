const MYSQL = require('./node_modules/mysql');
const CONFIG = require('./config/config.json');
const UTILITIES = require('./utils/utilities.js');
const BUILDER = require('./utils/builder.js');

 
exports.handler = (event, context, callback) => {

	let connection = UTILITIES.getMySqlConnection({
		"host": CONFIG.host,
		"user": CONFIG.user,
		"password": CONFIG.password,
		"database": CONFIG.database,
		"multipleStatements": CONFIG.multipleStatements
	});


	connection.connect(function(error) {
		if(error) {
			callback(error);
		} else {
			let query = BUILDER.builder(event.parameters);
			connection.query(query, function(error, result, fields) {
				if(error) {
					connection.destroy();
					callback(error);
				} else {
					connection.end();
					callback(null, {
						"status": "success",
						"message": "feedback successfully deleted",
						"query": query
					});
				}
			});
		}
	});

}