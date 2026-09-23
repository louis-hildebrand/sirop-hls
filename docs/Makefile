TEX_UTILS := colors.tex tikz.tex

%.svg: bin/%.pdf
	pdftocairo $< -svg -o $@

bin/%.pdf: %.tex $(TEX_UTILS)
	latexmk $< -pdf -outdir=./bin/

.PHONY: clean
clean:
	rm -rf bin/
