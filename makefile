Main: images src/Main.java
	javac src/*.java -d bld/

./PHONY: clean
clean:
	rm -fr bld/* images/*

images:
	mkdir images/

./PHONY: run
run: Main
	java -classpath bld Main