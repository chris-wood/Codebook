/* Class definition ommitted */

public void run(){
  List<Integer> list = Arrays.asList(5, 9, -1, 60, 10, 2, 9);

  // region <lambdas> ---------------------------------------------------------------------------┐
  list.sort   ((a, b) -> b - a);
  list.forEach(i -> System.out.print(i + "   ")); // output: 60   10   9   9   5   2   -1

          //  ┌-----------> Note the empty parenthesis, this lambda has no input
  new Thread(() -> {
    try   { Thread.sleep(1000); }
    catch (InterruptedException e){}
  }).start();

  System.out.println();
  // endregion ----------------------------------------------------------------------------------┘

  // region <streams> ---------------------------------------------------------------------------┐
  // Streams allow method chaining
  list.stream().distinct()
               .sorted  ((a, b) -> b - a)
               .filter  (i -> i > 0)
               .map     (multiplyBy2) // multiplyBy2 is a pointer to function, see its definition below
               .forEach (System.out::println); // output: 120 // note the :: operator to access static methods
                                               //         20
                                               //         18
                                               //         10
                                               //         4

  System.out.println();
  // endregion ----------------------------------------------------------------------------------┘

  // region <concurrent execution> --------------------------------------------------------------┐
  // First, this is a normal (sequential) execution of some arbitrary task.
  // See the concurrent equivalent below.
  long t1 = System.currentTimeMillis();
  list.stream().forEach(i -> {
    try   { Thread.sleep(1000); }
    catch (InterruptedException e){}
  });

  System.out.println("Sequential Time: " + (System.currentTimeMillis() - t1)); // output: Sequential Time: 7002

  // Then this is the equivalent using Parallel Streams. 
  // Parallel streams are like Streams in addition to there ability
  // to distribute code among all available CPU cores.
  // Note that it's not like threading, this uses the fork/join framework (introduced in Java 7)
  // to deliver true concurrent execution.
  long t2 = System.currentTimeMillis();
  list.parallelStream().forEach(i -> {
    try   { Thread.sleep(1000); }
    catch (InterruptedException e){}
  });

  System.out.println("Parallel Time: " + (System.currentTimeMillis() - t2)); // output: Parallel Time: 2012
  // endregion ----------------------------------------------------------------------------------┘

  // region <high-order functions> --------------------------------------------------------------┐
  higherOrderFunction(x -> x + 1,
                      y -> y > 10);
  // endregion ----------------------------------------------------------------------------------┘
}

/**
 * Functions became first class citizens in Java 8, basically, they became a data type.
 * Meaning that you can pass them as parameters to other functions (aka high-order functions), and, 
 * you can also return them from a function.
*/
public void higherOrderFunction(Function<Integer, Integer> function2,
                                Predicate<Integer> predicate){
  System.out.println("Higher fn1: " + function2.apply(10));  // output: Higher fn1: 11
  System.out.println("Higher fn2: " + predicate.test(10));   // output: Higher fn2: false
}

/**
 * Declaring a function as a data type that's usable as a first class citizen in different contexts.
*/
private static Function<Integer, Integer> multiplyBy2 = (x) -> x * 2;