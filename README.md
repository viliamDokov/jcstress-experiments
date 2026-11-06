# JCStress tests for jrc11 

Stress tests are located in Coherence.java

You can use nix and run `nix-build` to build and obtain `jcsress.jar` in `results/jcstress.jar`.
Alternatively you can use maven and run `mvn -pl tests-custom -am -Dmaven.javadoc.skip=true` to build the `jcstress.jar` file.

To execute the tests you can copy the `jcstress.jar` to a machine with the architecture you are interested in testing and run the command:
`java -jar jcstress.jar -t [NAME_OF_TEST]`. 
The `[NAME_OF_TEST]` must be some regex that matches class name of a registered jcstress test, for example `Cohernce.ReleaseSequence`.
The only requirement is that you need to have some `jdk11` installed on the machine.




