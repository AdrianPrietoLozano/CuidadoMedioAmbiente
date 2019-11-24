CREATE TABLE KNN(
	usuario_id INT PRIMARY KEY NOT NULL,
	escombro INT DEFAULT 0,
	envases INT DEFAULT 0,
	carton INT DEFAULT 0,
	bolsas INT DEFAULT 0,
	electricos INT DEFAULT 0,
	pilas INT DEFAULT 0,
	neumaticos INT DEFAULT 0,
	medicamentos INT DEFAULT 0,
	varios INT DEFAULT 0
);


INSERT INTO KNN VALUES(5, 1, 2, 3, 4, 5, 6, 7, 8, 9),
(23, 6, 2, 3, 4, 2, 5, 7, 8, 6),
(26, 1, 2, 7, 5, 2, 2, 2, 2, 2),
(12, 1, 0, 0, 0, 0, 0, 8, 0, 8),
(89, 1, 2, 3, 4, 0, 5, 1, 8, 6),
(45, 0, 2, 3, 7, 2, 1, 7, 8, 6),
(49, 7, 4, 3, 5, 6, 5, 7, 8, 6),
(56, 6, 65, 89, 4, 5, 5, 7, 8, 6),
(46, 4, 2, 0, 0, 12, 5, 7, 8, 6),
(77, 0, 0, 0, 0, 0, 0, 0, 0, 0);