package me.TechsCode.SpigotAPI.data;

import com.google.gson.JsonObject;

public class UserVerification {

    private final String userId, code;
    private String discord;
    private boolean verificationChecked;
    private UserVerificationStatus verificationStatus;
    private final long requestedTime;
    private long verifiedTime;

    public UserVerification(JsonObject jsonObject){
        this.userId = jsonObject.get("userId").getAsString();
        this.code = jsonObject.get("code").getAsString();
        this.verificationChecked = jsonObject.get("verificationChecked").getAsBoolean();
        this.verificationStatus = UserVerificationStatus.of(jsonObject.get("verificationStatus").getAsString());
        this.discord = jsonObject.get("discord").getAsString();
        this.requestedTime = jsonObject.get("requestedTime").getAsLong();
        this.verifiedTime = jsonObject.get("verifiedTime").getAsLong();
    }

    public UserVerification(String userId, String code) {
        this.userId = userId;
        this.code = code;
        this.verificationChecked = false;
        this.verificationStatus = UserVerificationStatus.UNVERIFIED;
        this.discord = null;
        this.requestedTime = System.currentTimeMillis() / 1000;
    }

    public String getUserId() {
        return userId;
    }

    public String getCode() {
        return code;
    }

    public boolean isverificationChecked() {
        return verificationChecked;
    }

    public void setVerified(boolean verified) {
        this.verificationChecked = verified;
        this.verifiedTime = System.currentTimeMillis() / 1000;
    }

    public UserVerificationStatus getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(UserVerificationStatus verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public String getDiscord() {
        return discord;
    }

    public long getRequestedTime() {
        return requestedTime;
    }

    public long getVerifiedTime() {
        return verifiedTime;
    }

    public void setDiscord(String discord) {
        this.discord = discord;
    }

    public JsonObject toJsonObject(){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("userId", userId);
        jsonObject.addProperty("code", code);
        jsonObject.addProperty("verificationChecked", verificationChecked);
        jsonObject.addProperty("verificationStatus", verificationStatus.name);
        jsonObject.addProperty("discord", discord);
        jsonObject.addProperty("requestedTime", requestedTime);
        jsonObject.addProperty("verifiedTime", verifiedTime);
        return jsonObject;
    }
}
