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

TestMain: src/TestMain.java
	javac src/*.java -d bld/

./PHONY: test
test: TestMain
	java -classpath bld TestMain

demo: Main
	java -classpath bld Main \
		--julia="+ * z z c 0 0.8" \
		--output=images/1.png
	java -classpath bld Main \
		--julia="+ * z z c 0.285 0" \
		--output=images/2.png
	java -classpath bld Main \
		--julia="+ * z z c -0.75 0.11" \
		--output=images/3.png
	java -classpath bld Main \
		--output=images/3-mandelbrot.png






format:
	java \
		--add-exports jdk.compiler/com.sun.tools.javac.api=ALL-UNNAMED \
		--add-exports jdk.compiler/com.sun.tools.javac.file=ALL-UNNAMED \
		--add-exports jdk.compiler/com.sun.tools.javac.parser=ALL-UNNAMED \
		--add-exports jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED \
		--add-exports jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED \
		-jar tools/google-java-format-1.13.0-all-deps.jar \
		--replace \
		src/*.java
