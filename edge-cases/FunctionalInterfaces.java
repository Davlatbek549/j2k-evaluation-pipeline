import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class FunctionalInterfaces {
    public List<String> normalize(List<String> names) {
        Predicate<String> notBlank = value -> value != null && !value.trim().isEmpty();
        Function<String, String> normalize = String::trim;
        Consumer<String> logger = System.out::println;

        return names.stream()
                .filter(Objects::nonNull)
                .filter(notBlank)
                .map(normalize)
                .sorted(Comparator.comparing(String::length).thenComparing(String::compareToIgnoreCase))
                .peek(logger)
                .collect(Collectors.toList());
    }

    public void runInline() {
        Arrays.asList(" Ada ", "Bob", null, "  ")
                .forEach(value -> {
                    if (value != null) {
                        System.out.println(value.trim());
                    }
                });
    }
}
