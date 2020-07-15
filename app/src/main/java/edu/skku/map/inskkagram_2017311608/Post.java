package edu.skku.map.inskkagram_2017311608;

import android.net.Uri;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Post {
    public User user;
    public String contents;
    public String tags;
    public String imguri;
    public boolean pub;

    public Post(){

    }

    public Post(User user, String contents, String tags, String imguri, boolean pub){
        this.user = user;
        this.contents = contents;
        this.tags = tags;
        this.imguri = imguri;
        this.pub = pub;
    }

    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("user", user);
        result.put("contents", contents);
        result.put("tags", tags);
        result.put("imguri", imguri);
        result.put("pub", pub);
        return result;
    }
}
