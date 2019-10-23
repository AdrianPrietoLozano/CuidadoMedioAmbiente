<?PHP
$l = "localhost";
$b = "eventos_limpieza";
$u = "root";
$p = "";


if(isset($_GET['ambientalista']) && isset($_GET['reporte_id']) && isset($_GET['titulo'])
	&& isset($_GET['fecha']) && isset($_GET['hora']) && isset($_GET['descripcion']))
{
	$ambientalista = $_GET['ambientalista'];
	$reporte_id = $_GET['reporte_id'];
	$titulo = $_GET['titulo'];
	$fecha = $_GET['fecha'];
	$hora = $_GET['hora'];
	$descripcion = $_GET['descripcion'];

	$conexion = mysqli_connect($l, $u, $p, $b);

	if(!$conexion)
	{
		echo "0";
		exit();
	}
	else
	{
		$fecha_hora = $fecha." ".$hora;
		$insert = "INSERT INTO evento_limpieza(ambientalista_id, titulo, reporte_id, fecha_hora, descripcion) VALUES({$ambientalista}, '$titulo', {$reporte_id}, '$fecha_hora', '$descripcion')";

		//echo $insert;
		$resultado = mysqli_query($conexion, $insert);

		if($resultado)
		{
			echo "1";	
		}
		else
		{
			//echo mysqli_errno($conexion) . ": " . mysqli_error($conexion) . "\n";
			echo "0";
		}

		mysqli_close($conexion);
	}

}
else
{
	echo "0";
}

?>