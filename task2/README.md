# Task 2: A Single Client Password Manager

## Description
This program is a basic password manager for a single client. The passwords are secured with an 
implementation of the RSA algorithm. The messages sent between the client has the websites and 
passwords encrypted using this implementation of RSA ensuring that the server and interceptors.
The Client is able to store a website and its given password, get the password for a stored 
website and end the communication between the Client and the Server.

## RSA

This will read the p and q from the given file "primes.txt" to compute n, phi(n), e, and d. Only
when computing 'd' does it use the modular inversefunction (BigInteger.modInverse()). In all other
places that requre modular exponentiation uses my own implementation of fast modular exponential 
algorithm.

## Client.java

The client file is where the user inputs the commands they wish to store or retrive. 
The following is an example output for the client file:

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

This file will check for potential input errors and attempt to notify the user to 
potential ways that they can alter their previous input to obtain the desired result.

## Password manager server

When the server receives a store command, it will store the specified password such that it is
mapped to the specified website. Afterwards it will send back the message
"Password successfully stored."

When the server receives a get commend, it will send back only the password that is mapped to the
specified website.

## Requirements
This program was created using java 17 and will require you to have java 17 or later installed.

## Running the program

To run the program you must complete the following steps:
 - Step 1: Compile the program using:"Javac *.java" in the terminal.
 - Step 2: You must have 2 terminals open and in one use the command "java Server".
 - Step 3: Once the server is running it will output "Listening for a client on port "22500" which means
           on the second terminal you can now input the command "java Client".

After the completion of these steps the client terminal will output an interface meaning that you have successfully
started the program and can input the following inputs into the client terminal:
inputs: 

- store <website> <password>
- get <website>
- end

