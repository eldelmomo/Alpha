# Alpha-simple

**Alpha** is a simple implementation of the **Alpha Miner** process mining algorithm for process model discovery.  
The current version focuses on generating the **footprint matrix** and **Petri net model** from event log traces.

This implementation only accepts logs in a list-of-traces format, where each trace is given as a comma-separated list of tasks.
Example syntax: java Main A,B,C A,C,D B,C,E
   
## Features

- Parses event log traces from command-line arguments.  
- Computes the **footprint matrix** and **Petri net model** according to the Alpha Miner method [Aalst2004].  
- Modular structure for easy extension towards Petri net generation.



## Usage

Compile and run the program with Java:

### Example:
```bash
javac Main.java
java Main java Main a,b,c,d a,b,c,d a,b,c,d a,c,b,d a,c,b,d a,c,b,d a,c,b,d a,b,c,e,f,b,c,d a,b,c,e,f,b,c,d a,b,c,e,f,c,b,d a,c,b,e,f,b,c,d a,c,b,e,f,b,c,d a,c,b,e,f,b,c,e,f,c,b,d
```


### Output:
```bash
Footprint:
_, a, b, c, d, e, f
a, #, >, >, #, #, #
b, <, #, =, >, >, <
c, <, =, #, >, >, <
d, #, <, <, #, #, #
e, #, <, <, #, #, >
f, #, >, >, #, <, #
```
