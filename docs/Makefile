workflow.svg: bin/workflow.pdf
	pdftocairo bin/workflow.pdf -svg -o workflow.svg

bin/workflow.pdf: workflow.tex
	latexmk workflow.tex -pdf -outdir=./bin/

.PHONY: clean
clean:
	rm -rf bin/
