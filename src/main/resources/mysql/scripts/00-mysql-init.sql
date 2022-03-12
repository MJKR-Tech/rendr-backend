DROP TABLE IF EXISTS simple_row;

-- init table
CREATE TABLE simple_row (
	ticker VARCHAR(255) NOT NULL,
	instrumentType VARCHAR(255) DEFAULT NULL,
	coupon DOUBLE DEFAULT NULL,
	originalFace DOUBLE DEFAULT NULL,
	marketValue DOUBLE DEFAULT NULL,
	isin VARCHAR(255) DEFAULT NULL,
	portfolio VARCHAR(255) DEFAULT NULL,
	maturityDate VARCHAR(255) DEFAULT NULL,
	price DOUBLE DEFAULT NULL,
	positionDate VARCHAR(255) DEFAULT NULL,
	currentFace INT DEFAULT NULL,
	currency VARCHAR(255) DEFAULT NULL,
	contractCode VARCHAR(255) DEFAULT NULL,
	PRIMARY KEY (ticker)
);

-- sample value
INSERT INTO simple_row
VALUES (
    "TB",
    "TBILL",
    2.5,
    121000000,
    125817070.5,
    "US000000A002",
    "100001",
    "5/1/2021",
    103.98,
    "20210730",
    121000000,
    "USD",
    null
);
