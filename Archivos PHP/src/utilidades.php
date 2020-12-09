<?php
use Psr\Http\Message\ServerRequestInterface as Request;
use Slim\Http\UploadedFile;

function comprobarBodyParams(Request $request, array $valores) {

	foreach ($valores as $val) {
		if (!$request->getParsedBodyParam($val, $default = null)) {
			return false;
		}
	}

	return true;
}

?>