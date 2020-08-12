const MYSQL = require('../node_modules/mysql');


module.exports = {

    getMySqlConnection: function(params) {
        return MYSQL.createConnection(params);
    }

}