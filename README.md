# Torro
[![Build Status](https://travis-ci.com/Shaquu/torro.svg?token=7sUv3fGQGS9ZRFDNSCi8&branch=master)](https://travis-ci.com/Shaquu/torro)

Torro is a clean java UDP/TCP app to share files between hosts.
Requirements for project [here](https://sites.google.com/site/skjzaocznelato/projekt);

Project meets all requirements in above website.

# Featuring

  - host2host (TCP or UDP mode)
  - multihost (TCP or UDP mode)

You can also:
  - Request file list from remote host
  - Push file to remote host
  - Pull file from remote host
  - Resume file downloading
  - Download one file from many hosts at once

# Building jar

  ``` mvn org.apache.maven.plugins:maven-jar-plugin:2.4:jar ```
  
# Running

## Manualy
### UDP

  ``` java -jar torro-<version>.jar <debug mode true/false> true <port> <folder path> <other hosts ports delimeted by space> ```
  
  For example
  
  ``` java -jar torro-<version>.jar false true 10001 TORrent_1 10002 10003 ```

### TCP

  ``` java -jar torro-<version>.jar <debug mode true/false> false <port> <folder path> <other hosts ports delimeted by space> ```
  
  For example
  
  ``` java -jar torro-<version>.jar false false 10001 TORrent_1 10002 10003 ```

## Via prepared scripts
### Linux
Follow sub folders in run_scripts folder and run script in terminal.
### Windows
Follow sub folders in run_scripts folder and run script in git bash.

# Specification
Torro use a wide variety of packets.
 - FileListPacket
 - LogOnPacket
 - PullFilePacket
 - PullFilePartsPacket
 - PushFilePacket
 - PushFilePartsPacket
 - RequestFileListPacket

All packets extends base Packet which contains byte array where most data is written during Serialization.

If packet size is too huge then packet will be chunked into smaller packets. 
Host which receive chunks will wait until he receives all chunks and then will collect them into one packet and proceed with operations.

  - Request file list from remote host 
  
  > (Host(H1) sends RequestFileListPacket to remote host(H2).
  > When H2 receives packet then he sends FileListPacket to H1.
  > H1 loads packet and reads file list.
  - Push file to remote host
  
  > Host(H1) pack file into PushFilePacket and send it to remote host(H2).
  > When H2 receives packet then he load packet and read file from it.
  - Pull file from remote host
  
  > Host(H1) pack file description into PullFilePacket and send it to remote host(H2).
  > When H2 receives packet then he look for requested file. If found then he build PushFilePacket and send it to H1.
  > When H1 receives packet then he load packet and read file from it.
  - Resume file downloading
  
  > Host(H1) send PullFilePartsPacket to all host connected with him. Packet contains file description and requested file parts.
  > Other hosts(O1) looks for file in their folders and if found then they build PushFilePartsPacket and send it to H1.
  > When H1 receives packet then he load packet and join new data with existing one and read file from it.
  - Download one file from many hosts at once
  
  > Host(H1) send PullFilePacket with file description to all hosts connected with him (O1).
  > Other hosts(O1) looks for file in their folders and if found then they build PushFilePacket and send it to H1.
  > When H1 receives packet then he load packet and read file from it.
  
##Packet
Base packet which is Serializable. Contains methods to pack packet into byte array or load packet from byte array.

``` 
private final long id; //Unique file id
private final int part; //File part
private final int maxPart; //Max file part (if packet is not chunked then its 1)

private Byte[] data; //Some data 
```

##FileListPacket
Packet contains list of host(H1) files. Host(H2) which receives this packet will now know what files are on other host(H1).

List with files is written to byte array.
##LogOnPacket
Packet is sent in TCP mode only on first connection. It's just a ping packet.
##PullFilePacket
Packet contains file description of file we want to get from remote host(H1). 
Remote host(H1) will receive this packet and if he has the file then will build PushFilePacket containing file data.
##PullFilePartsPacket
The same like PullFilePacket but with specified chunk numbers we want to pull.

``` List<Integer> pargs; //Chunk parts ```
##PushFilePacket
Packet contains file content. It is sent from host(H1) to remote host(H2). 
When remote host(H2) receive this packet then he will get byte array and deserialize it into file.
##PushFilePartsPacket
The same like PushFilePacket but with specified chunk numbers we want to push.

``` List<Integer> pargs; //Chunk parts ```
##RequestFileListPacket
Packet which ticks remote host(H1) to send us(H2) his list of files.



License
----

MIT
