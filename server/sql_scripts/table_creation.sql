DROP TABLE IF EXISTS messages;
DROP TABLE IF EXISTS friendships;
DROP TABLE IF EXISTS campaign_updates;
DROP TABLE IF EXISTS campaigns;
DROP TABLE IF EXISTS sessions;
DROP TABLE IF EXISTS profiles;


CREATE TABLE profiles (
    id INT NOT NULL AUTO_INCREMENT,
    first_name VARCHAR(64) NOT NULL,
    last_name VARCHAR(64) NOT NULL,
    username VARCHAR(64) NOT NULL,
    password VARCHAR(64) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE (username)
);

CREATE TABLE sessions (
    user_id INT NOT NULL,
    token VARCHAR(36) NOT NULL,
    valid_until DATETIME,
    created DATETIME NOT NULL,
    FOREIGN KEY (user_id)
        REFERENCES profiles (id)
);

CREATE TABLE messages (
    id INT NOT NULL AUTO_INCREMENT,
    sender_id INT NOT NULL,
    reciever_id INT,
    message VARCHAR(256),
    PRIMARY KEY (id),
    FOREIGN KEY (sender_id)
        REFERENCES profiles (id),
    FOREIGN KEY (reciever_id)
        REFERENCES profiles (id)
);

CREATE TABLE friendships (
    person_one_id INT NOT NULL,
    person_two_id INT NOT NULL,
    one_last_send DATETIME,
    two_last_send DATETIME,
    PRIMARY KEY (person_one_id , person_two_id),
    FOREIGN KEY (person_one_id)
        REFERENCES profiles (id),
    FOREIGN KEY (person_two_id)
        REFERENCES profiles (id)
);
    
CREATE TABLE campaigns (  
    id INT NOT NULL AUTO_INCREMENT,
    owner_id INT NOT NULL,
    campaign_type ENUM('Tobacco', 'Alcohol', 'Diet'),
    is_hidden BOOLEAN,
    PRIMARY KEY (id),
    FOREIGN KEY (owner_id)
        REFERENCES profiles (id)
);

CREATE TABLE campaign_updates (
    campaign_id INT NOT NULL,
    update_time DATETIME NOT NULL,
    FOREIGN KEY (campaign_id)
        REFERENCES campaigns (id)
);