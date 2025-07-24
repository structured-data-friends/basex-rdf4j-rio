# basex-rdf4j-rio

The Rio RDF parser from RDF4J for BaseX.

See [Parsing and Writing RDF With Rio](https://rdf4j.org/documentation/programming/rio/).

# Installation

Download the repository to a local directory.
In this directory, run `mvn install`.
This creates a file called `basex-rdf4j-rio-n.m.s.jar` (where `n.m.s` is the version) in the `target` sub-directory.
Copy this jar file into the `lib/custom` directory of your BaseX installation, and (re)start BaseX.

# Functions

The RDF parsing functions are imported by

```xquery
import module namespace rdf='com.rakensi.basex.xquery.functions.rdf.RioParserModule';
```

The function you most likely want to use is:

```xquery
rdf:parse($uri as xs:string) as document-node()
```

This function tries to determine the type of the input document at `$uri`.
If the type can be determined the input document is parsed and an [`rdf:RDF` XML](https://www.w3.org/TR/rdf-syntax-grammar/) document is returned.

There are format-specific functions with the same signature as `rdf:parse`.
The formats and their expected filename-extensions are specified in the
[RDF4J source code](https://github.com/eclipse-rdf4j/rdf4j/blob/main/core/rio/api/src/main/java/org/eclipse/rdf4j/rio/RDFFormat.java).

* rdf:parse-rdf-xml
* rdf:parse-n-triples
* rdf:parse-ttl
* rdf:parse-ttl-star
* rdf:parse-n3
* rdf:parse-trix
* rdf:parse-trig
* rdf:parse-trig-star
* rdf:parse-binary
* rdf:parse-n-quads
* rdf:parse-json-ld
* rdf:parse-nd-json-ld
* rdf:parse-rdf-json
* rdf:parse-rdf-a
* rdf:parse-hdt

# Examples

Let Rio determine the input file type.

```xquery
import module namespace rdf='com.rakensi.basex.xquery.functions.rdf.RioParserModule';
let $testfile := 'file://a-ttl-file'
return rdf:parse($testfile)
```

Force parsing the input as TTL:

```xquery
import module namespace rdf='com.rakensi.basex.xquery.functions.rdf.RioParserModule';
let $testfile := 'file://a-ttl-file'
return rdf:parse-ttl($testfile)
```
