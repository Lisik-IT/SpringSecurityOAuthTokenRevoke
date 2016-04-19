package it.lisik.springutils;

public class RevocationRequest {
    private final String token;
    private final TokenType type;

    public RevocationRequest(String token, TokenType type) {
        this.token = token;
        this.type = type;
    }

    public String getToken() {
        return token;
    }

    public TokenType getType() {
        return type;
    }
}
