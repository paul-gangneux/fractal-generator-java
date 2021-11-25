Main: images src/Main.java
	javac src/Main.java -d bld/

./PHONY: clean
clean:
	rm -fr bld/* images/*

images:
	mkdir images/