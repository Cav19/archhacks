console.log("Deploying server...");

//Load dependencies
var http = require('http');
var mysql = require('mysql');
var queryString = require('queryString');
var dal = require('./dal');

//Start event loop
var server = http.createServer(requestHandler);
server.listen(1337);

console.log("Server started.");
/**
 * Handles all requests made to the server. 
 * @param req Request object 
 * @param res Response object used to reply to the client. 
 */
function requestHandler(req, res) {
    console.log("Request recieved");
    console.log(req.url);
    //console.log(req.headers);
    //console.log(req.connection);
    if(req.method == "POST" && req.url == "/quiteasy") {
        var body = '';

        req.on('data', (chunk) => {
            body += chunk.toString();
        });

        req.on('end', () => {
            //var data = queryString.parse(body);
            try {
                var data = JSON.parse(body);
            } catch (err) {
                replyUnableToParseData(res, err);
            }

            if(data['function'] == undefined) {
                replyNoFunction(res)
                return;
            } 

            switch(data['function']) {
                case 'login':
                    handleLogin(req, res, data);
                    return;
                case 'signup':
                    handleSignup(req, res, data);
                    return;
                case 'getSelfCampagins':
                    handleSelfCampagins(req, res, data);
                    return;
                case 'getOtherCampgains':
                    handleOtherCampagins(req, res, data);
                    return;
                case 'getFriends':
                    handleGetFriends(req, res, data);
                    return;
                case 'sendMessage':
                    handleSendMessage(req, res, data);
                    return;
                case 'getMessage':
                    handleGetMessage(req, res, data);
                    return;
                default:
                    replyUnsupportedFunction(res);
            }
        });
    } else {
        res.statusCode = 400;
        res.end("unsupported");
    }
}

/**
 * Handles the login procuedure. 
 * @param req Request from client with the login information. 
 * @param res Response to be made to the client.
 */
var reqLogin = ['username', 'password'];
function handleLogin(req, res, data) {
    if(!isRequiredSet(data, reqLogin)) {
        replyMissingInputs(res);
        return;
    }

    dal.login(data['username'], data['password'], function(success, userId, token) {
        var resData = {};
        setHeaderJson(res);

        if(success) {
            resData['success'] = true;
            resData['userId'] = userId;
            resData['token'] = token;
            res.statusCode = 200;
        } else {
            resData['success'] = false;
            resData['token'] = '';
            resData['userId'] = '';
            res.statusCode = 400;
        }
        res.end(JSON.stringify(resData));
    });
}

var reqSignup = ['firstName', 'lastName', 'username', 'password'];
function handleSignup(req, res, data) {
    if(!isRequiredSet(data, reqSignup)) {
        replyMissingInputs(res);
        return;
    }
}

var reqGetFriends = ['token', 'id'];
function handleGetFriends(req, res, data) {
    if(!isRequiredSet(data, reqGetFriends)) {
        replyMissingInputs(res);
        return;
    }

    dal.authenticateUser(data['id'], data['token'], function (valid) {
        if(!valid) {
            replyUnableToAuthenticate(res);
        } else {
            as_handleGetFriends(req, res, data);
        }
    })
}

/**
 * Authentication successfull. Now get friends data. 
 */
function as_handleGetFriends(req, res, data) {
    dal.getFriends(data['id'], function(success, friends) {
        if(!success) {
            replyWithError(res, "Unknown error", "");
            return;
        }

        var friendIds = [];
        for(var i=0; i<friends.length; i++) {
            friendIds.push(friends[i]['id']);
            friends[i]['campaigns'] = [];
        }

        dal.getManyCampaigns(friendIds, function callback(success, campaigns) {
           var resData = {};
           if(success) {
               resData['success'] = true;
               res.statusCode = 200;

               for(var i=0; i<campaigns.length; i++) {
                   for(var j=0; j<friends.length; j++) {
                       if(campaigns[i]['ownerId'] == friends[j]['id']) {
                           friends[j]['campaigns'].push(campaigns[i]);
                       }
                   }
               }

               resData['friends'] = friends;
           } else {
               resData['success'] = false;
               res.statusCode = 400;
           }
           setHeaderJson(res);
           res.end(JSON.stringify(resData));
        });
    });
}

