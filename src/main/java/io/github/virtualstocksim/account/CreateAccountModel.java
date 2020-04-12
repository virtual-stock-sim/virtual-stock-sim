package io.github.virtualstocksim.account;

public class CreateAccountModel {
    private String email, username;


    public CreateAccountModel(String email, String username)
    {
        this.email = email;
        this.username = username;
    }

    public CreateAccountModel(String username)
    {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }


}
