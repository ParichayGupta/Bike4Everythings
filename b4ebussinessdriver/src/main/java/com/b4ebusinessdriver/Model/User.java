package com.b4ebusinessdriver.Model;

/**
 * Created by manishsingh on 04/01/18.
 */

public class User {

    //private variables
    int id;
    String name;
    String phone_number;
    String profile_image;

    // Empty constructor
    public User(){

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", phone_number='" + phone_number + '\'' +
                ", profile_image='" + profile_image + '\'' +
                '}';
    }
}
