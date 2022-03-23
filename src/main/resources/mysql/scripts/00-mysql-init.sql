-- init table
DROP TABLE IF EXISTS data_header, data_table, data_sheet, data_template;

CREATE TABLE data_template (
    template_id BIGINT AUTO_INCREMENT,
    template_name VARCHAR(255) NOT NULL,
    date_created DATE NOT NULL,
    PRIMARY KEY (template_id) 
);

CREATE TABLE data_sheet (
    sheet_id BIGINT AUTO_INCREMENT,
    template_id BIGINT NOT NULL,
    sheet_name VARCHAR(255) NOT NULL,
    sheet_order INT NOT NULL,
    PRIMARY KEY (sheet_id),
    FOREIGN KEY (template_id)
        REFERENCES data_template(template_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

CREATE TABLE data_table (
    table_id BIGINT AUTO_INCREMENT,
    sheet_id BIGINT NOT NULL,
    row_num INT NOT NULL,
    col_num INT NOT NULL,
    PRIMARY KEY (table_id),
    FOREIGN KEY (sheet_id)
        REFERENCES data_sheet(sheet_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

CREATE TABLE data_header (
    header_id BIGINT AUTO_INCREMENT,
    table_id BIGINT NOT NULL,
    header_name VARCHAR(255) NOT NULL,
    header_order INT NOT NULL,
    PRIMARY KEY (header_id),
    FOREIGN KEY (table_id)
        REFERENCES data_table(table_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);
