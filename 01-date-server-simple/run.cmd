@echo off
@set PORT=5555
@set JAVA_OPTS=-Dgroovy.grape.report.downloads=true 
@set JAVA_HOME=E:\JVMs\jdk1.8.0_101
@set PATH=%ANT_HOME%\bin;%PATH%

@start groovy ratpack.groovy 