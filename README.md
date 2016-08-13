CRNToolkit - Java toolkit for Chemical Reaction Networks
--------------------------------------------------------

CRNToolkit is a small, incomplete Java toolkit for Chemical
Reaction Networks. It contains classes for handling such networks
conveniently.

A Linux-like system and the [eclipse](https://eclipse.org/) environment are recommended!

Introduction
------------

- species: some thing, e.g., A, B or C
- complex: a multiset of species, e.g., {}, {A}, {2B} or {B,C}
- S: the set of species, e.g., S := {A, B, C}
- C: the set of complexes, e.g., C := {{}, {A}, {2B}, {B,C}}
- R: the set of reactions, e.g., R := {{}->{A}, {A}->{2B}, {2B}->{B,C}, {B,C}->{}}
- reaction network: the tuple (S, C, R)

- linkage class: set of connected components in reaction network, i.e., a set of complexes
- strong linkage class: set of strongly connected components in reaction network, i.e., a set of complexes

- linkage class partitions set of complexes <=> equivalence relation on complexes: those complexes are equivalent, which are elements of the same linkage class  
- strong linkage class partitions set of complexes <=> equivalence relation on complexes: those complexes are equivalent, which are elements of the same strong linkage class

Examples
--------

Some networks in the examples are not available yet. 

Available networks
------------------

- [BioModels](https://www.ebi.ac.uk/biomodels-main/)

- [BiGG](http://bigg.ucsd.edu/)


Requirements
------------

- [Octave](https://www.gnu.org/software/octave/) is mandatory for linear algebra computations!

- [Polco](http://www.csb.ethz.ch/tools/software/polco.html) for EFM computation

- [JSBML](http://sbml.org/Software/JSBML) for parsing SBML-files

- [Singular](https://www.singular.uni-kl.de/) (optional but the corresponding methods won't work)