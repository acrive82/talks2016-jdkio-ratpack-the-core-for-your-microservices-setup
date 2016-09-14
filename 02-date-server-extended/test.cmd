
@echo off

@rem set JAVA_OPTS=-Dgroovy.grape.report.downloads=true 
@set JAVA_OPTS=

@set JAVA_HOME=E:\JVMs\jdk1.8.0_101
@set PATH=%JAVA_HOME%\bin;%PATH%

@start groovy DateServerTest.groovy 