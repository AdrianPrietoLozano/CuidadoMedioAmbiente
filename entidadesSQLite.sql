CREATE TABLE ambientalista
(
	_id 			INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
	correo			TEXT NOT NULL,
	nombre_usuario	TEXT NOT NULL,
	contrasenia 	TEXT NOT NULL
);


CREATE TABLE evento_limpieza
(
	_id 			 	INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
	ambientalista_id	INTEGER NOT NULL,
	titulo 				TEXT NOT NULL,
	reporte_id 			INTEGER NOT NULL,
	fecha_hora 			TEXT NOT NULL,
	descripcion			TEXT,
	FOREIGN KEY(ambientalista_id) REFERENCES ambientalista(_id)
);


CREATE TABLE volumen_residuo
(
	_id 	INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
	volumen TEXT NOT NULL,
);


CREATE TABLE tipo_residuo
(
	_id 	INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
	tipo 	TEXT NOT NULL,
);


CREATE TABLE reporte_contaminacion
(
	_id 				INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
	ubicacion 			TEXT NOT NULL,
	ambientalista_id	INTEGER NOT NULL,
	fotografia 			TEXT,
	descripcion 		TEXT,
	fecha_hora 			TEXT NOT NULL,
	volumen_id			INTEGER NOT NULL,
	tipo_residuo_id		INTEGER NOT NULL
	FOREIGN KEY(ambientalista_id)	REFERENCES ambientalista(_id),
	FOREIGN KEY(volumen_id)			REFERENCES volumen_residuo(_id),
	FOREIGN KEY(tipo_residuo_id)	REFERENCES tipo_residuo(_id)
);

CREATE TABLE participa_evento
(
	_id					INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
	ambientalista_id	INTEGER NOT NULL,
	evento_id			INTEGER NOT NULL,
	fecha_hora_inicio	TEXT NOT NULL,
	fecha_hora_fin		TEXT NOT NULL,
	FOREIGN KEY(ambientalista_id)	REFERENCES ambientalista(_id),
	FOREIGN KEY(evento_id)			REFERENCES evento_limpieza(_id)
);


CREATE TABLE recomendacion_evento
(
	_id 				INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
	ambientalista_id	INTEGER NOT NULL,
	evento_id			INTEGER NOT NULL,
	FOREIGN KEY(ambientalista_id)	REFERENCES ambientalista(_id),
	FOREIGN KEY(evento_id) 			REFERENCES evento_limpieza(_id)
);


CREATE TABLE recomendacion_crear_evento
(
	_id 				INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
	ambientalista_id	INTEGER NOT NULL,
	reporte_id			INTEGER NOT NULL,
	FOREIGN KEY(ambientalista_id)	REFERENCES ambientalista(_id),
	FOREIGN KEY(reporte_id)			REFERENCES reporte_contaminacion(_id)
);
