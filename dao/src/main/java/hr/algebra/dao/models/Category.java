package hr.algebra.dao.models;

import java.util.Objects;

public class Category implements Comparable<Category> {

    private final int categoryId;
    private String name;

    public Category(int categoryId, String name) {
        this.categoryId = categoryId;
        this.name = name;
    }

    public int getId() {
        return categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int compareTo(Category other) {
        return String.CASE_INSENSITIVE_ORDER.compare(
                name == null ? "" : name,
                other.name == null ? "" : other.name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Category other)) {
            return false;
        }
        return Objects.equals(name, other.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return name;
    }
}