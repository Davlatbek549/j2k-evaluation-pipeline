import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BuilderPattern {
    private final String name;
    private final int retries;
    private final boolean enabled;
    private final List<String> tags;

    private BuilderPattern(Builder builder) {
        this.name = builder.name;
        this.retries = builder.retries;
        this.enabled = builder.enabled;
        this.tags = Collections.unmodifiableList(new ArrayList<String>(builder.tags));
    }

    public String getName() {
        return name;
    }

    public int getRetries() {
        return retries;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public List<String> getTags() {
        return tags;
    }

    public static class Builder {
        private String name = "default";
        private int retries = 3;
        private boolean enabled = true;
        private List<String> tags = new ArrayList<String>();

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder retries(int retries) {
            this.retries = retries;
            return this;
        }

        public Builder enabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public Builder addTag(String tag) {
            this.tags.add(tag);
            return this;
        }

        public BuilderPattern build() {
            return new BuilderPattern(this);
        }
    }
}
