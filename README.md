v0.1 - 30 Mar 2011

# Provocateur - Targetted JUnit testing suites


This is a very early version. It works for us but may not work for you.
Help us make it better:



Provocateur is a tool to identify which JUnit tests you must run in order to test
modified source files. This allows the developer to create dynamic test suites
optimised to test only the local change set.

This is not a replacement for running a full build but in some circumstances you 
may wish to quickly check your local changes. This is particularly useful for
projects with large, slow running, Acceptance test suites.


##License

Provocateur is licensed under the Apache License v2. See LICENSE.txt

Provocateur includes the excellent ASM and Lucene libraries. These are availabile under
separate licenses. See Prvocateur/provocateur-thirdparty/README for full info.


##Usage

Provocateur consists of three components:

1. A test metric capturing agent which profiles your running build to determine which tests exercise which source code.
2. A dynamic JUnit suite framework allowing suites to be constructed on the fly based on your modification change set
3. A set of SCM plugins for identifying change sets. Currently only SVN is supported.

See Usage.txt for more info.

##FAQ

Q: Why should I use this?

A: If you have instances of developers checking in builds without running all the tests because
your test suites take too long this tool could help you.


Q: Will provocateur instrument my production classes with testing code?

A: No. Provocateur uses a custom classloader that does all necessary instrumentation as loadtime.

Q: Are large slow test suites not a symptom of bad module design?

A: Yes. If you have the opportunity to fix that then do so. If you don't then this tool could help
you.



Q: Can I use it within my IDE?

A: Yes. Provocateur currently uses standard JUnit suite runner behaviour supported by most IDEs.


Q: Does it work with Maven?

A: Currently the capturing of test metrics works with Maven's surefire. Creating a surefire dynamic suite
is not currently supported but is planned.


Q: Does it work with Ant?

A: It should as work with Ant's Junit task but we haven't tried.


Q: How does it work?

A: During a full build a profiler indexes information about which tests 'exercise' which source files (similar
to a code coverage tool). Subsequent Provocateur test suite runs use this information to filter out tests which 
do not exercise modified source files. A set of rules are used to determine specific behaviour based on file type.


Q: Why is it called Provocateur?

A: Geek joke. The software includes a Java agent for profiling the source. We started thinking about
all the famous Agents and this one seemed right.

Q: I am trying to build the project and maven is complaning it cannot find the correct jGit version.

A: The jGit mvn repository does not seem to be upto date you can find the version of jGit we build against [here](https://github.com/downloads/rikf/provocateur/org.eclipse.jgit_0.12.0-SNAPSHOT.jar)
