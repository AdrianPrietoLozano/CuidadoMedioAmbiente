INSERT INTO ambientalista(correo, nombre_usuario,
contrasenia)
VALUES
("adrian@gmail.com", "adrian", "12345");

INSERT INTO volumen_residuo(volumen)
VALUES
("cabe en un contenedor");

INSERT INTO tipo_residuo(tipo)
VALUES
("Escombros");


INSERT INTO reporte_contaminacion(
latitud, longitud, ambientalista_id, fotografia,
descripcion, fecha_hora, volumen_id, tipo_residuo_id)
VALUES 

(20.671434, -103.350885, 1, 'basura2.png', "descripcion", NOW(), 1, 1), 
(20.671273, -103.357194, 1, 'basura2.png', "descripcion", NOW(), 1, 1), 
(20.668814, -103.357280, 1, 'basura2.png', "descripcion", NOW(), 1, 1), 
(20.666184, -103.356518, 1, 'basura2.png', "descripcion", NOW(), 1, 1), 
(20.667991, -103.349909, 1, 'basura2.png', "descripcion", NOW(), 1, 1), 
(20.679506, -103.351679, 1, 'basura2.png', "descripcion", NOW(), 1, 1), 
(20.672961, -103.346057, 1, 'basura2.png', "descripcion", NOW(), 1, 1), 
(20.672720, -103.350509, 1, 'basura2.png', "descripcion", NOW(), 1, 1), 
(20.675771, -103.351614, 1, 'basura2.png', "descripcion", NOW(), 1, 1), 
(20.670909, -103.353663, 1, 'basura2.png', "descripcion", NOW(), 1, 1), 
(20.681975, -103.350638, 1, 'basura2.png', "descripcion", NOW(), 1, 1), 
(20.6734317,-103.362075, 1, 'basura2.png', "descripcion", NOW(), 1, 1), 
(20.675771, -103.351614, 1, 'basura2.png', "descripcion", NOW(), 1, 1), 
(20.680369, -103.348063, 1, 'basura2.png', "descripcion", NOW(), 1, 1), 
(20.672887, -103.353631, 1, 'basura2.png', "descripcion", NOW(), 1, 1), 
(20.675978, -103.354478, 1, 'basura2.png', "descripcion", NOW(), 1, 1);


INSERT INTO evento_limpieza(
ambientalista_id, titulo, reporte_id, fecha_hora, descripcion)
VALUES
(1, "Título del evento", 1, NOW(), "Descripcion"),//
(1, "Título del evento", 2, NOW(), "Descripcion"),
(1, "Título del evento", 3, NOW(), "Descripcion"),//
(1, "Título del evento", 4, NOW(), "Descripcion"),
(1, "Título del evento", 5, NOW(), "Descripcion"),
(1, "Título del evento", 6, NOW(), "Descripcion"),//
(1, "Título del evento", 7, NOW(), "Descripcion"),
(1, "Título del evento", 8, NOW(), "Descripcion"),
(1, "Título del evento", 9, NOW(), "Descripcion"),
(1, "Título del evento", 10, NOW(), "Descripcion"),
(1, "Título del evento", 11, NOW(), "Descripcion"),
(1, "Título del evento", 12, NOW(), "Descripcion"),
(1, "Título del evento", 13, NOW(), "Descripcion"),
(1, "Título del evento", 14, NOW(), "Descripcion"),
(1, "Título del evento", 15, NOW(), "Descripcion"),
(1, "Título del evento", 16, NOW(), "Descripcion");


INSERT INTO recomendacion_evento(ambientalista_id, evento_id)
VALUES(1, 3),(1, 6), (1, 1), (2, 7), (2, 6), (3, 8);






SELECT e._id,
latitud, longitud
FROM evento_limpieza as e
JOIN reporte_contaminacion as r
ON e.reporte_id = r._id


SELECT evento_limpieza._id, latitud, longitud,
	CASE WHEN evento_limpieza._id = recomendacion_evento.evento_id
			AND 1 = recomendacion_evento.ambientalista_id
				THEN 1
		ELSE 0
	END AS recomendado
	FROM evento_limpieza
	JOIN reporte_contaminacion as r
	ON evento_limpieza.reporte_id = r._id
	JOIN recomendacion_evento
	ON evento_limpieza._id = recomendacion_evento.evento_id


SELECT id_evento, latitud, longitud,
	CASE WHEN recomendacion_evento.ambientalista_id = 1 AND 
		recomendacion_evento.evento_id = id_evento THEN 1
		ELSE 0
	END AS recomendado

FROM

(SELECT evento_limpieza._id AS id_evento, latitud, longitud
	FROM evento_limpieza JOIN reporte_contaminacion
		ON evento_limpieza.reporte_id = reporte_contaminacion._id) AS temporal, recomendacion_evento




(SELECT evento_limpieza._id, latitud, longitud,
	CASE WHEN recomendacion_evento.ambientalista_id = 1
	THEN 1 ELSE 0 END AS recomendado
	FROM evento_limpieza JOIN reporte_contaminacion
		ON evento_limpieza.reporte_id = reporte_contaminacion._id
		JOIN recomendacion_evento ON evento_limpieza._id = recomendacion_evento.evento_id

 FROM
	evento_limpieza WHERE evento_limpieza._id = recomendacion_evento.evento_id
	AND recomendacion_evento.ambientalista_id = 1)





SELECT id_evento, latitud, longitud,
	
FROM recomendacion_evento as recomendacion
WHERE recomendacion.ambientalista_id = 1

JOIN


SELECT e._id as evento_id, latitud, longitud,
CASE WHEN recomendacion.evento_id = evento_id
		THEN 1 ELSE 0 END AS recomendado

FROM evento_limpieza AS e JOIN reporte_contaminacion AS reporte
	ON e.reporte_id = reporte._id


	JOIN recomendacion_evento AS recomendacion
	ON recomendacion.ambientalista_id = 1



SELECT id_evento, latitud, longitud,
CASE WHEN recomendacion_evento.ambientalista_id = 1
		THEN 1 ELSE 0 END AS recomendados

FROM
(SELECT evento_limpieza._id AS id_evento, latitud, longitud
	FROM evento_limpieza JOIN reporte_contaminacion
		ON evento_limpieza.reporte_id = reporte_contaminacion._id) AS c1

	JOIN

	recomendacion_evento ON recomendacion_evento.evento_id = id_evento




(SELECT evento_id, ambientalista_id FROM recomendacion_evento) AS temporal ON
temporal.evento_id = evento_limpieza._id









SELECT evento_limpieza._id, latitud, longitud,
	CASE WHEN recomendacion_evento.evento_id = evento_limpieza._id
	THEN 1 ELSE 0 END AS recomendado
	FROM evento_limpieza INNER JOIN recomendacion_evento
	ON recomendacion_evento.ambientalista_id = 1 WHERE evento_limpieza._id = recomendacion_evento.evento_id
		INNER JOIN reporte_contaminacion ON evento_limpieza.reporte_id = reporte_contaminacion._id




SELECT E._id, R.latitud, R.longitud, RE.ambientalista_id
FROM evento_limpieza AS E
JOIN reporte_contaminacion AS R
ON R._id = E.reporte_id
JOIN recomendacion_evento AS RE
ON E._id = RE.evento_id


SELECT E._id as id_reporte, E.reporte_id, RE.ambientalista_id
FROM evento_limpieza AS E
JOIN recomendacion_evento AS RE
ON E._id = RE.evento_id
JOIN
(
	reporte_contaminacion AS R ON id_reporte = R._id
)







SELECT E._id, E.reporte_id, RE.ambientalista_id