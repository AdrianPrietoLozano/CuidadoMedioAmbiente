CREATE DATABASE eventos_limpieza;
USE eventos_limpieza;


CREATE TABLE ambientalista
(
	_id 			 INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
	correo			 VARCHAR(50) NOT NULL,
	nombre_usuario	 VARCHAR(30) NOT NULL,
	contrasenia 	 VARCHAR(30) NOT NULL
);


CREATE TABLE evento_limpieza
(
	_id 			 	 INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
	ambientalista_id	 INT NOT NULL,
	titulo 				 VARCHAR(50) NOT NULL,
	reporte_id 			 INT NOT NULL,
	fecha_hora 			 TIMESTAMP NOT NULL,
	descripcion			 VARCHAR(200),
	FOREIGN KEY(ambientalista_id) REFERENCES ambientalista(_id)
);


CREATE TABLE volumen_residuo
(
	_id 	 INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
	volumen  VARCHAR(30) NOT NULL
);


CREATE TABLE tipo_residuo
(
	_id 	 INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
	tipo 	 VARCHAR(30) NOT NULL
);



CREATE TABLE reporte_contaminacion
(
	_id 				 INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
	latitud				 FLOAT(10, 6) NOT NULL,
	longitud			 FLOAT(10, 6) NOT NULL,
	ambientalista_id	 INT NOT NULL,
	fotografia 			 VARCHAR(30),
	descripcion 		 VARCHAR(200),
	fecha_hora 			 TIMESTAMP NOT NULL,
	volumen_id			 INT NOT NULL,
	tipo_residuo_id		 INT NOT NULL,
	FOREIGN KEY(ambientalista_id)	 REFERENCES ambientalista(_id),
	FOREIGN KEY(volumen_id)			 REFERENCES volumen_residuo(_id),
	FOREIGN KEY(tipo_residuo_id)	 REFERENCES tipo_residuo(_id)
);

CREATE TABLE participa_evento
(
	_id					 INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
	ambientalista_id	 INT NOT NULL,
	evento_id			 INT NOT NULL,
	fecha_hora_inicio	 TIMESTAMP NOT NULL,
	fecha_hora_fin		 TIMESTAMP NOT NULL,
	FOREIGN KEY(ambientalista_id)	 REFERENCES ambientalista(_id),
	FOREIGN KEY(evento_id)			 REFERENCES evento_limpieza(_id)
);


CREATE TABLE recomendacion_evento
(
	_id 				 INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
	ambientalista_id	 INT NOT NULL,
	evento_id			 INT NOT NULL,
	FOREIGN KEY(ambientalista_id)	 REFERENCES ambientalista(_id),
	FOREIGN KEY(evento_id) 			 REFERENCES evento_limpieza(_id)
);


CREATE TABLE recomendacion_crear_evento
(
	_id 				 INT PRIMARY KEY AUTO_INCREMENT NOT NULL,
	ambientalista_id	 INT NOT NULL,
	reporte_id			 INT NOT NULL,
	FOREIGN KEY(ambientalista_id)	 REFERENCES ambientalista(_id),
	FOREIGN KEY(reporte_id)			 REFERENCES reporte_contaminacion(_id)
);
