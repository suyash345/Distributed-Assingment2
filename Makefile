FILES = AggregationServer.java GETClient.java ContentServer.java ClientHandler.java JSON_Parser.java
JCC = javac
RM = rm
LIB = -cp "lib/gson-2.10.1.jar;." # For Windows

default: $(FILES)
	$(JCC) $(LIB) AggregationServer.java ClientHandler.java
	$(JCC) $(LIB) GETClient.java
	$(JCC) $(LIB) ContentServer.java
	$(JCC) $(LIB) JSON_Parser.java

CServer: ContentServer.class
	java $(LIB) ContentServer

Client: GETClient.class
	java $(LIB) GETClient

AServer: AggregationServer.class ClientHandler.class
	java $(LIB) AggregationServer

clean:
	$(RM) *.class
