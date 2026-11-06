# JCStress tests for JRC11

Stress tests are located in `tests-custom/src/main/java/org/openjdk/jcstress/tests/jrc11/Coherence.java`

You can use nix and run `nix build` to build and obtain `jcstress.jar` in `result/jcstress.jar`.
Alternatively you can use maven and run `mvn -pl tests-custom -am package -Dmaven.javadoc.skip=true` to build the `jcstress.jar` file in `tests-custom/target/jcstress.jar`.

To execute the tests you can copy the `jcstress.jar` to a machine with the architecture you are interested in testing and run the command:
`java -jar jcstress.jar -t '[NAME_OF_TEST]'`.
The `[NAME_OF_TEST]` must be some regex that matches class name of a registered jcstress test, for example `Coherence\.ReleaseSequence`.
The only requirement is that you need to have JDK 11 or newer installed on the machine.
