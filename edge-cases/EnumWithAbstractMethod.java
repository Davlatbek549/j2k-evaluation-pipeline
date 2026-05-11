public enum EnumWithAbstractMethod {
    ADD {
        @Override
        public int apply(int left, int right) {
            return left + right;
        }
    },
    SUBTRACT {
        @Override
        public int apply(int left, int right) {
            return left - right;
        }
    },
    MULTIPLY {
        @Override
        public int apply(int left, int right) {
            return left * right;
        }
    },
    DIVIDE {
        @Override
        public int apply(int left, int right) {
            if (right == 0) {
                throw new IllegalArgumentException("Cannot divide by zero");
            }
            return left / right;
        }
    };

    public abstract int apply(int left, int right);
}
