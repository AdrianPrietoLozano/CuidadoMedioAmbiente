<?PHP

require_once("config.php");

if(isset($_GET['id_ambientalista']) && isset($_GET['id_evento']) && isset($_GET['fecha_inicio']) && isset($_GET['hora_inicio']) && isset($_GET['fecha_fin']) && isset($_GET['hora_fin']))
{
	$json = array();

	$id_ambientalista = $_GET["id_ambientalista"];
	$id_evento = $_GET["id_evento"];
	$fecha_inicio = $_GET["fecha_inicio"];
	$hora_inicio = $_GET["hora_inicio"];
	$fecha_fin = $_GET["fecha_fin"];
	$hora_fin = $_GET["hora_fin"];

	$conexion = mysqli_connect(SERVER, USER, PASSWORD, DB);

	if(!$conexion)
	{
		$json["resultado"][] = "0";
	}
	else
	{
		$fecha_hora_inicio = $fecha_inicio." ".$hora_inicio;
		$fecha_hora_fin = $fecha_fin." ".$hora_fin;

		if(yaParticipaEvento($conexion, $id_ambientalista, $id_evento))
		{
			$json["resultado"][] = "2"; // el usuario ya participa en el evento
		}
		else
		{
			$insert = "INSERT INTO participa_evento(ambientalista_id, evento_id, fecha_hora_inicio, fecha_hora_fin) VALUES ({$id_ambientalista}, {$id_evento}, STR_TO_DATE('$fecha_hora_inicio', '%d/%m/%Y %H:%i:%s'), STR_TO_DATE('$fecha_hora_fin', '%d/%m/%Y %H:%i:%s'))";


			$resultado = mysqli_query($conexion, $insert);

			if($resultado)
			{
			    
				$json["resultado"][] = "1";

				actulizarTablaKNN($conexion, $id_evento, $id_ambientalista);
			}
			else
			{
				$json["resultado"][] = "0";
			}	
		}

		mysqli_close($conexion);
	}
}
else
{
	$json["resultado"][] = "0";
}


echo json_encode($json);

/*
http://localhost/EventosLimpieza/insertar_unirse_evento.php?id_ambientalista=1&id_evento=30&fecha_inicio=26-10-2019&hora_inicio=01:46:59&fecha_fin=26-10-2019&hora_fin=01:46:59
*/


//https://adrianpl.000webhostapp.com/EventosLimpieza/insertar_unirse_evento.php?id_ambientalista=1&id_evento=30&fecha_inicio=26/10/2019&hora_inicio=01:46:59&fecha_fin=26/10/2019&hora_fin=01:46:59

function obtenerIdResiduo($conexion, $id_evento)
{
	/*
	$select_tipo_residuo = "SELECT reporte_contaminacion.tipo_residuo_id FROM reporte_contaminacion WHERE reporte_contaminacion._id = (SELECT reporte_id FROM evento_limpieza WHERE evento_limpieza._id = {$id_evento})"; */

	$select_tipo_residuo = "SELECT reporte_contaminacion.tipo_residuo_id FROM reporte_contaminacion JOIN evento_limpieza ON evento_limpieza.reporte_id = reporte_contaminacion._id WHERE evento_limpieza._id = {$id_evento}";	
	
	$resultado = mysqli_query($conexion, $select_tipo_residuo);
	return mysqli_fetch_array($resultado)[0];
}

function existeAmbientalista($conexion, $id)
{
	$select = "SELECT * FROM KNN WHERE usuario_id = {$id}";
	if($res = mysqli_query($conexion, $select)){
	    $numeroRegistros = mysqli_num_rows($res);

		if($numeroRegistros > 0) // ya existe el ambientalista
		{
			return true;
		}
		else // no existe el ambientalista
		{
			return false;
		}
	}

	return true;
}


function yaParticipaEvento($conexion, $id_ambientalista, $id_evento)
{
	$select = "SELECT * FROM participa_evento WHERE evento_id='$id_evento' AND ambientalista_id='$id_ambientalista'";
	if($res = mysqli_query($conexion, $select)){
		$numeroRegistros = mysqli_num_rows($res);

		if($numeroRegistros > 0) {
			return true;
		}
		else {
			return false;
		}
	}

	return true;
}

function actulizarTablaKNN($conexion, $id_evento, $id_ambientalista)
{
	$nombre_columna_residuo = "";
	$residuo = obtenerIdResiduo($conexion, $id_evento);
		

	switch ($residuo) {
		case 1:
			$nombre_columna_residuo = "escombro";
			break;

		case 2:
			$nombre_columna_residuo = "envases";
			break;

		case 3:
			$nombre_columna_residuo = "carton";
			break;

		case 4:
			$nombre_columna_residuo = "bolsas";
			break;

		case 5:
			$nombre_columna_residuo = "electricos";
			break;

		case 6:
			$nombre_columna_residuo = "pilas";
			break;

		case 7:
			$nombre_columna_residuo = "neumaticos";
			break;

		case 8:
			$nombre_columna_residuo = "medicamentos";
			break;

		case 9:
			$nombre_columna_residuo = "varios";
			break;
			
		default:
			# code...
			break;
	}

	if(existeAmbientalista($conexion, $id_ambientalista))
	{
		$update = "UPDATE KNN SET {$nombre_columna_residuo} = {$nombre_columna_residuo} + 1 WHERE usuario_id={$id_ambientalista}";
		mysqli_query($conexion, $update);
	}
	else
	{
		$insertar = "INSERT INTO KNN(usuario_id, {$nombre_columna_residuo}) VALUES({$id_ambientalista}, 1)";
		mysqli_query($conexion, $insertar);
	}
}


?>

