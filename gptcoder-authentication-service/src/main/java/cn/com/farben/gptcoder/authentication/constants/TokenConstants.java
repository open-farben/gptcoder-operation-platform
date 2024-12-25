package cn.com.farben.gptcoder.authentication.constants;

public class TokenConstants {
    /** jwt签名的私钥 */
    public static final String JWT_SIGN_PRIVATE_KEY = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAMfWXw9XgYqUWI/LRQGZhLlZQR9GPw4pWxGtqYDRYOxrubUMiTdGWY4VnAIzO+qr0b3TkXhzKzPyrIZ4lhh4liYikX5tM3FiRLIZRgdtiIHOIS7/93mZUlCj7qfjbl37Cx8iSms5XAq/GKYlD88i1U3zfGROxrIp9IFlkP/zobfDAgMBAAECgYAN1ygQTPQ439teYiquWK6bk7Xx75CQb4bSK5/wvw+icDeVdX/PZzzunUqH0HqW3z2QkyWhHQDiAS23xoRGv5tOe1chnY6ysgtIMzmubuIGxZwZz8na50MCSN/zceUyS7E5L1Pw++/I5nO3XynEL389yPQvSps9arj1ORDnzNtEoQJBAMuJqejSQkiQ6gifFStQBX5lsc9zp2OvyDTalgY22nix8RpWaJvwhr8wnVJ/RKIKYIB1k+fPgAqnlYrVlDUFGXECQQD7WIqJqd6TW8iZtgY/to8ravt5vbR65i6mydRIreMAND6jVAq+UHPpiYSSNE6l0s7BtNnVMqPiJEKKPfAvdupzAkAS6E7oWd/ebdfPxTdqNpzMCRZjdxLtp76pakTVfvL8k0sabzx4f/eEezeiGkB97QjZ5hI/Tn9OfhLeOsjxw++BAkB1XydymczCa34FqabJYesBj3fXper96Ten02A6gFkc40jM5JYPwWofiIRL8fk4HnVGp7mBgaBeHiikfcAHqjRZAkAuru5Y/QG3llirubW1R3s4ywe4MoRI+j1dK+gvATah1kF+Oa8hDqC+JdCnYhpL2yHijpXffbuIHb+nBX9A2zEZ";

    /** 访问令牌有效期：分钟 */
    public static final int ACCESS_TOKEN_PERIOD = 60;

    /** 刷新令牌有效期：分钟 */
    public static final int REFRESH_TOKEN_PERIOD = 120;

    private TokenConstants() {
        throw new IllegalStateException("常量类不允许实例化");
    }
}
