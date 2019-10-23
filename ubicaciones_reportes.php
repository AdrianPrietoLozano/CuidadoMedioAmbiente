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
	$consulta = "SELECT R._id AS id_reporte, latitud, longitud FROM reporte_contaminacion AS R";
	$resultado = mysqli_query($conexion, $consulta);

	if($resultado)
	{
		while($registro = mysqli_fetch_array($resultado))
		{
			$json['reportes'][]=$registro;
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