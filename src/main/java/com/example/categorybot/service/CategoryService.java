package com.example.categorybot.service;

public interface CategoryService {

    void addRootElement(String name);

    void addChildElement(String parent, String child);

    String viewTree(StringBuilder stringBuilder, long parentId, int depth);

    void deleteCategoryAndDescendants(String categoryName);

    long findMinParent();

    boolean checkIfCategoryExist(String s);
}
