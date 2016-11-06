module.exports = {
	authenticateUser : authenticateUser,
	getOtherCampaigns: getOtherCampaigns,
	getSelfCampaigns : getSelfCampaigns,
    login : login,
    getFriends : getFriends, 
    getManyCampaigns : getManyCampaigns,
    sendMessage : sendMessage,
    getMessage : getMessage
};

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

var authenticationQuery = 'SELECT is_valid_authentication(%s, "%s")';
var loginQuery = "CALL login('%s', '%s')" //Username, password
var otherCampaignsQuery = "CALL get_user_campaigns(%s, false)";
var selfCampaignsQuery = "CALL get_user_campaigns(%s, true)";
var signupQuery = "SELECT create_profile('%s','%s','%s','%s')"
var sendMessageQuery = 'SELECT send_message(%s, %s, "%s")';
var getFriendsQuery = 'CALL get_user_friends(%s)';
var getManyCampaignsQuery = "CALL get_all_campaigns('%s')";
var getMessageQuery = "CALL get_message(%s)";

function authenticateUser(userId, token, callback) {
    var query = util.format(authenticationQuery, userId, token);
    conn.query(query, function(error, rows, fields) {
        if(error) {
            console.log("error authenticating user: " + error);
            callback(false);
            return;
        } 
        
        if(rows[0]['valid'] == 0) {
            callback(false);
        } else {
            callback(true);
        }
    });
}

function login(username, password, callback) {
    var query = util.format(loginQuery, username, password);
    conn.query(query, function(error, rows, fields) {
        if(error) {
            console.log(error);
            callback(false, undefined, undefined);
            return;
        }
        rows = rows[0];
       // console.log(fields);
        //console.log(rows);
        if(rows[0]['user_id'] == undefined) {
            callback(false, undefined, undefined);
        } else {
            callback(true, rows[0]['user_id'], rows[0]['token']);
        }
    });
}


function signup(username, password, firstName, lastName) {
    var query = util.format(signupQuery, firstName, lastName, userName, password);
    conn.query(query, function(error, rows, fields) {
            if(error) {

            }
    });
}

function sendMessage(fromId, toId, message, callback) {
    var query = util.format(sendMessageQuery, fromId, toId, message);
    conn.query(query, function(error, rows, fields) {
        if (error) {
            console.log(error);
            callback(false);
        } else {
            callback(true);
        }
    });
}

function getMessage(toId, callback) {
    var query = util.format(getMessageQuery, toId);
    conn.query(query, function(error, rows, fields) {
        if(error) {
            console.log(error);
            callback(false, undefined, undefined, undefined);
        } else {
            rows = rows[0];
            callback(true, rows[0]['sf_name'], rows[0]['sl_name'], rows[0]['msg']);
        }
    });
}

function getFriends(userId, callback) {
    var query = util.format(getFriendsQuery, userId);
    conn.query(query, function(error, rows, fields) {
        if(error) {
            console.log(error);
            callback(false, undefined);
        } else {
            rows = rows[0];
            var friends = []
            for(var i=0; i<rows.length; i++) {
                var friend = {}
                friend['id'] = rows[i]['id'];
                friend['firstName'] = rows[i]['first_name'];
                friend['lastName'] = rows[i]['last_name'];
                friend['username'] = rows[i]['username'];
                friends.push(friend);
            }
            callback(true, friends);
        }
    });
}

function getManyCampaigns(userIds, callback) {
    var strList = userIds.join(',');
    var query = util.format(getManyCampaignsQuery, strList);
    conn.query(query, function(error, rows, fields) {
        if(error) {
            callback(false, undefined);
        } else {
            rows = rows[0];
            campaigns = [];
            for(var i=0; i<rows.length; i++) {
                var campaign = {};
                campaign['id'] = rows[i]['id'];
                campaign['ownerId'] = rows[i]['owner_id'];
                campaign['campaignType'] = rows[i]['campaign_type'];
                campaigns.push(campaign);
            }
            callback(true, campaigns);
        }
    });
}

function getSelfCampaigns(userId, callback) {
    getCampaigns(userId, true, callback);
}

function getOtherCampaigns(userId, callback) {
    getCampaigns(userId, false, callback);
}

function getCampaigns(userId, isSelf, callback) {
    if(isSelf) {
        var query = util.format(selfCampaignsQuery, userId);
    } else {
        var query = util.format(otherCampaignsQuery, userId);
    }

    conn.query(query, function(error, rows, fields) {
        if(error) {
            callback(error, undefined);
        } else {
            var campaigns = [];
            for(var i=0; i<rows.length; i++) {
                var campaign = {};
                campaign['id'] = rows[i]['id'];
                campaign['campaignType'] = rows[i]['campaign_type'];
                campaign['isHidden'] = rows[i]['is_hidden'];
                campaigns.push(campaign);
            }
            callback(undefined, campaigns);
        }
    });
}