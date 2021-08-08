//BookStacker - BookStack API wrapper for Java
//Copyright (c) 2021 Keksuccino.
//BookStacker is licensed under MIT.

package de.keksuccino.bookstacker;

public class BookStackPage {

    public int id;
    public int book_id;
    public int chapter_id;
    public String name;
    public String slug;
    public int priority;
    public boolean draft;
    public boolean template;
    public String created_at;
    public String updated_at;
    public BookStackUser created_by;
    public BookStackUser updated_by;
    public BookStackUser owned_by;
    public String markdown;
    public String html;
    public int revision_count;

}
