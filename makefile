fractalmaker.jar: bld/Main.class images
	jar cfe fractalmaker.jar Main -C bld/ .

bld/Main.class: src/*.java
	javac src/*.java -d bld/

./PHONY: clean
clean:
	rm -fr bld/* images/* fractalmaker.jar

./PHONY: run
run: fractalmaker.jar
	java -jar fractalmaker.jar

images:
	mkdir images

demo: fractalmaker.jar
	java -jar fractalmaker.jar \
		--julia="+ * z z c -0.75 0.11" \
		--x1=-1.6 --y1=-1 --x2=1.6 --y2=1 \
		--intensity=2 \
		--iterations=2000 \
		--antialiasing=2 \
		--output=images/img1.png

	java -jar fractalmaker.jar \
		--julia="+ power z 3 c 0.55 0.2482" \
		--x1=-1.35 --y1=-1.25 --x2=1.15 --y2=1.25 \
		--intensity=1.2 \
		--output=images/img2.png

	java -jar fractalmaker.jar \
		--julia="+ * z z c -0.8 0.18" \
		--x1=-1.65 --y1=-1 --x2=1.65 --y2=1 \
		--luminosity \
		--intensity=7 \
		--iterations=1000 \
		--antialiasing=2 \
		--output=images/img3.png

	java -jar fractalmaker.jar \
		--julia="+ * z z c 0.285 0.01" \
		--x1=-1.0 --y1=-1.2 --x2=1.0 --y2=1.2 \
		--intensity=100 \
		--iterations=2000 \
		--antialiasing=2 \
		--output=images/img4.png

	java -jar fractalmaker.jar \
		--julia="/ z + sin power z 2 c 0.4 0.7" \
		--intensity=50 \
		--iterations=2000 \
		--antialiasing=2 \
		--output=images/img5.png

	java -jar fractalmaker.jar \
		--mandelbrot \
		--x1=-2.2 --y1=-1.2 --x2=0.8 --y2=1.2 \
		--intensity=20 \
		--iterations=2000 \
		--antialiasing=2 \
		--output=images/img6.png

	java -jar fractalmaker.jar \
		--mandelbrot \
		--x1=-0.7497901859504129 --y1=-0.12205847107438043 \
		--x2=-0.73955444214876 --y2=-0.11296735537190059 \
		--step=2.0E-5 \
		--iterations=700 \
		--antialiasing=3 \
		--output=images/img7.png

		

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
