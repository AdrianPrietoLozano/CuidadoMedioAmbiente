<?PHP
/*Debe devolver los id de los eventos y su ubicación(latitud y longitud)*/

$host = "localhost";
$usuario = "root";
$password = "";
$base_datos = "eventos_limpieza";

$json = array();

$conexion = mysqli_connect($host, $usuario, $password, $base_datos);

if(!$conexion)
{
	echo "0";
}
else
{
	$consulta = "SELECT e._id AS id_evento, latitud, longitud FROM evento_limpieza as e JOIN reporte_contaminacion as r ON e.reporte_id = r._id";
	$resultado = mysqli_query($conexion, $consulta);

	if($resultado)
	{
		while($registro = mysqli_fetch_array($resultado))
		{
			$json['ubicacion'][]=$registro;
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