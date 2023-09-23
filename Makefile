FILES = AggregationServer.java GETClient.java ContentServer.java ClientHandler.java testing.java
JCC = javac
RM = rm
LIB = -cp "gson-2.10.1.jar;." # For Windows

default: $(FILES)
	$(JCC) $(LIB) AggregationServer.java ClientHandler.java
	$(JCC) $(LIB) GETClient.java
	$(JCC) $(LIB) ContentServer.java
	$(JCC) $(LIB) testing.java

CServer: ContentServer.class
	java $(LIB) ContentServer

Client: GETClient.class
	java $(LIB) GETClient

AServer: AggregationServer.class ClientHandler.class
	java $(LIB) AggregationServer

Test: testing.java
	java $(LIB) testing
clean:
	$(RM) *.class
