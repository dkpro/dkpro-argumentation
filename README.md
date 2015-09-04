# DKPro Argumentation

Java framework for demonstration purposes of working with DKPro/UIMA typesystem for argumentation annotation and argumentation mining.

Version 0.0.2

## Installation

DKPro Argumentation is available from Maven Central. For working with the UIMA type system, add the following dependencies to your `pom.xml`

```
<dependency>
    <groupId>de.tudarmstadt.ukp.dkpro.argumentation</groupId>
    <artifactId>de.tudarmstadt.ukp.dkpro.argumentation.types</artifactId>
    <version>0.0.2</version>
</dependency>
```
## Modules

### de.tudarmstadt.ukp.dkpro.argumentation.types

The UIMA type system is the central part of DKPro Argumentation. It enables to annotate argument components and relations on top of the UIMA/DKPro framework.

The class hierarchy contains two central classes, ``ArgumentComponent`` and ``ArgumentRelation``.

```
uima.tcas.Annotation
|
+- de.tudarmstadt.ukp.dkpro.argumentation.types.ArgumentUnit</name>
   |      typeValue: uima.cas.String (Field for storing type value)
   |      properties: uima.cas.String (Field for storing any additional information; String
   |                                   serialization of java Properties)
   |
   +- de.tudarmstadt.ukp.dkpro.argumentation.types.ArgumentComponent
   |  |
   |  +- de.tudarmstadt.ukp.dkpro.argumentation.types.Claim
   |  |       stance: uima.cas.String
   |  |
   |  +- de.tudarmstadt.ukp.dkpro.argumentation.types.Premise
   |  |  |
   |  |  +- de.tudarmstadt.ukp.dkpro.argumentation.types.Backing
   |  |
   |  +- de.tudarmstadt.ukp.dkpro.argumentation.types.Rebuttal
   |  |
   |  +- de.tudarmstadt.ukp.dkpro.argumentation.types.Refutation
   |  |
   |  +- de.tudarmstadt.ukp.dkpro.argumentation.types.Citation
   |  |
   |  +- de.tudarmstadt.ukp.dkpro.argumentation.types.MajorClaim
   |
   +- de.tudarmstadt.ukp.dkpro.argumentation.types.ArgumentRelation
      |      source: de.tudarmstadt.ukp.dkpro.argumentation.types.ArgumentUnit
      |      target: de.tudarmstadt.ukp.dkpro.argumentation.types.ArgumentUnit
      |
      +- de.tudarmstadt.ukp.dkpro.argumentation.types.Support
      |
      +- de.tudarmstadt.ukp.dkpro.argumentation.types.Attack
      |
      +- de.tudarmstadt.ukp.dkpro.argumentation.types.Detail
      |
      +- de.tudarmstadt.ukp.dkpro.argumentation.types.Same
```

### IO

Contains ``ArgumentDumpWriter`` class that debugs all argument annotations in a document to the file/stdout.

### Misc

Provides classes to easily operate with argument component annotations.

### Preprocessing

Contains several annotators for casting the problem of argument component identification as BIO tagging (i.e. ``ArgumentTokenBIOAnnotator``)

### Examples

#### Example project for reading annotated data

as shown in

> Habernal, I., Eckle-Kohler, J., & Gurevych, I. (2014). Argumentation Mining on the Web from Information Seeking Perspective. In E. Cabrio, S. Villata, & A. Wyner (Eds.), Proceedings of the Workshop on Frontiers and Connections between Argumentation Theory and Natural Language Processing (pp. 26-39). Bertinoro, Italy: CEUR-WS. Retrieved from http://ceur-ws.org/Vol-1341/

##### Requirements

- Annotated data
  - packaged separately, available at https://www.ukp.tu-darmstadt.de/data/argumentation-mining/argument-annotated-user-generated-web-discourse/

##### How-to

1. Modify paths to gold data
  - Modify `de.tudarmstadt.ukp.dkpro.argumentation.tutorial.ArgumentationCorpusDebugger` and set the `annotatedCorpusDir` variable to point to the gold data located in `gold.data.toulmin` directory
2. Run `ArgumentationCorpusDebugger`
  - It will print annotated argument components, relations, and other info to the std. out
3. Explore it further!
  - Have a look at `de.tudarmstadt.ukp.dkpro.argumentation.io.writer.ArgumentDumpWriter` from the `de.tudarmstadt.ukp.dkpro.argumentation.0.0.2` package which shows how to access the argument components, their text, tokens, sentences, etc.


&copy; 2013-2015 UKP

Argumentation Mining Special Interest Group members

https://www.ukp.tu-darmstadt.de/ukp-home/research-areas/argumentation-mining/