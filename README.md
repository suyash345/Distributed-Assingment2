# How to Run
* The version that is submitted has a makefile that only complies on Mac/Linux. Therefore, to run on windows change the makefile line  ___LIB = -cp "gson-2.10.1.jar:."___ to ___LIB = -cp "gson-2.10.1.jar;."___
* It is also recommended that the Program is to be run either on Windows Powershell or on Mac terminal to see the colours for testing
* first to compile Aggregation, Content, and GetClient use run ___make___
* To run the Aggregation Server use ___make AServer___. 
* To run the Content Server use ___make CServer___ 
* to run the GETClient use ___make Client___.
* Note you have to run the Aggregation Server first, prior to the Content Server, and GetClient.


# Lamport Clocks
> Lamport clocks have been implemented in both the Aggregation Server and Content Server. 
> In the Aggregation Server the lamport clocks were implemented 
> However, as the ContentServer is not multithreaded,  

# Testing 
* All these tests are conducted in the testing.java file, where you are able to call functions to emulate both a Client and a ContentServer
* To compile Test file use ___make CompileTest___ 
* To run the Test file use ___make Test___. Note this will connect to port 4567, hence the Aggregation server should also be on the same port.

### Testing 30 second timers 
* To test for this we run the Content Server, with the Aggregation Server, and also with the __dataServer.json__ empty and after 30 seconds the data will disappear i.e deleted.

### Simutaneous Testing with Get and Put Requests

### Error Code Testing
* To test the Get and Put request the whole request (along with the body) is displayed when it is received.
* For example if a aggregation server is receiving a request, the whole request is displayed after receiving it, 
and when a ContentServer/Client has received the response, the response is displayed on their side.
* To test further on the responses received we are able to send a request with other types of request. 
For example when we input a PUT request with nobody, the response should be 204, or when a request is not a get or put
the response should be 400.
* This ensures that text sending works, and that basic communication works.
* In form of __The response from the server is: HTTP1.1 200 OK Server: AggregationServer/1.0 3__, where is the last Lamport Time

### Lamport Time Testing 
* To Test the Lamport Time we compare the times before and after the get/put responses and requests. 
The code displays the lamport time before and after the request. 
If the times are the same after the communication , then the lamport times are correct.
* This is done in testing.java, where you are able to compare the lamport times before sending, and after receiving responses.
    
### Testing for multiple Content Servers
* To test the communications between Content Servers and the Aggregation Server, the input file must be changed, as if data is sent with same ID the Aggregation server does not store it.
* in the testing.java file, multiple instances of put requests and get requests are run to determine the interactions of numerous clients and servers.
* the first time the put method is run, there is only one instance of data. When it is run again, it reads from a different input file and sends different data with a different ID. 
This data can then be checked by the getClient run after where the response has two different IDs. This tests if the data is stored correctly when multiple Content Servers send data, and that it can be accessed by clients.
### Test for multiple Clients requests.
* To test for this numerous clients on different terminals were run to determine if the Aggregation server is able to process and send back the content to all the GETClients running on different terminals.

### Testing for Harness
* In the 


### Testing for failure recovery testing.
* To test for this use ___CTRL + C___ to stop the server, and the data should still remain. 

`