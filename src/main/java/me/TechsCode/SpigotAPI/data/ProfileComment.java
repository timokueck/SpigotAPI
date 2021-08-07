package me.TechsCode.SpigotAPI.data;

import com.google.gson.JsonObject;
import org.json.simple.JSONObject;

public class ProfileComment {
    private final String commentId, userId, message;

    public ProfileComment(String commentId, String userId, String message) {
        this.commentId = commentId;
        this.userId = userId;
        this.message = message;
    }

    public String getCommentId() {
        return commentId;
    }

    public String getUserId() {
        return userId;
    }

    public String getText() {
        return message;
    }

    public JsonObject getState() {
        JsonObject comment = new JsonObject();
        comment.addProperty("commentId", commentId);
        comment.addProperty("userId", userId);
        comment.addProperty("message", message);

        return comment;
    }
}
