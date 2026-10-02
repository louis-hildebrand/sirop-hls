TEX_UTILS := $(wildcard utils/*.tex)
ALL_SVGS := $(patsubst %.tex, %.svg, $(wildcard *.tex))

all: $(ALL_SVGS)

%.svg: bin/%.pdf
	pdftocairo $< -svg -o $@

bin/%.pdf: %.tex $(TEX_UTILS)
	latexmk $< -pdf -outdir=./bin/

.PHONY: clean
clean:
	rm -rf bin/
