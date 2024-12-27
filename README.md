# Bingo ticket generator

A small tool for generating [Bingo 90](https://en.wikipedia.org/wiki/Bingo_(British_version)) strips.
The strip generator should be used as part of larger system, and provides only the basic execution functionality,
printing crude ASCII representation of the tickets on screen.

## Development

The tool is implemented in pure Java, without additional dependencies.
It can thus be developed and tested in any decent IDE.
Most Java IDEs should be able to execute it easily,
but text terminal instructions for [Apache Maven](https://en.wikipedia.org/wiki/Apache_Maven) are also provided.

### Testing

Due to the stochastic nature of ticket filling algorithm, it's not possible to strictly adhere to TDD principles.
The tests exist for basic data structures and overall algorithm behaviour.
The correctness of generated tickets is verified by testing the validation of edge cases.

Run test by executing command `mvn test`

### Build

Building the tool for execution or distribution is straightforward by executing command `mvn package`.
This will run all the tests and create an executable file `MrMilo.jar` in the target folder within the project.

## Execution

When executed without additional parameters or with incorrect parameters,
the tool generates and prints one strip and usage instructions.

Example usage `java -jar target/MrMilo.jar r 3`

### Options

- `r N` - generate and print N strips.
- `s N` - silently generate N strips, print statistics per cell for debugging.
- `t N` - silently generate N strips, print time statistics, for performance assessment.
