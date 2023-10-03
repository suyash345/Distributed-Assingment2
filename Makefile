FILES = AggregationServer.java GETClient.java ContentServer.java ClientHandler.java testing.java
JCC = javac
RM = rm
LIB = -cp "gson-2.10.1.jar;."

default: $(FILES)
	$(JCC) $(LIB) AggregationServer.java ClientHandler.java
	$(JCC) $(LIB) GETClient.java
	$(JCC) $(LIB) ContentServer.java


CServer: ContentServer.class
	java $(LIB) ContentServer

Client: GETClient.class
	java $(LIB) GETClient

AServer: AggregationServer.class ClientHandler.class
	java $(LIB) AggregationServer

CompileTest: testing.java
	$(JCC) $(LIB) testing.java

Test: testing.class
	java $(LIB) testing
clean:
	$(RM) *.class
