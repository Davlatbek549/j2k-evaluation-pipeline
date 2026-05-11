import java.io.Closeable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ComplexGenerics {
    public static class Holder<T extends Number & Comparable<T> & Serializable> {
        private final T value;

        public Holder(T value) {
            this.value = value;
        }

        public T getValue() {
            return value;
        }
    }

    public <T extends Closeable & Serializable, R extends Comparable<? super R>>
    List<? extends R> transform(
            Map<? super String, ? extends List<? extends T>> input,
            Converter<? super T, ? extends R> converter
    ) throws Exception {
        List<R> results = new ArrayList<R>();
        for (List<? extends T> values : input.values()) {
            for (T value : values) {
                results.add(converter.convert(value));
            }
        }
        return results;
    }

    public interface Converter<I, O> {
        O convert(I input) throws Exception;
    }
}
