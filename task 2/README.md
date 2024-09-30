# Task 2: A basic password manager

For this task, you will develop a basic password manager for a single client. This password
manager will be secured with your own implementation of the RSA algorithm. You will have find
the correct way to use RSA, i.e. which parts of the messages to encrypt/decrypt, so that the
password management server and potential interceptors are never able to see the cleartext passwords.

The following sections provide further details for the expected pieces of the program.


## RSA

You will read p and q from the primes.txt file to compute n, phi(n), e, and d. To find d, you may
use the standard library modular inverse function, i.e. BigInteger.modInverse(). For all other places where
modular exponentiation is required, you will use your own implementation of the fast modular
exponentiation algorithm.

You will want to use the BigInteger (https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/math/BigInteger.html) class for this program.


## Password manager client

You will extend the Client.java file to have a user interface and to correctly handle passwords. The
interface should allow the user to store, retrieve, or end the program in any order at any point.
The interface should gracefully handle erroneous inputs and received messages.

The client has three possible network messages that it will send to the server:
- store website password
- get website
- end

Where store tells the server to store the password for the website, get tells the server to send
back the password for the specified website, and end tells the server to end the program.

The following an example output and input for the client:

```
Welcome to the SENG2250 password manager client, you have the following options:
- store <website> <password>
- get <website>
- end

>>> store example.com correct horse battery staple
Password successfully stored.

You have the following options:
- store <website> <password>
- get <website>
- end

>>> get example.com
Your password for example.com is correct horse battery staple

You have the following options:
- store <website> <password>
- get <website>
- end

>>> end
bye.
```


## Password manager server

You will extend the Server.java file to handle the store and get commands received from the client.

When the server receives a store command, it will store the specified password such that it is
mapped to the specified website. Afterwards it will send back the message
"Password successfully stored."

When the server receives a get commend, it will send back only the password that is mapped to the
specified website.

The end command is already handled in the code you are extending, but its specifications are that
on receiving an end command it will send back "end okay" and quit the program.


## Running the programs

The programs should be compiled only with javac, i.e. with the `javac *.java` command. The server
and client are started in separate command prompts/terminals. The server starts first and looks for
clients, the client, when started, will connect to the server if it is online.