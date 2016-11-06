DROP TABLE IF EXISTS sessions;

CREATE TABLE sessions (
    user_id INT NOT NULL,
    token VARCHAR(36) NOT NULL,
    valid_until DATETIME,
    created DATETIME NOT NULL,
    FOREIGN KEY (user_id)
        REFERENCES profiles (id) 
)