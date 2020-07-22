package by.aermakova.prettysip.logic.model;

/**
 * Model-helper for login on sip-server.
 */
public class UserSipInfo {

    private String username;
    private String password;
    private String hostname;

    public UserSipInfo() {
    }

    public UserSipInfo(String username, String password, String hostname) {
        this.username = username;
        this.password = password;
        this.hostname = hostname;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getHostname() {
        return hostname;
    }

    @Override
    public String toString() {
        return "UserSipInfo{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", hostname='" + hostname + '\'' +
                '}';
    }
}