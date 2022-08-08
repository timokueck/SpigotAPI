package me.TechsCode.SpigotAPI.data;

public enum UserVerificationStatus {
    UNVERIFIED("unverified"),
    VERIFIED("verified"),
    POST_NOT_FOUND("post_not_found"),
    UNKNOWN("unknown");

    public final String name;

    UserVerificationStatus(String name) {
        this.name = name;
    }

    public static UserVerificationStatus of(String name){
        switch (name){
            case "unverified":
                return UNVERIFIED;
            case "verified":
                return VERIFIED;
            case "post_not_found":
                return POST_NOT_FOUND;
            default:
                return UNKNOWN;
        }
    }
}
