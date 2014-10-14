README

How to run this project:

1. Run Server.main first to set up the server.
2. Configure the server IP address in ServerAddress.java so that client (App) will be able to connect to the server correctly.
3. Compile the com.example.android.notepad in eclipse and run.

Overview:

The original NotePad sample is extended so that all the data is transmitted to and stored on a remote server. After the modification, the architecture is changed from UI <--> Local database to UI <--> Local data cache <--> Cloud storage. Once the App is started, local data cache is filled with data from remote server. While running, all the write operations are performed first on local cache and further passed to cloud (write-through); read operations, on the other hand, are directly performed on local cache. 

Permissions:

This App now requires Internet permission to be able to execute.


Modifications in detail:

--Add--

Server.java: Server instance. It creates a server socket and listens to client's message. It also initializes and maintains the cloud storage. The remote storage is implemented as a file, and handles two major operations: LOAD and SAVE. The protocol for server-client communication is as follows:

Client-Request	Value		Payload		Server-Reply
LOAD 			1		None			All records
SAVE			2		All records	None

Client.java: Client instance. It creates socket and communicates with server.

ServerAddress.java: An interface to store server address. Server IP address is stored here as a constant for convenience. Any client that wishes to connect to server just needs to implement (include) this interface.

Codec.java: Utility class. It provides utility methods to serialize and de-serialize database records.

NoteEntry.java: The data structure for an entry in cloud storage.

Command.java: Constants for communication protocol.

DBService.java: An Android service instance. It is supposed to handle internal requests. Currently, it serves two needs. 1) Update URL of remote database. 2) Delete entries in local content provider.

--Change--

NotesList.java: Add a private class LoadDatabaseTask which extends AsyncTask. It is instantiated and invoked in onCreate() method, in order to load data from remote server and initialize local database cache. Network transmission is realized in AsyncTask, because it blocks and thus may even crash the App if it works in UI thread.

NoteEditor.java: Add AsyncTask classes including SaveDatabaseTask which creates new  thread to update server data. Especially, a separate SaveDatabaseAfterDeletionTask is created if the update is to delete an existing entry in the database.

If you have further questions, please contact Mu Zhang (muzhang@syr.edu).
