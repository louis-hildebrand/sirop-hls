# Installation

The Sirop compiler is provided as an executable .jar file; see the [Releases tab on GitHub](https://github.com/louis-hildebrand/sirop-hls/releases).
Download the .jar file and run it with

```
$ java -jar sirop.jar --version
```

For convenience, you could define a function like

```sh
function sirop {
    java -jar /absolute/path/to/sirop.jar "$@"
}
export -f sirop
```

and then run

```
$ sirop --version
```

For help with the command-line interface, run

```
$ sirop --help
```

## Syntax Highlighting

A Vim plugin providing basic syntax highlighting can be found at [github.com/louis-hildebrand/sirop-vim](https://github.com/louis-hildebrand/sirop-vim).
