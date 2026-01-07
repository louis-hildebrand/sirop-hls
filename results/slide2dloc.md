# Engineering Effort to Add New Operator

To estimate the effort required to add a new operator to each language, we count lines of code.
As shown below, the change is
- 85 lines of Scala code in Sirop
- 109 lines of Scala code and 95 lines of VHDL code in SHIR

## Sirop

- A commit before defining `StmSlide2D`: 3ea3566bdaa354bc37cffefbc455fbd11203981a
- A commit after defining `StmSlide2D`: c4815af6e4dbabe58ae85226b254bb33ce88251c

Confirm changes:
```sh
$ git diff --stat 3ea3566bdaa354bc37cffefbc455fbd11203981a c4815af6e4dbabe58ae85226b254bb33ce88251c src/main/scala/mhir/sugar/
 src/main/scala/mhir/sugar/Stream.scala | 103 +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
 1 file changed, 103 insertions(+)
```

Count change in LoC:
```sh
$ cloc --diff 3ea3566bdaa354bc37cffefbc455fbd11203981a c4815af6e4dbabe58ae85226b254bb33ce88251c --git --match-d='src/main/scala/mhir/sugar'
       1 text file.
       1 text file.
       0 files ignored.

github.com/AlDanial/cloc v 1.90  T=0.46 s (2.2 files/s, 4584.4 lines/s)
-------------------------------------------------------------------------------
Language                     files          blank        comment           code
-------------------------------------------------------------------------------
Scala
 same                            0              0            239           1788
 modified                        1              0              0              0
 added                           0              3             15             85
 removed                         0              0              0              0
-------------------------------------------------------------------------------
SUM:
 same                            0              0            239           1788
 modified                        1              0              0              0
 added                           0              3             15             85
 removed                         0              0              0              0
-------------------------------------------------------------------------------
```

## SHIR

- A commit before: a0dfabbf010b5ca00a475dc1440183fe73bab470
- A commit after: b05fe6f596d4fe1f0b95f45ab3f2bf5316d57e32

Confirm changes:
```sh
$ git diff --stat a0dfabbf010b5ca00a475dc1440183fe73bab470 b05fe6f596d4fe1f0b95f45ab3f2bf5316d57e32 src/main/ vhdltemplates/
 src/main/backend/hdl/arch/ArchExpr.scala        |  60 ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
 src/main/backend/hdl/graph/BehaviourNodes.scala |   9 +++++++++
 src/main/backend/hdl/graph/GraphGenerator.scala |  13 +++++++++++++
 src/main/backend/hdl/vhdl/VhdlGenerator.scala   |  28 ++++++++++++++++++++++++++++
 vhdltemplates/slide_2d_ordered_stream.vhd       | 112 ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
 5 files changed, 222 insertions(+)
```

Count change in LoC:
```sh
$ cloc --diff a0dfabbf010b5ca00a475dc1440183fe73bab470 b05fe6f596d4fe1f0b95f45ab3f2bf5316d57e32 --git --match-d='src/main|vhdltemplates'       4 text files.
       5 text files.
       0 files ignored.

github.com/AlDanial/cloc v 1.90  T=0.36 s (13.8 files/s, 17621.8 lines/s)
-------------------------------------------------------------------------------
Language                     files          blank        comment           code
-------------------------------------------------------------------------------
Scala
 same                            0              0            505           5641
 modified                        4              0              0              0
 added                           0              1              0            109
 removed                         0              0              0              0
VHDL
 same                            0              0              0              0
 modified                        0              0              0              0
 added                           1             12              5             95
 removed                         0              0              0              0
-------------------------------------------------------------------------------
SUM:
 same                            0              0            505           5641
 modified                        4              0              0              0
 added                           1             13              5            204
 removed                         0              0              0              0
-------------------------------------------------------------------------------
```
