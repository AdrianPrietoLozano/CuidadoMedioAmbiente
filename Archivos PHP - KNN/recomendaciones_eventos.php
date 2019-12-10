<?PHP
require_once("config.php");
require_once("KNN.php");

if(isset($_GET['id_ambientalista']))
{
    $id_ambientalista = $_GET['id_ambientalista'];
    $conexion = mysqli_connect(SERVER, USER, PASSWORD, DB);

    if(!$conexion)
    {
        echo "0";
    }
    else
    {
        $datos = array();
        $etiquetas = array();
        $datosUsuario = array();

        if(existeUsuarioEnTablaKNN($conexion, $id_ambientalista))
        {
            getDatosBD($conexion, $datos, $datosUsuario, $etiquetas, $id_ambientalista);

            $knn = new KNN($datos, $etiquetas);
            $predicciones = $knn->getPredicciones($datosUsuario);

            if(count($predicciones) == 0) // no hay vecinos cercanos
            {
                // recomendar los eventos mas populares
                //echo "<br>sin vecinos<br>";
                echo json_encode(obtenerEventosMasPopulares($conexion, $id_ambientalista));
            }
            else // el algoritmo si encontró vecinos cercanos
            {
                $eventosRecomendados = generarRecomendacionesEventos($conexion, $predicciones);
                if(count($eventosRecomendados) == 0) // no hay recomendaciones
                {
                    //echo "<br>cero recomendaciones<br>";
                    echo json_encode(obtenerEventosMasPopulares($conexion, $id_ambientalista));
                }
                else
                {
                    //echo "<br>generados con el argoritmo<br>";
                    echo json_encode($eventosRecomendados);
                }
            }
        }
        else
        {
            // recomendar los eventos mas populares
            //echo "<br>el usuario no existe en la tabla<br>";
            echo json_encode(obtenerEventosMasPopulares($conexion, $id_ambientalista));
        }
        
        mysqli_close($conexion);
    }
}
else
{
    echo "0";
}


function existeUsuarioEnTablaKNN($conexion, $id_usuario)
{
    $select = "SELECT * FROM KNN WHERE usuario_id = {$id_usuario}";
    if($res = mysqli_query($conexion, $select)){
        $numeroRegistros = mysqli_num_rows($res);

        if($numeroRegistros > 0) // ya existe el usuario
        {
            return true;
        }
        else // no existe el ambientalista
        {
            return false;
        }
    }

    return false; // dudas
}



/* funcion personalizada */
function &obtenerQueryJsonEvento($conexion, $query)
{
    $json = array();
    $resultado = mysqli_query($conexion, $query);
    if($resultado)
    {
        while($registro = mysqli_fetch_array($resultado))
        {
            $res['id_evento'] = $registro["id_evento"];
            $res['titulo'] = $registro['titulo'];
            $res['fecha'] = $registro['fecha'];
            $res['hora'] = $registro['hora'];
            $res['foto'] = $registro['foto'];

            $json['eventos'][]=$res;
        }
    }

    return $json;
}

