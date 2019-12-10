<?PHP
require_once("config.php");


if(isset($_GET["evento_id"]))
{
	$evento_id = $_GET['evento_id'];
	$json = array();

	$conexion = mysqli_connect(SERVER, USER, PASSWORD, DB);

	if(!$conexion)
	{
		echo "0";
	}
	else
	{
		$consulta = "SELECT E.titulo, nombre_usuario AS creador, E.descripcion, tipo_residuo.tipo, " .
			"DATE_FORMAT(E.fecha_hora, '%d/%m/%Y') AS fecha, " .
			"DATE_FORMAT(E.fecha_hora, '%H:%i:%s') AS hora, R.fotografia AS foto " .
			"FROM (evento_limpieza AS E JOIN ambientalista ON E.ambientalista_id = ambientalista._id " .
				"JOIN reporte_contaminacion AS R ON E.reporte_id = R._id " .
				"JOIN tipo_residuo ON R.tipo_residuo_id = tipo_residuo._id) " .
				"WHERE E._id={$evento_id}";
		
		$consultaNumPersonasUnidas = "SELECT COUNT(*) AS personas_unidas " .
			"FROM participa_evento " . 
			"WHERE evento_id={$evento_id} AND NOW() <= fecha_hora_fin";
		
		$resultado = mysqli_query($conexion, $consulta);

		if($resultado)
		{
			if($registro = mysqli_fetch_array($resultado))
			{
				$res['titulo'] = $registro["titulo"];
				$res['creador'] = $registro["creador"];
				$res['descripcion'] = $registro["descripcion"];
				$res['fecha'] = $registro["fecha"];
				$res['hora'] = $registro["hora"];
				$res['foto'] = $registro["foto"];
				$res['residuo'] = $registro["tipo"];
				$json['datos_evento'][] = $res;
			}

			$resNumPersonasUnidas = mysqli_query($conexion, $consultaNumPersonasUnidas);
			if($resNumPersonasUnidas)
			{
				$registroPerUnidas = mysqli_fetch_array($resNumPersonasUnidas);
				$json['personas_unidas'] = $registroPerUnidas["personas_unidas"];
			}
			else
			{
				$json['personas_unidas'] = 0;
			}

			echo json_encode($json);
		}
		else
		{
			echo "0";
		}

		mysqli_close($conexion);
	}
}


?>