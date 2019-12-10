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
	$consulta = "SELECT * FROM KNN";

	$resultado = mysqli_query($conexion, $consulta);

	if($resultado)
	{
		while($registro = mysqli_fetch_array($resultado))
		{
			$res['usuario_id'] = $registro["usuario_id"];
			$res['escombro'] = $registro["escombro"];
			$res['envases'] = $registro["envases"];
			$res['carton'] = $registro["carton"];
			$res['bolsas'] = $registro["bolsas"];
			$res['electricos'] = $registro["electricos"];
			$res['pilas'] = $registro["pilas"];
			$res['neumaticos'] = $registro["neumaticos"];
			$res['medicamentos'] = $registro["medicamentos"];
			$res['varios'] = $registro["varios"];

			$json['datos'][] = $res;
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