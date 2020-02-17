<?PHP

require_once("config.php");


if(isset($_GET['ambientalista_id']) && isset($_GET['reporte_id']) && isset($_GET['titulo'])
	&& isset($_GET['fecha']) && isset($_GET['hora']) && isset($_GET['descripcion']))
{
	$ambientalista_id = $_GET['ambientalista_id'];
	$reporte_id = $_GET['reporte_id'];
	$titulo = $_GET['titulo'];
	$fecha = $_GET['fecha'];
	$hora = $_GET['hora'];
	$descripcion = $_GET['descripcion'];

	$conexion = mysqli_connect(SERVER, USER, PASSWORD, DB);

	if(!$conexion)
	{
		echo "0";
		exit();
	}
	else
	{

		if(reporteYaTieneEvento($conexion, $reporte_id))
		{
			echo "2"; // ya existe el evento para ese reporte	
		}
		else
		{
			if(insertarEvento($conexion, $ambientalista_id, $reporte_id, $titulo, $fecha, $hora, $descripcion)) {
				echo "1";
			} else {
				echo "0";
			}
		}	

		mysqli_close($conexion);
	}

}
else
{
	echo "0";
}


function insertarEvento($conexion, $id_ambientalista, $reporte_id, $titulo, $fecha, $hora, $descripcion)
{
	$fecha_hora = $fecha." ".$hora;
	$insert = "INSERT INTO evento_limpieza(ambientalista_id, titulo, reporte_id, fecha_hora, descripcion) VALUES({$id_ambientalista}, '$titulo', {$reporte_id}, STR_TO_DATE('$fecha_hora', '%d/%m/%Y %H:%i'), '$descripcion')";

		//echo $insert;
	$resultado = mysqli_query($conexion, $insert);

	if($resultado) {
		return true;	
	} else {
		return false;
	}
}


function reporteYaTieneEvento($conexion, $reporte_id)
{
	$select = "SELECT * FROM evento_limpieza WHERE reporte_id = {$reporte_id}";
	if($res = mysqli_query($conexion, $select)){
	    $numeroRegistros = mysqli_num_rows($res);

		if($numeroRegistros > 0) // ya existe el evento
		{
			return true;
		}
		else // no existe el evento
		{
			return false;
		}
	}

	return true; // dudas
}

?>