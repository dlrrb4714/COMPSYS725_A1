COMPSYS725 Assignment 1

Simple File Transfer Protocol 

- This README contains information required to understand the code base and testing, as well as user authentication needed to access the server.

Testing setup

1) Create a project from existing resources on IntelliJ and import the source folder

2) Run the "SFTPProtocolMain.java" file

3) Following the instructions below, enter commands

4) If there are any issues with running, please restart the program and try again

5) If rerunning the main file, please use DONE command from below to disconnect the server / client prior to restarting

-----------------------------------------------------------------------------------------------------------------------

Testing Instructions

User authentication:

Users:
admin, fox, rat

Account / ID :
admin, foxy123, helpfulrat56jj

Password:
admin, 1234, 5678

- Admin is a superuser - the username or account both suffice for a successful login. fox and rat are guest users, who require both accounts and passwords for a successful login. Account and password can be input in any order.

- Using the admin account is recommended for testing purposes.

Test file:
- When testing commands to send files, the test1 file can be used. A manual input is required for its size. Please check the file properties for size prior to input as this can potentially affect availability status.

-----------------------------------------------------------------------------------------------------------------------
Test cases
- With any command, a single space is required between each parameter.
- Any invalid commands or command input without logging in will return an error message.

USER

Command : 	USER (user)
Outcome :		1) If Superuser, will be logged in
		2) If guest, user will be identified then prompted to provide	 account and password

Errors : 		If invalid user, prompted to try again


ACCT

Command : 	ACCT (account)
Outcome : 	1) If Superuser, will be logged in
		2) If input was a correct account, user is prompted to provide password
		3) If there was a correct previous input of a corresponding password, will be logged in

Errors : 		If invalid account, prompted to try again


PASS

Command : 	PASS (password)
Outcome : 	1) Superuser requires a user or account input to log in
		2) If input was a correct password, user is prompted to provide account
		3) If there was a correct previous input of a corresponding account, will be logged in

Errors : 		If invalid password, prompted to try again


TYPE

Command : 	TYPE(type(A,B,C))
Outcome : 	1) Depending on input of A, B or C, will return a response of the mode used

Errors :		If invalid type, prompted to try again


LIST

Command : 	LIST(type(F,V), directory)
Outcome : 	1) If there was an input F, provides a list of files and folders in user directory with names
		2) If there was an input V, provides a list of files with relevant information (file size, hide status, last written date) and folders in user directory with names

Errors : 		


CDIR

Command : 	CDIR(directory)
Outcome : 	1) Changes the working directory to the input address.

Errors : 		If the directory doesn't exist, prompted to try again


KILL

Command : 	KILL(file name)
Outcome: 		1) Deletes the specified file at the working directory.

Errors : 		If the file doesn't exist, prompted to try again


NAME

Command : 	NAME(existing file name)
Outcome : 	1) Tells user if file exists in working directory then allows the user to use the TOBE command

Errors : 		If the file doesn't exist, prompted to try again


TOBE

Command :	TOBE(new file name)
Outcome : 	1) Renames specified file name from the NAME command and renames it to the new file name

Errors : 		If the original file was not checked with NAME, prompted to try again


DONE

Command : 	DONE
Outcome : 	1) Returns a message of "+" then exits the connection

Errors : 		


RETR

Command : 	RETR(file name)
Outcome : 	1) The remote system will check the file specified by the user to be sent and return the size of the file

Errors : 		If the file doesn't exist, prompted to try again


SEND

Command : 	SEND
Outcome : 	1) The specified file will be sent to the remote system

Errors : 		If the file transfer was not requested with RETR, prompted to try again


STOP

Command : 	STOP
Outcome : 	1) Aborts the RETR command

Errors : 		


STOR

Command : 	STOR(type(NEW,OLD,APP),file name)
Outcome : 	1) Tells the remote system to receive the file and save it according to the type specified by the user.
		2) NEW specifies a new generation of the file
		3) OLD specifies the new file to overwrite the original
		4) APP specifies the new file to append to the original

Errors : 		Invalid type input prompts the user to try again

SIZE

Command : 	SIZE(number of bytes in file)
Outcome : 	1) The number of bytes specifies the 8 bit bytes that will be sent.

Errors : 		If the file is too big, the command prompts to not send the file