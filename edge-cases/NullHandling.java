import java.util.Optional;

public class NullHandling {
    public String displayName(User user) {
        if (user != null) {
            Profile profile = user.getProfile();
            if (profile != null) {
                String nickname = profile.getNickname();
                if (nickname != null) {
                    return nickname;
                }
            }
        }
        return "anonymous";
    }

    public Optional<String> optionalEmail(User user) {
        if (user == null) {
            return Optional.empty();
        }
        if (user.getProfile() == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(user.getProfile().getEmail());
    }

    public Integer riskyLength(String value) {
        if (value != null) {
            return value.length();
        }
        return null;
    }

    public static class User {
        private final Profile profile;

        public User(Profile profile) {
            this.profile = profile;
        }

        public Profile getProfile() {
            return profile;
        }
    }

    public static class Profile {
        private final String nickname;
        private final String email;

        public Profile(String nickname, String email) {
            this.nickname = nickname;
            this.email = email;
        }

        public String getNickname() {
            return nickname;
        }

        public String getEmail() {
            return email;
        }
    }
}
