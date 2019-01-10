# Torro
[![Build Status](https://travis-ci.com/Shaquu/torro.svg?token=7sUv3fGQGS9ZRFDNSCi8&branch=master)](https://travis-ci.com/Shaquu/torro)

Torro is a clean java UDP/TCP app to share files between hosts.
Requirements for project [here](https://sites.google.com/site/skjzaocznelato/projekt);

# Featuring

  - host2host (TCP or UDP mode)
  - multihost (TCP or UDP mode)

You can also:
  - Request file list from remote host
  - Push file to remote host
  - Pull file from remote host
  - Resume file downloading
  - Download one file from many hosts at once (different chunk from different host)

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

License
----

MIT
