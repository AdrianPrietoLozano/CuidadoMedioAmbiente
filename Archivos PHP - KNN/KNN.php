<?php

require_once("RegistroKNN.php");

class KNN
{
    private $datos = array();
    private $etiquetas = array();
    
    function __construct(&$datos, &$etiquetas)
    {
        $this->datos = $datos;
        $this->etiquetas = $etiquetas;
    }
    
    private function determinarK()
    {
        $numElementos = (float)count($this->datos);
        $root = sqrt($numElementos);
        $rawK = $root / 2;
        
        $num = round($rawK);
        if($num % 2 == 0) // es par
        {
            $num--;
        }
        
        return $num;
        
    }
    
    // Se le pasan los datos para los cuales
    // se desean las recomendaciones.
    // Devuelve un arreglo con los id's de los
    // usuarios predecidos
    public function getPredicciones(&$datosUsuario)
    {
        $distancias = array();

        $numElementos = count($this->datos);
        for($i = 0; $i < $numElementos; $i++)
        {
            $distancia = $this->getDistanciaEntre($datosUsuario, $this->datos[$i]);
            $distancias[] = new RegistroKNN($this->etiquetas[$i], $distancia);
        }

        usort($distancias, array("KNN", "ordenarElementos"));

        $k = $this->determinarK();
        $ids_usuarios_predicciones = array();
        for($i = 0; $i < $k; $i++)
        {
            $ids_usuarios_predicciones[] = $distancias[$i]->getIdUsuario();
        }

        //var_dump($ids_usuarios_predicciones);

        return $ids_usuarios_predicciones;
    }

    // ordena dos elementos tipo RegistroKNN
    private static function ordenarElementos($a, $b)
    {
        if ($a->getDistancia() == $b->getDistancia()) {
            return 0;
        }
        return ($a->getDistancia() < $b->getDistancia()) ? -1 : 1;
    }
    
    // devuelve la distancia entre dos usuarios
    private function getDistanciaEntre(&$datos1, &$datos2)
    {
        $numElementos = count($datos1);
        $sumaCuadrados = 0.0;

        for($i = 0; $i < $numElementos; $i++)
        {
            $a = pow($datos1[$i] - $datos2[$i], 2);
            
            $sumaCuadrados += $a;
        }

        /*
        echo "Distancia entre [";
        foreach ($datos1 as $d1) {
            echo $d1." ";
        }
        echo "], [";

        foreach ($datos2 as $d2) {
            echo $d2." ";
        }

        echo "] = ".abs(sqrt($sumaCuadrados))."<br>";
        */
        
        return abs(sqrt($sumaCuadrados));
    }
    
}

?>