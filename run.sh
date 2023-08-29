#! /bin/bash

rm -rf target
# Download the dependencies
mvn dependency:go-offline -B dependency:copy-dependencies -DoutputDirectory=target/dependencies
cp target/dependencies/*.jar target/
rm -rf target/dependencies
# Build a release artifact
mvn package -DskipTests
java -jar target/pdfsigner-1.0-SNAPSHOT.jar
echo "Check files/output_signed.pdf"
