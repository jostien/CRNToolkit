CRNToolkit - Java toolkit for Chemical Reaction Networks
--------------------------------------------------------
CRNToolkit is a small, incomplete Java toolkit for Chemical Reaction Networks. It contains classes for handling such networks conveniently.

A Linux-like system and the [eclipse](https://eclipse.org/) environment are recommended!

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
- [Singular](https://www.singular.uni-kl.de/) (optional, but without Singular, the corresponding methods won't work)

Description
-----------
# Introduction
Chemical Reaction Network Theory (CRNT) can be described mainly using sets (see [CRNT](http://www.jeremy-gunawardena.com/papers/crnt.pdf)) so that most statements can easily and understandable be given with a small set of variables like y, y' for complexes, or c, c' for concentration vectors. Thus, it makes sense to represent those objects via a programming language (here java). Some objects from CRNT:
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

# The main classes MySet and MyMultiset
The two main classes in this package are the MySet- and MyMultiset-class. Mostly, all other classes are derived from them. MySet is derived from TreeSet so that the ordering of elements is always well defined as long as there is a meaningful toString()-method in its elements. Thus, MySet and MyMultiset always expect a meaningful toString()-method for distinguishing its elements.

Classes derived from MySet:
- MyEquivalenceClass
- MyPartition
- MyMatrix

Class derived from MyMultiset:
- Complex

# The MyEntry class and its subclasses
The MyEntry class serves as starting point for MyMatrix (see below) entries. Different fields are derived from it, e.g., the MyInteger class (integers), the MyRational class (rational numbers), and the MyDouble class (real numbers). Each entry has two additional data fields associated with rows and columns, respectively. These fields describe the corresponding dimension, i.e., from which space the rows (or the columns) come from.

# The MyMatrix class
Also the MyMatrix class is derived from MySet. Its entries are its elements. Entries should be derived from MyEntry. Since MyMatrix is also a TreeSet, its computation slows down for larger matrices significantly. E.g.
- the stoichiometric matrix N (with m = # of species rows and r = # of reactions columns) is a linear mapping from reaction space to species space and, thus, the rows are labeled with the corresponding species objects whereas the columns are labeled with the corresponding reaction objects;
- the matrix Y (with m = # of species rows and n = # of complexes columns) is a linear mapping from complex space to species space and, thus, the rows are labeled with the corresponding species objects whereas the columns are labeled with the corresponding complex objects.

This is quite convenient since it automatically associates the corresponding object with a row or column, especially, since this information is printed out by the toString()-method.

# The MyGraph class
The MyGraph class is intended to encapsulate all graph related code. Associated to it are the MyNode and MyEdge class.

# The ReactionNetwork class
The ReactionNetwork class is derived from MyGraph since each reaction network is also a graph.

# Miscellaneous
There is no guarantee that each method will work as intended since the code is continously evolving. Some classes and methods are heavily used and tested, others are present but are never or only occasionally used.
