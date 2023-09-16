FILES = AggregationServer.java GETClient.java ContentServer.java ClientHandler.java
JCC = javac
RM = rm

default: $(FILES)
	$(JCC) AggregationServer.java
	$(JCC) GETClient.java
	$(JCC) ContentServer.java
	$(JCC) ClientHandler.java


rmi:
	start rmiregistry & 

CServer: ContentServer.class
	java ContentServer

Client: GETClient.class
	java GETClient

AServer: AggregationServer.class
	java AggregationServer ClientHandler
	
clean:
	$(RM) *.class