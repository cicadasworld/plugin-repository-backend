package gtcloud.plugin.repository.security;

public class JwtSubject {

    private String username;

    public JwtSubject(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
