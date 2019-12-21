Student: Amancio Pontes Hermo (PSI18)

Steps needed to compile and run the project:
Those steps assume that the commands are executed from the root directory of the delivered code, and that the jade.jar exists at the root directory.

To compile, the following command is used:
	$ javac -cp "./jade.jar:./PSI18/:./" MainAgent.java PSI18/BaseAgent.java PSI18/RandomAgent.java PSI18/FixedAgent.java PSI18/ThresholdReacherAgent.java PSI18/QlearningAgent.java PSI18_GUI.java

To run the project, the following command is used:
	$ java -cp "./jade.jar:.:./PSI18/" jade.Boot -agents "main:MainAgent;random:PSI18.RandomAgent;fixed:PSI18.FixedAgent;reacher:PSI18.ThresholdReacherAgent;qlearner:PSI18.QlearningAgent" -local-port 10999
(-local-port is optional, but it may be necesary in case of the default port being in use)

Agent Implementation:
	
	Every playing agent extends from the class BaseAgent, and BaseAgent extends from Agent. This implementation was made motivated by the fact that every player agent needs to follow the same messaging and behaviour pattern except for the decisions when they need to play, so an abstract function "playRound(int)" is implemented for each agent to customize the decision they make in each round. In addition, the functions:

 public void onConfigured(){}
 public void onResult(String result){}
 public void onRoundFinished(){}

are also offered to override by the agents in case they need to make actions on some states of the game.

Agents Implemented:

	Fixed Agent: allways plays 0;
	
	Random agent: chooses any action between 0 and 4 with equal probability

	Threshold Reacher Agent: plays 2 until the treshold is reached, then plays 0
	
	Q learning agent: makes decisions based on a qlearning implementation.
