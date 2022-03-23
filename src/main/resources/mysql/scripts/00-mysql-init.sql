-- init table
DROP TABLE IF EXISTS Template, Sheet, DataTable, DataHeader;

CREATE TABLE Template (
    template_id BIGINT AUTO_INCREMENT,
    template_name VARCHAR(255) NOT NULL,
    date_created DATE NOT NULL,
    PRIMARY KEY (template_id) 
);

CREATE TABLE Sheet (
    sheet_id BIGINT AUTO_INCREMENT,
    template_id BIGINT NOT NULL,
    sheet_name VARCHAR(255) NOT NULL,
    sheet_order INT NOT NULL,
    PRIMARY KEY (sheet_id),
    FOREIGN KEY (template_id) REFERENCES Template(template_id)
);

CREATE TABLE DataTable (
    table_id BIGINT AUTO_INCREMENT,
    sheet_id BIGINT NOT NULL,
    row_num INT NOT NULL,
    col_num INT NOT NULL,
    PRIMARY KEY (table_id),
    FOREIGN KEY (sheet_id) REFERENCES Sheet(sheet_id)
);

CREATE TABLE DataHeader (
    header_id BIGINT AUTO_INCREMENT,
    table_id BIGINT NOT NULL,
    header_name VARCHAR(255) NOT NULL,
    header_order INT NOT NULL,
    PRIMARY KEY (header_id),
    FOREIGN KEY (table_id) REFERENCES DataTable(table_id)
);
