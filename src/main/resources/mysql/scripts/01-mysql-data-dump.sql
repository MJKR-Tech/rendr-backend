INSERT INTO Template
VALUES
(1, "Complex_1", CURDATE()),
(2, "Complex_2", CURDATE() + 1),
(3, "Complex_3", CURDATE() - 1);

INSERT INTO Sheet
VALUES
(1, 1, "first", 0),
(2, 1, "second", 1),
(3, 1, "third", 2);

INSERT INTO DataTable
VALUES
(1, 1, 0, 0),
(2, 1, 10, 0);

INSERT INTO DataHeader
VALUES
(1, 1, "header_1", 0),
(2, 1, "header_2", 1),
(3, 2, "header_3", 0),
(4, 2, "header_4", 1);