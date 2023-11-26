package com.rpl9.ratemykos.model;

import java.util.Date;
import java.util.List;

public class Comment {
    public int comment_id;
    public int user_id;
    public String username;
    public String comment;
    public int kos_id;
    public int reply_id;
    public Date created_at;
    public List<Comment> replies;
}
