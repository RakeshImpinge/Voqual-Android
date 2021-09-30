package com.westernsydneyunipx.model;

/**
 * @author PA1810.
 */
public class User {

    private int id;
    private String first_name;
    private String username;
    private String updated_at;
    private String email;
    private int age;
    private int researcher_id;
    private String last_name;
    private String created_at;
    private String mobile;
    private String profile_pic;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getResearcher_id() {
        return researcher_id;
    }

    public void setResearcher_id(int researcher_id) {
        this.researcher_id = researcher_id;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
    public String getProfile_pic()
    {
        return profile_pic;
    }

    public void setProfile_pic(String profile_pic)
    {
        this.profile_pic=profile_pic;
    }

    @Override
    public String toString() {
        return first_name + " " + last_name;
    }
}
