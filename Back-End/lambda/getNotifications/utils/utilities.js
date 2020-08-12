const MYSQL = require('../node_modules/mysql');
const CONFIG = require('../config/config.json');


module.exports = {

    getMySqlConnection: function(params) {
        return MYSQL.createConnection(params);
    }

}