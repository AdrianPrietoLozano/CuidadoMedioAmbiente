<?php
use Psr\Http\Message\ServerRequestInterface as Request;
use Psr\Http\Message\ResponseInterface as Response;
use Slim\Http\UploadedFile;
use Slim\App;

require  __DIR__ . "/../tablesDB/ReporteDB.php";
require_once __DIR__ . "/../actualizarKNN.php";

return function(App $app) {


    $app->get('/reportes', function( Request $request, Response $response, array $args) {
        $reporteDB = new ReporteDB($this->db);
        $resultado = $reporteDB->findAll();

        return $response->withJson($resultado);
    });


    $app->get("/reportes/{id:[0-9]+}", function(Request $request, Response $response, array $args) {
        $json = array();
        $resultado = "0";
        $mensaje = "Ocurrió un error";

        $reporteDB = new ReporteDB($this->db);
        $reporte = $reporteDB->find($args["id"]);

        if ($reporte) {
            $json["reporte"] = $reporte;

            $eventoDB = new EventoDB($this->db);
            $json["reporte"]["tiene_evento"] = $eventoDB->existeEventoConReporte($args["id"]);
            $json["reporte"]["tiene_limpieza"] = $reporteDB->reporteTieneLimpieza($args["id"]);

            $resultado = "1";
            $mensaje = "Operación éxitosa";
        }

        $json["estatus"]["resultado"] = $resultado;
        $json["estatus"]["mensaje"] = $mensaje;

        return $response->withJson($json); 
    });


    $app->post("/reportes", function(Request $request, Response $response, array $args) {
        $json = array();
        $resultado = "0";
        $mensaje = "Ocurrió un error";

        $queryParams = array("latitud", "longitud", "ambientalista_id", "volumen", "residuos", "descripcion");

        $uploadedFile = $request->getUploadedFiles()["file"] ?? null;
        $fileName = "";
        if (!empty($uploadedFile) && $uploadedFile->getError() === UPLOAD_ERR_OK) {
            $extension = strtolower(pathinfo($uploadedFile->getClientFileName(), PATHINFO_EXTENSION));
            $nombreBase = bin2hex(random_bytes(8));
            $directory = $this->get("images_directory");
            $fileName = $directory . DIRECTORY_SEPARATOR . $nombreBase . "." . $extension;

            $uri = $request->getUri();
            if (comprobarBodyParams($request, $queryParams)) {
                $reporteDB = new reporteDB($this->db);

                $latitud = $request->getParsedBodyParam($queryParams[0]);
                $longitud = $request->getParsedBodyParam($queryParams[1]);
                $ambientalistaId = $request->getParsedBodyParam($queryParams[2]);
                $volumen = $request->getParsedBodyParam($queryParams[3]);
                $residuos = $request->getParsedBodyParam($queryParams[4]);
                $descripcion = $request->getParsedBodyParam($queryParams[5]);

                $idReporte = $reporteDB->insert($latitud, $longitud, $ambientalistaId,
                    $fileName, $descripcion, $volumen, $residuos);

                if ($idReporte != -1) {
                    $correcto = false;
                    try {
                        $uploadedFile->moveTo($fileName);
                        $resultado = "1";
                        $mensaje = "Exito";
                        $json["id_reporte"] = $idReporte;
                        $json["url_imagen"] = $fileName;
                        $correcto = true;
                    } catch (Exception $e) {
                        //echo $e->getMessage();
                        $resultado = "0";
                        $mensaje = "Error al guardar la imagen";
                        $reporteDB->delete($idReporte);
                        $correcto = false;
                    }

                    if ($correcto) {
                        actualizarKnn($this->db, $ambientalistaId, $residuos, $volumen);
                    }
                }
            }
        }

        $json["resultado"] = $resultado;
        $json["mensaje"] = $mensaje;

        return $response->withJson($json); 

    });

    $app->get('/reportes/usuario/{idUsuario:[0-9]+}', function( Request $request, Response $response, array $args) {
        $reporteDB = new ReporteDB($this->db);
        $resultado = $reporteDB->findAllReportesUsuario($args["idUsuario"]);

        return $response->withJson($resultado);
    });
}



?>

