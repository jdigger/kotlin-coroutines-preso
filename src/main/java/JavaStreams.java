import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class JavaStreams {

    public static void main(String[] args) {
        streams_are_monads();

        lists_are_multi_use();

        streams_are_single_use();
    }

    private static void streams_are_monads() {
        Stream.of(2, 3, 4, 7, 9)
            .filter(i -> i % 2 == 0)
            .map(i -> i * 3)
            .findFirst()
            .ifPresent(i -> System.out.println("First filtered and mapped number: " + i));

        Stream.of(2, 3, 4, 7, 9)
            .flatMap(i -> (i % 2 == 0) ? Stream.of(i) : Stream.empty())  // functionally the same as filter
            .map(i -> i * 3)
            .findFirst()
            .ifPresent(i -> System.out.println("First filtered and mapped number: " + i));
    }

    private static void lists_are_multi_use() {
        List<Integer> list = Arrays.asList(2, 3, 4, 7, 9);

        System.out.println("list.size: " + list.size());
        System.out.println("list.size: " + list.size());
    }

    private static void streams_are_single_use() {
        try {
            Stream stream = Stream.of(2, 3, 4, 7, 9);

            System.out.println("stream.count: " + stream.count());
            System.out.println("stream.count: " + stream.count());
        }
        catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

}
