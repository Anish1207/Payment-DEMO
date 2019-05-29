package com.perpule.pays;

public class ValidateOtpResponse {
    private String access_token;
    private String expires;
    private String scope;
    private String resourceOwnerId;

    public ValidateOtpResponse(String access_token, String expires, String scope, String resourceOwnerId) {
        this.access_token = access_token;
        this.expires = expires;
        this.scope = scope;
        this.resourceOwnerId = resourceOwnerId;
    }

    public ValidateOtpResponse() {
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getExpires() {
        return expires;
    }

    public void setExpires(String expires) {
        this.expires = expires;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getResourceOwnerId() {
        return resourceOwnerId;
    }

    public void setResourceOwnerId(String resourceOwnerId) {
        this.resourceOwnerId = resourceOwnerId;
    }
}
