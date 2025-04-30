#!/bin/bash

javac -cp "Simulador/snakeyaml-2.4.jar" Simulador/*.java

rm -rf snakeyaml-classes
mkdir snakeyaml-classes

(cd snakeyaml-classes && jar xf ../Simulador/snakeyaml-2.4.jar)

jar cfm simulador.jar Manifest.txt Simulador/*.class -C snakeyaml-classes .

rm -f Simulador/*.class
rm -rf snakeyaml-classes

echo "Build concluído! Execute com: java -jar simulador.jar config.yml"