<?PHP
$l = "localhost";
$b = "prueba";
$u = "root";
$p = "";

$dato = $_GET["dato"];
$conexion = mysqli_connect($l, $u, $p, $b);

if(!$conexion)
{
	echo "no se conecto";
}
else
{

$inset = "INSERT INTO tabla1(nombre) VALUES ('{$dato}')";
$resultado = mysqli_query($conexion, $inset);

if($resultado)
{
	echo "exito";
}
else
{
	echo "fallo";
}

mysqli_close($conexion);
}

?>