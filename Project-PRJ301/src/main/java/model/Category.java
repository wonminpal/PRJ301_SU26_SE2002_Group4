/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author LENOVO
 */
public class Category {

    private int id;
    private String name;
    private String description;
    private int parentId;
    private String slug;
    private boolean status;

    private List<Category> children = new ArrayList<>();

    public Category() {
    }

    public Category(int id, String name, String description, int parentId, String slug, boolean status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.parentId = parentId;
        this.slug = slug;
        this.status = status;
    }

    public List<Category> getChildren() {
        return children;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getParentId() {
        return parentId;
    }

    public String getSlug() {
        return slug;
    }

    public boolean isStatus() {
        return status;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return this.name;
    }

}
