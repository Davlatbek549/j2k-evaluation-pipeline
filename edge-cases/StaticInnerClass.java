public class StaticInnerClass {
    private final String owner;

    public StaticInnerClass(String owner) {
        this.owner = owner;
    }

    public static class StaticNested {
        private static int created;
        private final String label;

        public StaticNested(String label) {
            this.label = label;
            created++;
        }

        public static int createdCount() {
            return created;
        }

        public String label() {
            return label;
        }
    }

    public class InstanceInner {
        private final int index;

        public InstanceInner(int index) {
            this.index = index;
        }

        public String describe() {
            return owner + "#" + index;
        }
    }

    public String combine() {
        StaticNested nested = new StaticNested("static");
        InstanceInner inner = new InstanceInner(7);
        return nested.label() + ":" + inner.describe() + ":" + StaticNested.createdCount();
    }
}
