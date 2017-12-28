# Assignment 3

## How to run the program

+ Compile source code

In the main directory, run the following command:

```
make clean
make all
```

+ Run the Server using the *server* script.
```
./server
```

The Server will output the *port* file.

+ Run the Client using the *client* script.
```
./client <host> <port> <op-string> <file name> <send|recv size> <wait time>
```

## Build and test environment

+ Build: 
```
ubuntu1604-002.student.cs.uwaterloo.ca
ubuntu1604-006.student.cs.uwaterloo.ca
ubuntu1604-008.student.cs.uwaterloo.ca
```
+ Test: 

```
ubuntu1604-002.student.cs.uwaterloo.ca
ubuntu1604-006.student.cs.uwaterloo.ca
ubuntu1604-008.student.cs.uwaterloo.ca
```

## Design ideas

All parts of the assignment have been completed. 

I choose to use a multi-threaded design for the server. On the server side, the main thread maintains a hashmap to store all *key-Socket* pairs. The access and modification operations to the hashmap will be synchronized by a Semaphore with initial value of 1. When a client connects to the server, a new server thread will be created and the client will transfer the 9-byte *op-string* to the server thread. Then the server thread processes client's request based on the request type: 

+ download request

The server thread updates the download Socket object of the corresponding key in the hashmap and then returns.

+ upload request

The server thread gets the download Socket object of the corresponding key in the hashmap and start to forward data from upload Socket to download Socket.

+ terminate request

The server thread closes the ServerSocket in the main thread to stop receiving connections. The main thread then joins all processing server threads and closes all open Sockets in the hashmap before it finally returns.


## Tools

+ *make* version: *GNU Make 4.1*

+ *Java* version: *openjdk "9-internal"*