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