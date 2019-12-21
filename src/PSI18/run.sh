javac -cp "../jade/lib/jade.jar::./PSI18/" PSI18/MainAgent.java PSI18/BaseAgent.java PSI18/RandomAgent.java PSI18/PSI18_GUI.java
java -cp "../jade/lib/jade.jar::." jade.Boot -agents "main:PSI18.MainAgent;random:PSI18.RandomAgent;random2:PSI18.RandomAgent;random3:PSI18.RandomAgent" -local-port 10999

