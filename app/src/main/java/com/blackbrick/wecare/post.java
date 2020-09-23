package com.blackbrick.wecare;


public class post {

    public String user_id, image_url, desc;

    public post(){

    }

    public post(String user_id, String image_url, String desc) {
        this.user_id = user_id;
        this.image_url = image_url;
        this.desc = desc;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