var reqSendMessage = ['token', 'id', 'message', 'friendId']
function handleSendMessage(req, res, data) {
    if(!isRequiredSet(data, reqSendMessage)) {
        replyMissingInputs(res);
        return;
    }

    dal.authenticateUser(data['id'], data['token'], function(valid) {
        if(!valid) {
            replyUnableToAuthenticate(res);
            return;
        } else {
            as_handleSendMessage(req, res, data);
        }
    });
}

function as_handleSendMessage(req, res, data) {
    dal.sendMessage(data['id'], data['friendId'], data['message'], function(messageSent) {
        var resData = {};
        setHeaderJson(res);
        if(messageSent) {
            res.statusCode = 200;
            resData['success'] = true;
        } else {
            res.statusCode = 400;
            resData['success'] = false;
        }
        res.end(JSON.stringify(resData));
    });
}

var reqGetMessage = ['token', 'id'];
function handleGetMessage(req, res, data) {
    if(!isRequiredSet(req, res, data)) {
        replyMissingInputs(res);
        return;
    }

    dal.authenticateUser(data['id'], data['token'], function(valid) {
        if(!valid) {
            replyUnableToAuthenticate(res);
            return;
        } else {
            as_handleGetMessage(req, res, data);
        }
    });
}

function as_handleGetMessage(req, res, data) {
    dal.getMessage(data['id'], function(valid, senderFirstName, senderLastName, message) {
        var resData = {};
        setHeaderJson(res);
        if(!valid) {
            resData['success'] = false;
            res.statusCode = 400;
        } else {
            resData['success'] = true;
            resData['senderFirstName'] = senderFirstName;
            resData['senderLastName'] = senderLastName;
            resData['message'] = message;
            res.statusCode = 200;
        }
        res.end(JSON.stringify(resData));
    });
}

var reqSelfCampaigns = ['token', 'id'];
function handleSelfCampagins(req, res, data) {
    if(!isRequiredSet(data, reqSelfCampaigns)) {
        replyMissingInputs(res);
        return;
    }

    dal.authenticateUser(data['id'], data['token'], function (valid) {
        if(!valid) {
            replyUnableToAuthenticate(res);
        } else {
            as_handleSelfCampaigns(req, res, data);
        }
    });
}

function as_handleSelfCampaigns(req, res, data) {
    dal.getSelfCampaigns(data['id'], function(campaigns) {
        var resData = {};
        resData['success'] = true;
        resData['campaigns'] = campaigns;
        res.statusCode = 200;
        setHeaderJson(res);
        res.end(JSON.stringify(resData));
    });
}

var reqOtherCampaigns = ['tokens', 'id', 'friendId'];
function handleOtherCampagins(req, res, data) {
    if(!isRequiredSet(data, reqOtherCampaigns)) {
        replyMissingInputs(res);
        return;
    }

    dal.authenticateUser(token, data['id'], function(valid) {
        if(!valid) {
            replyUnableToAuthenticate(res);
            return;
        } 

        as_handleOtherCampaigns(req, res, data);
    });
}

function as_handleOtherCampaigns(req, res, data) {
    dal.getOtherCampaigns(data['friendId'], function(error, campaigns) {
        if(error) {
            replyUnableToLoadCampaigns(error);
        } else {
            var resData = {};
            resData['success'] = true;
            resData['campaigns'] = campaigns;
            setHeaderJson(res);
            res.statusCode = 200;
            res.end(JSON.stringify(resData));
        }
    });
}



/**
 * Utility Functions
 */

function setHeaderJson(res) {
    res.setHeader('content-type', 'text/json');
}

function replyWithError(res, errorMsg, error, errorCode) {
    if (error == undefined) {
        error = '';
    }

    if(errorCode == undefined) {
        errorCode = 400;
    }

    var resData = {};
    resData['success'] = false;
    resData['errorMsg'] = errorMsg;
    resData['error'] = error;
    
    res.statusCode = errorCode;
    setHeaderJson(res);
    res.end(JSON.stringify(resData));
}

function replyNoFunction(res) {
   replyWithError(res, "no function provided");;
}

function replyUnableToParseData(res, error) {
    replyWithError(res, "unable to parse data", error);
}

function replyUnsupportedFunction(res) {
    replyWithError(res, "unsupported function");
}

function replyUnableToAuthenticate(res) {
    replyWithError(res, "unable to authenticate");
}

function replyMissingInputs(res) {
    replyWithError(res, "missing inputs");
}

function isRequiredSet(data, required) {
	for(var i =0; i < required.length; i++) {
		if(data[required[i]] === undefined) {
			return false;
		}
	}
	return true; 
} 