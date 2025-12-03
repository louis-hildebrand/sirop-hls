# Engineering effort measurements

## Aetherling

First go back to the latest "real" commit by the Aetherling authors themselves.
(Skip the one random commit in 2022.)

```sh
$ g switch -d 34c5403e07433e572170699f3bd69c5b5c3eff2d
```

### List of authors

```sh
$ g log --format='%an' | sort | uniq
David Durst
Dillon Bailey Huff
Dillon Huff
dillonhuff
mattfel
```

## Individual development time

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

## SHIR

### List of authors

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

### Individual development time

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
