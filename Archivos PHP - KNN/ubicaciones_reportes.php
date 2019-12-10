<?PHP
require_once("config.php");

$json = array();

$conexion = mysqli_connect(SERVER, USER, PASSWORD, DB);

if(!$conexion)
{
	echo "0";
}
else
{
	$consulta = "SELECT R._id AS id_reporte, latitud, longitud FROM reporte_contaminacion AS R";
	
	$consultaNumReportes = "SELECT COUNT(_id) AS num_reportes FROM reporte_contaminacion";
	$resultado = mysqli_query($conexion, $consulta);

	if($resultado)
	{
		while($registro = mysqli_fetch_array($resultado))
		{
			$res['id_reporte'] = $registro['id_reporte'];
			$res['latitud'] = $registro['latitud'];
			$res['longitud'] = $registro['longitud'];
			$json['reportes'][]=$res;
		}

		$resultadoNumReportes = mysqli_query($conexion, $consultaNumReportes);
		if($resultadoNumReportes)
		{
			$registroNumReportes = mysqli_fetch_array($resultadoNumReportes);
			$json['num_reportes'] = $registroNumReportes["num_reportes"];
		}
		else{
			$json['num_reportes'] = 0;
		}

		echo json_encode($json);
	}
	else
	{
		echo "0";
	}
	mysqli_close($conexion);
}

?>