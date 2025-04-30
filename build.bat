@echo off

javac -cp "Simulador\snakeyaml-2.4.jar" Simulador\*.java

rmdir /s /q snakeyaml-classes 2>nul

mkdir snakeyaml-classes
cd snakeyaml-classes
jar xf ..\Simulador\snakeyaml-2.4.jar
cd ..

jar cfm simulador.jar Manifest.txt Simulador\*.class -C snakeyaml-classes .

del /q Simulador\*.class
rmdir /s /q snakeyaml-classes

echo Programa compilado. Execute com: java -jar simulador.jar seu_arquivo.yml
pause