
Super Fortran Lexer (using JFlex) + Parser + Code Generator (using LLVM)

##### Lexer:
java -jar part1.jar source.sf

##### Parser:
java -jar part2.jar [options] source.sf
  * -v : verbose mode
  * -wt tree.tex : write tree to .tex  file
 
##### Compiler:
java -jar part3.jar source.sf [options]
* -o llvm.ll : write IR code to .ll file
* -o llvm.ll -exec : execute the .sf file after writing
