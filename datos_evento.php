<?PHP
/*Debe devolver los datos de un reporte*/

$host = "localhost";
$usuario = "root";
$password = "";
$base_datos = "eventos_limpieza";

if(isset($_GET["evento_id"]))
{
	$evento_id = $_GET['evento_id'];
	$json = array();

	$conexion = mysqli_connect($host, $usuario, $password, $base_datos);

	if(!$conexion)
	{
		echo "0";
	}
	else
	{
		$consulta = "SELECT titulo, nombre_usuario AS creador, descripcion, DATE_FORMAT(fecha_hora, '%d-%m-%Y') AS fecha, DATE_FORMAT(fecha_hora, '%H:%i:%s') AS hora FROM (evento_limpieza JOIN ambientalista ON ambientalista_id = ambientalista._id) WHERE evento_limpieza._id={$evento_id}";
		
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
				$json['datos_evento'][] = $res;
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