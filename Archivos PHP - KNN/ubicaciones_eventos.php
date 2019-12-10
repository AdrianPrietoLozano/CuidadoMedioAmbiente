<?PHP

require_once("config.php");
$conexion = mysqli_connect(SERVER, USER, PASSWORD, DB);

$json = array();

if(!$conexion)
{
	echo "0";
}
else
{
	$consulta = "SELECT E._id AS id_evento, R.latitud, R.longitud FROM evento_limpieza AS E JOIN reporte_contaminacion AS R ON E.reporte_id = R._id WHERE NOW() <= E.fecha_hora";

	$resultado = mysqli_query($conexion, $consulta);

	if($resultado)
	{
		while($registro = mysqli_fetch_array($resultado))
		{
			$res['id_evento'] = $registro['id_evento'];
			$res['latitud'] = $registro['latitud'];
			$res['longitud'] = $registro['longitud'];

			$json['ubicacion'][]=$res;
		}
		mysqli_close($conexion);
		echo json_encode($json);
	}
	else
	{
		echo "0";
	}
}

?>