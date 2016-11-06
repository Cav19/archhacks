var mysql = require('mysql');
var util = require('util');

var conn = mysql.createConnection({
    host : 'localhost',
    user : 'root',
    password : '',
    database : 'quiteasy' 
});

conn.connect(function(err) {
    if(err) {
        console.log("Unable to connect to database...");
    } else {
        console.log("Connected to database.");
    }
})

const authenticationQuery = 'SELECT is_valid_authentication(%s, %s) AS valid';
function authenticateUser(userId, token, callback) {
    const query = util.format(authenticationQuery, userId, token);
    conn.query(query, function(error, rows, fields) {
        if(error) {
            console.log("error authenticating user: " + error);
            callback(false);
            return;
        } 
        callback(rows[0]['valid']);
    });
}