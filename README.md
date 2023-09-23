# How to Run
* first to compile all files use run ___make___
* To run the Aggregation Server use ___make AServer___. 
* To run the Content Server use ___make CServer___ 
* to run the GETClient use __make Client___.
* Note you have to run the Aggregation Server first, prior to the Content Server, and GetClient.

# Unfinished parts
* There is no 30 second deletion on data that has arrived.
* automated testing with different error codes

# Testing 

### Put and Get Requests (With error code testing)  
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
    
### Testing for multiple Content Servers
* To test the communications between Content Servers and the Aggregation Server, the input file must be changed.
* The Aggregation Server will not input JSON data with the same ID, hence to test if multiple Content Servers can be tested.
In the input file the ID needs to be changed and the Content Server needs to be run again, so that multiple instances of data can be stored in the Aggregation Server.
* To test if the data is actually stored correctly, the GETClient can be run to receive the stored data from the Aggregation Server.

### Test for multiple Clients requests.
* To test for this numerous clients on different terminals were run to determine if the Aggregation server is able to process and send back the content  
to all the GETClients running on different terminals.

### Testing for failure recovery testing.
* To test for this use ___CTRL + C___ to stop the server, and the data should still remain. 