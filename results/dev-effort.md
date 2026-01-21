# Engineering effort measurements

NOTE: in all these command listings, `g` is an alias for `git`.

## Lines of code

### Aetherling

Chisel itself:
```sh
g sw -d 2063447 && cloc . --fullpath --not-match-d test
HEAD is now at 2063447415 Merge branch '3.2.x' into 3.2-release
     125 text files.
     125 unique files.
       9 files ignored.

github.com/AlDanial/cloc v 1.90  T=0.10 s (1201.7 files/s, 312059.9 lines/s)
-------------------------------------------------------------------------------
Language                     files          blank        comment           code
-------------------------------------------------------------------------------
SVG                              4              2             45          13263
Scala                          100           1934           5033           9022
Markdown                         5            151              0            456
Scheme                           2             56              0            287
XML                              2              0              3            216
YAML                             2              9              6            168
make                             3             38             19            100
C++                              1             17              8             70
-------------------------------------------------------------------------------
SUM:                           119           2207           5114          23582
-------------------------------------------------------------------------------
```

### SHIR

Scala code:
```sh
$ cloc src/main/backend/hdl/vhdl
       4 text files.
       4 unique files.
       0 files ignored.

github.com/AlDanial/cloc v 1.90  T=0.01 s (459.2 files/s, 224910.1 lines/s)
-------------------------------------------------------------------------------
Language                     files          blank        comment           code
-------------------------------------------------------------------------------
Scala                            4             82            161           1716
-------------------------------------------------------------------------------
SUM:                             4             82            161           1716
-------------------------------------------------------------------------------
```

VHDL templates:
```sh
$ cloc ./vhdltemplates/ --not-match-f 'mem|async' --not-match-d 'drafts|testing|unused'
     120 text files.
     120 unique files.
       3 files ignored.

github.com/AlDanial/cloc v 1.90  T=0.07 s (1668.0 files/s, 150121.2 lines/s)
-------------------------------------------------------------------------------
Language                     files          blank        comment           code
-------------------------------------------------------------------------------
VHDL                           120           1222            467           9111
-------------------------------------------------------------------------------
SUM:                           120           1222            467           9111
-------------------------------------------------------------------------------
```

### Sirop

```sh
$ cloc src/main/scala/mhir/gen/
      10 text files.
      10 unique files.
       0 files ignored.

github.com/AlDanial/cloc v 1.90  T=0.01 s (1025.1 files/s, 265610.2 lines/s)
-------------------------------------------------------------------------------
Language                     files          blank        comment           code
-------------------------------------------------------------------------------
Scala                           10            168            280           2143
-------------------------------------------------------------------------------
SUM:                            10            168            280           2143
-------------------------------------------------------------------------------
```

## Man-months

### Aetherling

First go back to the latest "real" commit by the Aetherling authors themselves.
(Skip the one random commit in 2022.)

```sh
$ g switch -d 34c5403e07433e572170699f3bd69c5b5c3eff2d
```

#### List of authors

```sh
$ g log --format='%an' | sort | uniq
David Durst
Dillon Bailey Huff
Dillon Huff
dillonhuff
mattfel
```

### Individual development time

David Durst: 23 months
```sh
$ g log --author='Durst' --format='%ad' --date=format:'%Y-%m' | (head -n1 && tail -n1)
2020-09
2018-10
```

Dillon Huff: 5 months
```sh
$ g log --author='[Hh]uff' --format='%ad' --date=format:'%Y-%m' | (head -n1 && tail -n1)
2020-03
2019-10
```

Matt Feldman: 5 months
```sh
$ g log --author='mattfel' --format='%ad' --date=format:'%Y-%m' | (head -n1 && tail -n1)
2020-03
2019-10
```

Total: 33 man-months

### Chisel

First go back to v3.2.8, which is what Aetherling uses.
```sh
g switch -d 2063447415df76f9355226bb47d575a4255de722
```

#### List of authors

