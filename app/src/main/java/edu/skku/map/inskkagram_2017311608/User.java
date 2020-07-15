package edu.skku.map.inskkagram_2017311608;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class User implements Serializable {
    public String username;
    public String password;
    public String fullname;
    public String birthday;
    public String email;

    public User(){

    }

    public User(String username, String password, String fullname, String birthday, String email){
        this.username = username;
        this.password = password;
        this.fullname = fullname;
        this.birthday = birthday;
        this.email = email;
    }

    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("username", username);
        result.put("password", password);
        result.put("fullname", fullname);
        result.put("birthday", birthday);
        result.put("email", email);
        return result;
    }

}
