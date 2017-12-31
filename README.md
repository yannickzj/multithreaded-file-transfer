# Multi-threaded File Transfer

## Introduction

This project aims to implement a server to facilitate multiple file exchanges between clients using TCP.

## Architecture

### Server program

The server program can handle an arbitrary number of concurrent connections and file exchanges, only limited by system configuration or memory. The server is started without any parameters and creates a TCP socket at an OS-assigned port. It prints out the assigned port number and stores it in a local file **port**, which is used when starting clients. The server listens on its main socket and accepts client connections as they arrive. Clients perform an upload or download operation, or instruct the server to terminate.

Both upload and download operations specify a key that is used to match clients with each other, i.e., a client asking for downloading with a specific key receives the file that another client uploads using that key. Files are not stored at the server, but instead clients wait for a match and then data is forwarded directly from the uploader to the downloader. The server always matches a pair of uploader and downloader with the same key. For simplicity, we assume that only a single downloader matches an uploader, and an uploader never starts before its matched downloader.

When the server receives the termination command from a client, it closes all waiting connections from unmatched clients and does not accept any further connections. However, it completes ongoing file exchanges and terminates only after all file exchanges are finished.

### Communication

The data stream sent from the client to the server adheres to the following format:

+ **command**: 1 ASCII character: **G** (get = download), **P** (put = upload), or **F** (finish = termination)

+ **key**: 8 ASCII characters (padded at the end by ‘\0’-characters, if necessary)

In case of an upload, the above 9-byte control information is immediately followed by the binary data stream of the file. In case of download, the server responds with the binary data stream of the file. When a client has completed a file upload, it closes the connection. Then the server closes the download connection to the other client.

### Client program

The client takes up to 6 parameters and can be invoked in 3 different ways:

+ terminate server: `client <host> <port> F`

+ download: `client <host> <port> G<key> <file name> <recv size>`

+ upload: `client <host> <port> P<key> <file name> <send size> <wait time>`

The client creates a TCP socket and connects to the server at `<host>` and `<port>`. It then transmits the command string given in the 3rd shell parameter to the server as described above, i.e., with padding. When transmitting an **F** command, the client still sends an empty key, i.e., 8 ‘\0’-characters.

When requesting an upload or download, the client reads data from or stores data to, respectively, the file specified in the 4th parameter `<file name>`.

The 5th parameter specifies the size of the buffer that is transmitted during each individual **write/send** or **read/recv** system call during the file transfer - except for the final data chunk that might be smaller.

When uploading a file, the 6th parameter specifies a wait time in milliseconds between subsequent **write/send** system calls. Assume the wait time is always below 1 second, so the system call *usleep* can be used to implement the wait. This parameters allows for a simple form of rate control that is important to test the concurrency of the server.


## How to run the program

+ Compile source code

In the main directory, run the following command:

```
make clean
make all
```

+ Run the Server using the *server.sh* script.
```
./server.sh
```

The Server will output the *port* file.

+ Run the Client using the *client.sh* script.
```
./client.sh <host> <port> <op-string> <file name> <send|recv size> <wait time>
```

## Build and test environment

+ Build and test: 
```
ubuntu 16.04
```

## Design ideas


Multi-threaded design is chosen to implement the server. On the server side, the main thread maintains a hashmap to store all *key-Socket* pairs. The access and modification operations to the hashmap will be synchronized by a Semaphore with initial value of 1. When a client connects to the server, a new server thread will be created and the client will transfer the 9-byte *op-string* to the server thread. Then the server thread processes client's request based on the request type: 

+ download request

The server thread updates the download Socket object of the corresponding key in the hashmap and then returns.

+ upload request

The server thread gets the download Socket object of the corresponding key in the hashmap and start to forward data from upload Socket to download Socket.

+ terminate request

The server thread closes the ServerSocket in the main thread to stop receiving connections. The main thread then joins all processing server threads and closes all open Sockets in the hashmap before it finally returns.


## Tools

+ *make* version: *GNU Make 4.1*

+ *Java* version: *openjdk-9*