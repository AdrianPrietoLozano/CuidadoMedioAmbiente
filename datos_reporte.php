<?PHP
/*Debe devolver los datos de un reporte*/

$host = "localhost";
$usuario = "root";
$password = "";
$base_datos = "eventos_limpieza";

if(isset($_GET["reporte_id"]))
{
	$reporte_id = $_GET['reporte_id'];
	$json = array();

	$conexion = mysqli_connect($host, $usuario, $password, $base_datos);

	if(!$conexion)
	{
		echo "0";
	}
	else
	{
		$consulta = "SELECT DATE_FORMAT(fecha_hora, '%d-%m-%Y') AS fecha, DATE_FORMAT(fecha_hora, '%H:%i:%s') AS hora, tipo, volumen, descripcion, nombre_usuario FROM (reporte_contaminacion JOIN tipo_residuo ON tipo_residuo_id = tipo_residuo._id JOIN volumen_residuo ON volumen_id = volumen_residuo._id JOIN ambientalista ON ambientalista_id = ambientalista._id) WHERE reporte_contaminacion._id =".$reporte_id;

		$resultado = mysqli_query($conexion, $consulta);

		if($resultado)
		{
			if($registro = mysqli_fetch_array($resultado))
			{
				$json['datos_reporte'][] = $registro;
			}

			echo json_encode($json);
		}
		else
		{
			echo "err1";
		}

		mysqli_close($conexion);
	}
}


?>