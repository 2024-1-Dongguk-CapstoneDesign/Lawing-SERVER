CREATE TABLE IF NOT EXISTS member(

    member_id BIGINT AUTO_INCREMENT,
    name VARCHAR(10) NOT NULL ,
    email VARCHAR(30) NOT NULL ,
    birth VARCHAR(10) NOT NULL ,
    gender VARCHAR(10) NOT NULL ,
    created_date DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    modified_date DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (member_id)

);

CREATE TABLE IF NOT EXISTS member_token(

    member_token_id BIGINT AUTO_INCREMENT,
    member_id BIGINT,
    access_token VARCHAR(255) NOT NULL ,
    access_expiration DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    refresh_token VARCHAR(255) NOT NULL ,
    refresh_expiration DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    PRIMARY KEY (member_token_id),
    FOREIGN KEY (member_id) REFERENCES member(member_id)
);

CREATE TABLE IF NOT EXISTS license(

    license_id BIGINT AUTO_INCREMENT,
    member_id BIGINT,
    owner_name VARCHAR(10) NOT NULL ,
    license_number VARCHAR(12) NOT NULL ,
    license_birth VARCHAR(6) NOT NULL ,
    serial_number VARCHAR(6) NOT NULL,
    created_date DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    modified_date DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    PRIMARY KEY (license_id),
    FOREIGN KEY (member_id) REFERENCES member(member_id)

)