function &obtenerEventosMasPopulares($conexion, $id_ambientalista)
{
    //SELECT evento_id FROM participa_evento WHERE NOW() <= fecha_hora_fin GROUP BY evento_id ORDER BY COUNT(evento_id) DESC LIMIT 20

    $select = "SELECT evento._id AS id_evento, evento.titulo AS titulo, " .
        "DATE_FORMAT(evento.fecha_hora, '%d-%m-%Y') AS fecha, DATE_FORMAT(evento.fecha_hora, '%H:%i') AS hora, " .
        "reporte.fotografia AS foto " .
        "FROM (evento_limpieza AS evento JOIN reporte_contaminacion AS reporte ON evento.reporte_id = reporte._id) " .
            "WHERE evento._id IN " .
                "(SELECT evento_id FROM participa_evento " .
                    "WHERE NOW() <= fecha_hora_fin GROUP BY evento_id ORDER BY COUNT(evento_id) DESC) " .
                    "AND evento._id NOT IN " .
                        "(SELECT evento_id FROM participa_evento WHERE ambientalista_id = {$id_ambientalista})";

    $json = obtenerQueryJsonEvento($conexion, $select);

    if(count($json) == 0)
    {
        $select2 = "SELECT evento._id AS id_evento, evento.titulo AS titulo, " .
            "DATE_FORMAT(evento.fecha_hora, '%d-%m-%Y') AS fecha, DATE_FORMAT(evento.fecha_hora, '%H:%i') AS hora, " .
            "reporte.fotografia AS foto " .
            "FROM (evento_limpieza AS evento JOIN reporte_contaminacion AS reporte ON evento.reporte_id = reporte._id) " .
            "WHERE NOW() <= evento.fecha_hora LIMIT 10";
        $json = obtenerQueryJsonEvento($conexion, $select2);

        if(count($json) == 0) {
            $json["eventos"] = array();
        }
    }

    return $json;
}


/*NOTA: HACE FALTA HACER QUE CUANDO UN USUARIO NO ESTE EN LA TABLA KNN SE LE RECOMIENDE
OTRA COSA*/
function getDatosBD($conexion, &$datos, &$datosUsuario, &$etiquetas, $idUsuarioActual)
{
    $encontrado = false;
    $consulta = "SELECT * FROM KNN";
    $resultado = mysqli_query($conexion, $consulta);
        
    if($resultado)
    {
        while($registro = mysqli_fetch_array($resultado))
        {
            $arrayUsuario = array(
                $registro["escombro"],
                $registro["envases"],
                $registro["carton"],
                $registro["bolsas"],
                $registro["electricos"],
                $registro["pilas"],
                $registro["neumaticos"],
                $registro["medicamentos"],
                $registro["varios"],
            );

            if($registro["usuario_id"] == $idUsuarioActual)
            {
                $datosUsuario = $arrayUsuario;
                $encontrado = true;
            }
            else
            {
                $etiquetas[] = $registro["usuario_id"];
                $datos[] = $arrayUsuario;
            }   
        }

        if(!$encontrado) // el usuario no ha participado en ningun evento
        {
            // deberia recomendarle los eventos con mas participantes
            $datosUsuario = array(0, 0, 0, 0, 0, 0, 0, 0, 0);
        }
    }
    else
    {
        return false;
    }
    
    return true;
}

function &generarRecomendacionesEventos($conexion, &$idsUsuarios)
{

    $json = array();

    $select2 = "SELECT evento._id AS id_evento, evento.titulo AS titulo, ".
        "DATE_FORMAT(evento.fecha_hora, '%d-%m-%Y') AS fecha, DATE_FORMAT(evento.fecha_hora, '%H:%i') AS hora, " .
        "reporte.fotografia AS foto " .
        "FROM (evento_limpieza AS evento JOIN reporte_contaminacion AS reporte ON evento.reporte_id = reporte._id) ".
        "WHERE evento._id IN (SELECT evento_id FROM participa_evento WHERE NOW() <= fecha_hora_fin AND ";

    $num_elementos = count($idsUsuarios);
    for($i = 0; $i < $num_elementos; $i++) {
        $select2 .= " ambientalista_id=".$idsUsuarios[$i];

        if($i + 1 < $num_elementos){
            $select2 .= " OR ";
        }
    }

    $select2 .= ")";

    $resultado = mysqli_query($conexion, $select2);
    if($resultado)
    {
        while($registro = mysqli_fetch_array($resultado))
        {
            $res['id_evento'] = $registro["id_evento"];
            $res['titulo'] = $registro['titulo'];
            $res['fecha'] = $registro['fecha'];
            $res['hora'] = $registro['hora'];
            $res['foto'] = $registro['foto'];

            $json['eventos'][]=$res;
        }
    }
    else
    {
        $json['eventos'] = array();
    }

    return $json;
}

?>