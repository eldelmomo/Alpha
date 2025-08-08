# Alpha

**Alpha** is a simple implementation of the **Alpha Miner** process mining algorithm for process model discovery.  
The current version focuses on generating the **footprint matrix** from event log traces.  
Future development aims to extend the functionality to generate a **Petri net model** directly from the logs.

This implementation only accepts logs in a list-of-traces format, where each trace is given as a comma-separated list of tasks.
Example syntax: java Main A,B,C A,C,D B,C,E
   
## Features

- Parses event log traces from command-line arguments.  
- Computes the **footprint matrix** according to the Alpha Miner method.  
- Modular structure for easy extension towards Petri net generation.

## Planned Features

- Implement the transformation from the footprint matrix to **Petri net models**.  
- Add file-based event log reading (e.g., CSV or XES format).  
- Improve CLI interface and usability.

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
