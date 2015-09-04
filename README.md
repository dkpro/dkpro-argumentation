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

### de.tudarmstadt.ukp.dkpro.argumentation.io

Contains ``ArgumentDumpWriter`` class that debugs all argument annotations in a document to the file/stdout.

### de.tudarmstadt.ukp.dkpro.argumentation.misc

Provides classes to easily operate with argument component annotations.

### de.tudarmstadt.ukp.dkpro.argumentation.preprocessing

Contains several annotators for casting the problem of argument component identification as BIO tagging (i.e. ``ArgumentTokenBIOAnnotator``)

## Examples

See https://github.com/habernal/dkpro-argumentation-tutorial


&copy; 2013-2015 UKP

Argumentation Mining Special Interest Group members

https://www.ukp.tu-darmstadt.de/ukp-home/research-areas/argumentation-mining/