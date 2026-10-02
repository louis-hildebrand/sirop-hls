TEX_UTILS := $(wildcard utils/*.tex)
MODES := dark light
ALL_SVGS := $(foreach file, $(wildcard *.tex), $(foreach mode, $(MODES), $(mode)/$(patsubst %.tex,%.svg,$(file))))

all: $(ALL_SVGS)

dark/%.svg: dark/bin/%.pdf
	pdftocairo $< -svg -o $@

dark/bin/%.pdf: %.tex $(TEX_UTILS)
	cp utils/colors_dark.tex utils/colors.tex
	latexmk $< -pdf -outdir=./dark/bin/
	rm utils/colors.tex

light/%.svg: light/bin/%.pdf
	pdftocairo $< -svg -o $@

light/bin/%.pdf: %.tex $(TEX_UTILS)
	cp utils/colors_light.tex utils/colors.tex
	latexmk $< -pdf -outdir=./light/bin/
	rm utils/colors.tex

.PHONY: clean
clean:
	rm -rf dark/bin/ light/bin/
