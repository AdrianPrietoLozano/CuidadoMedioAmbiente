<?php
use Psr\Http\Message\ServerRequestInterface as Request;
use Psr\Http\Message\ResponseInterface as Response;
use Slim\App;

require_once __DIR__ . "/../tablesDB/UsuarioDB.php";
require_once __DIR__ . "/../config/config.php";
require_once __DIR__ . "/../utilidades.php";

return function(App $app) {

	$app->get("/usuario/{id:[0-9]+}", function(Request $request, Response $response, array $args) {
		$resultado = "0";
		$mensaje = "Ocurrió un error";
		$json = array();
		
		$userDB = new UsuarioDB($this->db);
		if ($userDB->existeUsuario($args["id"])) {
			$json = $userDB->find($args["id"]);
			if ($json) {
				$resultado = "1";
				$mensaje = "Datos correctos";
			} else {
				$resultado = "0";
				$mensaje = "Ocurrió un error";
			}
		} else {
			$resultado = "2";
			$mensaje = "No existe el usuario";
		}

		$json["resultado"] = $resultado;
		$json["mensaje"] =  $mensaje;

        return $response->withJson($json);
    });

    $app->post('/usuario/login', function(Request $request, Response $response, array $args) {
    	$res = "resultado";
    	$msg = "mensaje";
    	$json = array();

    	if (!comprobarBodyParams($request, ["email", "contrasenia"])) {
    		return $response->withJson([$res => "0", $msg => "Datos incompletos"]);
    	}

    	$email = $request->getParsedBodyParam("email");
    	$contrasenia = $request->getParsedBodyParam("contrasenia");
    	$userDB = new UsuarioDB($this->db);

		if (!$userDB->existeEmail($email)) {
			return $response->withJson([$res => "2", $msg => "Email no registrado"]);
    	}

    	$json = $userDB->findByCredenciales($email, $contrasenia);
    	if (empty($json)) {
    		return $response->withJson([$res => "3", $msg => "Ocurrió un error"]);	
    	}

    	$json[$res] = "1";
    	$json[$msg]= "Inicio de sesión exitoso";

    	return $response->withJson($json);

    });

    $app->post('/usuario/google/login', function(Request $request, Response $response, array $args) {

    	$resultado = "0";
    	$mensaje = "Ocurrió un error";

    	$id_token = $request->getParsedBody()["id_token"] ?? null;

    	if (is_null($id_token)){
    		return $response->withJson(["resultado" => "0", "mensaje" => "No se recibió el id token"]);
    	}

    	$client = new Google_Client(["client_id" => CLIENT_ID]);
    	try {
			$playload = $client->verifyIdtoken($id_token);
		} catch (Exception $e) {
			$playload = array();
		}

		if ($playload) {
			$userDB = new UsuarioDB($this->db);
    		$userid = $playload["sub"];        

    		$idInsertado = -1;
	    	if (!$userDB->existeUsuarioGoogle($userid)) { // si no existe el usuario
	    		$idInsertado = $userDB->insert($playload["email"], $playload["name"], null, $playload["sub"]); // insertar usuario

	    		if ($idInsertado == -1) {
	    			return $response->withJson(["resultado" => "0", "mensaje" => "Error al insertar el usuario"]);
	    		}
	    	}

	    	$json = $userDB->findByGoogleID($userid);
	    	if ($json) {
	    		$resultado = "1";
	    		$mensaje = "Operación éxitosa";
	    	} else {
	    		$resultado = "0";
	    		$mensaje = "Ocurrió un error al obtener datos de la BD.";	
	    	}

		} else {
		    // invalid ID token            
		    $resultado = "0";
		    $mensaje = "ID token inválido";
		}    

		$json["resultado"] = $resultado;
		$json["mensaje"] =  $mensaje;

        return $response->withJson($json);
        
    });

    $app->post('/usuario/registrar', function(Request $request, Response $response, array $args) {
        $res = "resultado";
    	$msg = "mensaje";
    	$json = array();

    	if (!comprobarBodyParams($request, ["email", "contrasenia", "nombre"])) {
    		return $response->withJson([$res => "0", $msg => "Datos incompletos"]);
    	}

    	$email = $request->getParsedBodyParam("email");
    	$contrasenia = $request->getParsedBodyParam("contrasenia");
    	$nombre = $request->getParsedBodyParam("nombre");
    	$userDB = new UsuarioDB($this->db);

    	if ($userDB->existeEmail($email)) {
    		return $response->withJson([$res => "2", $msg => "Email ya registrado"]);
    	}

    	if ($userDB->insert($email, $nombre, $contrasenia, null) === -1) {
    		return $response->withJson([$res => "0", $msg => "Error al crear usuario"]);
    	}

    	$json = $userDB->findByCredenciales($email, $contrasenia);
    	if (empty($json)) {
    		return $response->withJson([$res => "0", $msg => "Ocurrió un error"]);
    	}

    	$json[$res] = "1";
    	$json[$msg] = "Usuario creado exitosamente";

    	return $response->withJson($json);
    });
}



?>
