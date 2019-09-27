import java.util.Optional;

public class OptionalUse {
    public static void main(String[] args) {
        Optional.of("something")
            .map(item -> item + " else")
            .ifPresent(System.out::println);
    }
}
