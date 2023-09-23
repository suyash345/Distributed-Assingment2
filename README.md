# How to Run
* first to compile all files use run ___make___
* To run the Aggregation Server use ___make AServer___. 
* To run the Content Server use ___make CServer___ 
* to run the GETClient use __make Client___.
* To run the Test file use ___make Test___. Note this will connect to port 4567, hence the Aggregation server should also be on the same port.
* Note you have to run the Aggregation Server first, prior to the Content Server, and GetClient.

# Unfinished parts
* There is no 30 second deletion on data that has arrived.
* automated testing with different error codes
* automated testing same IDs with updated values, the Aggregation Server, should take the most recent value.
* The aggregation server does not update most recent data, as if the new data has the same ID, the old data is stored and the new data is thrown away.

# Testing 
* All these tests are conducted in the testing.java file, where you are able to call functions to emulate both a Client and a ContentServer

### Put and Get Requests (With error code testing)  (Not implemented yet)
* To test the Get and Put request the whole request (along with the body) is displayed when it is received.
* For example if a aggregation server is receiving a request, the whole request is displayed after receiving it, 
and when a ContentServer/Client has received the response, the response is displayed on their side.
* To test further on the responses received we are able to send a request with other types of request. 
For example when we input a PUT request with no body, the response should be 204, or when a request is not a get or put
the response should be 400.
* This ensures that text sending works, and that basic communication works.

### Lamport Time
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

### Testing for failure recovery testing.
* To test for this use ___CTRL + C___ to stop the server, and the data should still remain. 