### Compile
  javac \*.java

### Start RMI Registry
  rmiregistry &

### Client/Server Examples
  java Server lab3\
  java Client localhost lab3 REGISTER www.google.com 8.8.8.8\
  java Client localhost lab3 LOOKUP www.google.com\
