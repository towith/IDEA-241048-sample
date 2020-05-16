set jOption=
rem set jOption= jOption -agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=5005
rem set jOption=%jOption% -Ddebug
"D:\Program Files\Java\jdk1.8.0_211\bin\java.exe" %jOption% -XX:TieredStopAtLevel=1 -noverify -Dspring.profiles.active=dev -Dspring.output.ansi.enabled=always -Dcom.sun.management.jmxremote -Dspring.jmx.enabled=true -Dspring.liveBeansView.mbeanDomain -Dspring.application.admin.enabled=true -javaagent:D:\Applications\Scoop\apps\IntelliJ-IDEA-Ultimate\current\IDE\lib\idea_rt.jar=9219:D:\Applications\Scoop\apps\IntelliJ-IDEA-Ultimate\current\IDE\bin -Dfile.encoding=UTF-8 -classpath classpath_right.jar com.willbe.wordl.WordlearnbackendApp
