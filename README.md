# About
BookStacker is a Java wrapper for the [BookStack](https://github.com/BookStackApp/BookStack) API.

# How to Use
To use the API, just create a new `BookStack` instance.

```
import de.keksuccino.bookstacker.BookStack;

public static BookStack bookstack;

public static void main(String[] args) {
   
   String wikiUrl = "http://mywiki.com";
   String apiToken = "0000000000000000000";
   String apiSecret = "0000000000000000000";

   bookstack = new BookStack(wikiUrl, apiToken, apiSecret);
   
   bookstack.createBook("Some Book Title", "This is a book description.")
   
}
```

> **IMPORTANT**: You need an API token and secret to use the BookStack API. They can be generated in the user settings of your wiki.
