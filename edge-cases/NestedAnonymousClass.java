import java.util.concurrent.Callable;

public class NestedAnonymousClass {
    public Runnable createProcessor(final String prefix) {
        return new Runnable() {
            @Override
            public void run() {
                Callable<String> loader = new Callable<String>() {
                    @Override
                    public String call() {
                        return prefix + "-loaded";
                    }
                };

                Runnable inner = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            System.out.println(new Callable<String>() {
                                @Override
                                public String call() throws Exception {
                                    return loader.call().toUpperCase();
                                }
                            }.call());
                        } catch (Exception ex) {
                            System.out.println(ex.getMessage());
                        }
                    }
                };

                inner.run();
            }
        };
    }
}
