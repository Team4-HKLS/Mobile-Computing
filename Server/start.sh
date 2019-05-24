mvn clean
mvn package
rm -rf nohup.out
nohup java -jar target/server-0.0.1-SNAPSHOT.war &
