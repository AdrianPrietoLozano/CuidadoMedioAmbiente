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
(1, "Título del evento", 1, NOW(), "Descripcion"),
(1, "Título del evento", 2, NOW(), "Descripcion"),
(1, "Título del evento", 3, NOW(), "Descripcion"),
(1, "Título del evento", 4, NOW(), "Descripcion"),
(1, "Título del evento", 5, NOW(), "Descripcion"),
(1, "Título del evento", 6, NOW(), "Descripcion"),
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


SELECT e._id,
latitud, longitud
FROM evento_limpieza as e
JOIN reporte_contaminacion as r
ON e.reporte_id = r._id