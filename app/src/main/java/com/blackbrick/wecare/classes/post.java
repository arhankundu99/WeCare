package com.blackbrick.wecare.classes;


public class post {

    public String userId, image_url, desc;

    public post(){

    }

    public post(String userId, String image_url, String desc) {
        this.userId = userId;
        this.image_url = image_url;
        this.desc = desc;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getImageUrl() {
        return image_url;
    }

    public void setImageUrl(String imageUrl) {
        this.image_url = imageUrl;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
