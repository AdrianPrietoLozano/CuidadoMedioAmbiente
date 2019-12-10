<?PHP

require_once("config.php");

$json = array();

if(isset($_GET["id_ambientalista"]))
{
	$id_ambientalista = $_GET["id_ambientalista"];

	$conexion = mysqli_connect(SERVER, USER, PASSWORD, DB);

	if(!$conexion)
	{
		$json["datos"] = array();
	}
	else
	{
		$consulta = "SELECT E.titulo, A.nombre_usuario AS creador, E.descripcion, DATE_FORMAT(P.fecha_hora_fin, '%d/%m/%Y') AS fecha, DATE_FORMAT(P.fecha_hora_fin, '%H:%i') AS hora, R.fotografia AS foto, RESIDUO.tipo FROM (evento_limpieza AS E JOIN participa_evento AS P ON P.ambientalista_id='$id_ambientalista' AND NOW() <= P.fecha_hora_fin AND P.evento_id = E._id JOIN reporte_contaminacion AS R ON E.reporte_id = R._id JOIN tipo_residuo AS RESIDUO ON RESIDUO._id = R.tipo_residuo_id JOIN ambientalista AS A ON E.ambientalista_id = A._id)";

		$resultado = mysqli_query($conexion, $consulta);

		if($resultado)
		{
			while($registro = mysqli_fetch_array($resultado))
			{
				$res['titulo'] = $registro["titulo"];
				$res['creador'] = $registro["creador"];
				$res['descripcion'] = $registro["descripcion"];
				$res['fecha'] = $registro["fecha"];
				$res['hora'] = $registro["hora"];
				$res['foto'] = $registro["foto"];
				$res['tipo'] = $registro["tipo"];

				$json['datos'][] = $res;
			}

			if(count($json) == 0) {
				$json["datos"] = array();
			}
		}
		else
		{
			$json["datos"] = array();
		}
		mysqli_close($conexion);
	}
}
else
{
	$json["datos"] = array();
}

echo json_encode($json);
?>