```sh
$ g log --format='%an' | sort | uniq
Adam Izraelevitz
Albert Chen
Albert Magyar
Anders Pitman
Andrew Waterman
Angie Wang
azidar
Boris V.Kuznetsov
Brendan Sweeney
chick
Chick Markley
Christopher Celio
colin4124
Colin Schmidt
Danny
Donggyu
Donggyu Kim
Ducky
ducky
ducky64
edwardcwang
Edward Wang
Fabien Marteau
grebe
Hasan Genc
Henry Cook
Howard Mao
Jack
jackbackrack
Jack Koenig
jackkoenig
Jim Lawson
Kamyar Mohajerani
Kevin Townsend
Leway Colin
Martin Schoeberl
Megan Wachs
mergify[bot]
Michael Gielda
Nick Hynes
Palmer Dabbelt
Paul Rigge
Richard Lin
Richard Xia
Schuyler Eldridge
Scott Beamer
Sebastian Bøe
Sequencer
Siddhanathan Shanmugam
Stephen Twigg
Steve Burns
Stevo
The Gitter Badger
Wesley W. Terpstra
yep
```

#### Individual development time

Jack Koenig: 62 months
```sh
$ g log --author='[Kk]oenig' --format='%ad' --date=format:'%Y-%m' | (head -n1 && tail -n1)
2020-12
2015-10
```

Schuyler Elridge: 36 months
```sh
$ g log --author='Schuyler' --format='%ad' --date=format:'%Y-%m' | (head -n1 && tail -n1)
2019-10
2016-10
```

Jim Lawson: 58 months
```sh
$ g log --author='Jim Lawson' --format='%ad' --date=format:'%Y-%m' | (head -n1 && tail -n1)
2020-05
2015-07
```

Richard Lin: 41 months
```sh
$ g log --author='Richard Lin' --format='%ad' --date=format:'%Y-%m' | (head -n1 && tail -n1)
2019-07
2016-02
```

Albert Magyar: 17 months
```sh
$ g log --author='Albert Magyar' --format='%ad' --date=format:'%Y-%m' | (head -n1 && tail -n1)
2019-01
2017-08
```

Total: >= 214 months

### SHIR

#### List of authors

```sh
$ g log --format='%an' | sort | uniq
Andrej
Ayan
ayanchak1508
Ayan Chakraborty
Christof Schlaak
Christophe Dubach
Jonathan Van der Cruysse
jonathanvdc
Louis Hildebrand
Nathaniel Branchaud
Paul Teng
s1210443
Tzung-Han JUANG
Tzung-Han Juang
Yuan-Po (Paul) Teng
Yuan-Po Teng
Zhitao Lin
zlin888
```

#### Individual development time

Andrej: negligible
```sh
$ g log --author='Andrej' --format='%ad' --date=format:'%Y-%m' | (head -n1 && tail -n1)
2018-11
```

Ayan: 4 months
```sh
$ g log --author='[Aa]yan' --format='%ad' --date=format:'%Y-%m' | (head -n1 && tail -n1)
2021-09
2021-05
```

Christophe Dubach: 2 months
```sh
$ g log --author='Dubach' --format='%ad' --date=format:'%Y-%m' | (head -n1 && tail -n1)
2018-12
2018-10
```

Christof Schlaak: 52 months
```sh
$ g log --author='Schlaak' --format='%ad' --date=format:'%Y-%m' | (head -n1 && tail -n1)
2023-02
2018-10
```

Jonathan Van der Cruysse: 15 months
```sh
$ g log --author='[Jj]onathan' --format='%ad' --date=format:'%Y-%m' | (head -n1 && tail -n1)
2024-11
2023-08
```

Louis Hildebrand: I didn't contribute to SHIR itself, I just wrote some benchmarks.

Nathaniel Branchaud: 3 months
```sh
$ g log --author='Branchaud' --format='%ad' --date=format:'%Y-%m' | (head -n1 && tail -n1)
2020-08
2020-05
```

Yuan-Po (Paul) Teng: 24 months
```sh
$ g log --author='Teng' --format='%ad' --date=format:'%Y-%m' | (head -n1 && tail -n1)
2025-07
2023-07
```

Tzung-Han Juang: 49 months
```sh
$ g log --author='Tzung-Han' --format='%ad' --date=format:'%Y-%m' | (head -n1 && tail -n1)
2025-05
2021-04
```

Zhitao Lin: 17 months
```sh
$ g log --author='[Ll]in' --format='%ad' --date=format:'%Y-%m' | (head -n1 && tail -n1)
2022-05
2020-12
```

Total: 166 man-months
