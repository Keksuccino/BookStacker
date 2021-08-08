//BookStacker - BookStack API wrapper for Java
//Copyright (c) 2021 Keksuccino.
//BookStacker is licensed under MIT.

package de.keksuccino.bookstacker;

import java.util.ArrayList;
import java.util.List;

public class BookStackChapter {

    public int id;
    public int book_id;
    public String name;
    public String slug;
    public String description;
    public int priority;
    public String created_at;
    public String updated_at;
    public int created_by;
    public int updated_by;
    public int owned_by;

    public List<BookStackPage> pages = new ArrayList<BookStackPage>();

}
