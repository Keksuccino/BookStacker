//BookStacker - BookStack API wrapper for Java
//Copyright (c) 2021 Keksuccino.
//BookStacker is licensed under MIT.

package de.keksuccino.bookstacker;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class BookStack {

    protected String url;
    protected String apiToken;
    protected String apiSecret;

    public BookStack(String wikiUrl, String apiToken, String apiSecret) {
        this.url = wikiUrl;
        this.apiToken = apiToken;
        this.apiSecret = apiSecret;
    }

    public List<BookStackBook> getBooks(@Nullable List<BookStackChapter> cachedChapterList, @Nullable List<BookStackPage> cachedPageList) {
        List<BookStackBook> l = new ArrayList<BookStackBook>();
        try {
            URL url = new URL(this.url + "/api/books");
            HttpURLConnection http = (HttpURLConnection)url.openConnection();
            http.setRequestProperty("Authorization", "Token " + this.apiToken + ":" + this.apiSecret);

//            System.out.println("[DEBUG] getBooks: " + http.getResponseCode() + " " + http.getResponseMessage());

            BufferedReader br = null;
            String lines = "";
            try {
                br = new BufferedReader(new InputStreamReader(http.getInputStream(), StandardCharsets.UTF_8));
                String line;
                while ((line = br.readLine()) != null) {
                    lines += line;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            Gson gson = new GsonBuilder().create();

            BookStackBookContainer c = gson.fromJson(lines, BookStackBookContainer.class);

            List<BookStackPage> pages = cachedPageList;
            List<BookStackChapter> chapters = cachedChapterList;

            for (BookStackBook b : c.data) {

                //Add pages to the book object
                if (pages == null) {
                    pages = getPages();
                }
                List<BookStackPage> bpl = new ArrayList<BookStackPage>();
                for (BookStackPage p : pages) {
                    if ((p.book_id == b.id) && (p.chapter_id == 0)) {
                        bpl.add(p);
                    }
                }
                b.pages = bpl;

                //Add chapters to the book object
                if (chapters == null) {
                    chapters = getChapters(pages);
                }
                List<BookStackChapter> bcl = new ArrayList<BookStackChapter>();
                for (BookStackChapter ch : chapters) {
                    if (ch.book_id == b.id) {
                        bcl.add(ch);
                    }
                }
                b.chapters = bcl;

                //Add book to final book list
                l.add(b);
            }

            http.disconnect();
            try {
                if (br != null) {
                    br.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return l;
    }

    public List<BookStackChapter> getChapters(@Nullable  List<BookStackPage> cachedPagesList) {
        List<BookStackChapter> l = new ArrayList<BookStackChapter>();
        try {
            URL url = new URL(this.url + "/api/chapters");
            HttpURLConnection http = (HttpURLConnection)url.openConnection();
            http.setRequestProperty("Authorization", "Token " + this.apiToken + ":" + this.apiSecret);

//            System.out.println("[DEBUG] getChapters: " + http.getResponseCode() + " " + http.getResponseMessage());

            String lines = "";
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(http.getInputStream(), StandardCharsets.UTF_8));
                String line;
                while ((line = br.readLine()) != null) {
                    lines += line;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            Gson gson = new GsonBuilder().create();

            BookStackChapterContainer c = gson.fromJson(lines, BookStackChapterContainer.class);

            List<BookStackPage> pages = cachedPagesList;

            for (BookStackChapter ch : c.data) {

                //Add pages to chapter object
                if (pages == null) {
                    pages = getPages();
                }
                List<BookStackPage> bsp = new ArrayList<BookStackPage>();
                for (BookStackPage p : pages) {
                    if (p.chapter_id == ch.id) {
                        bsp.add(p);
                    }
                }
                ch.pages = bsp;

                //Add chapter to final chapter list
                l.add(ch);

            }

            http.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return l;
    }

    public List<BookStackPage> getPages() {
        List<BookStackPage> l = new ArrayList<BookStackPage>();
        try {
            URL url = new URL(this.url + "/api/pages");
            HttpURLConnection http = (HttpURLConnection)url.openConnection();
            http.setRequestProperty("Authorization", "Token " + this.apiToken + ":" + this.apiSecret);

//            System.out.println("[DEBUG] getPages: " + http.getResponseCode() + " " + http.getResponseMessage());

            String lines = "";
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(http.getInputStream(), StandardCharsets.UTF_8));
                String line;
                while ((line = br.readLine()) != null) {
                    lines += line;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            Gson gson = new GsonBuilder().create();

            BookStackPageSimpleContainer c = gson.fromJson(lines, BookStackPageSimpleContainer.class);

            for (BookStackPageSimple p : c.data) {
//                System.out.println("[DEBUG] getPages: " + "Getting content for page: " + p.name);
                l.add(getPage(p.id));
            }

            http.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return l;
    }

    public BookStackPage getPage(int pageId) {
        BookStackPage p = null;
        try {
            try {
                URL url = new URL(this.url + "/api/pages/" + pageId);
                HttpURLConnection http = (HttpURLConnection)url.openConnection();
                http.setRequestProperty("Authorization", "Token " + this.apiToken + ":" + this.apiSecret);

//                System.out.println("[DEBUG] getPage: " + http.getResponseCode() + " " + http.getResponseMessage());

                String lines = "";
                try {
                    BufferedReader br = new BufferedReader(new InputStreamReader(http.getInputStream(), StandardCharsets.UTF_8));
                    String line;
                    while ((line = br.readLine()) != null) {
                        lines += line;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Gson gson = new GsonBuilder().create();

                p = gson.fromJson(lines, BookStackPage.class);

                http.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return p;
    }

    public BookStackBook createBook(String name, String description) {
        try {

            URL url = new URL(this.url + "/api/books");
            HttpURLConnection http = (HttpURLConnection)url.openConnection();
            http.setRequestMethod("POST");
            http.setDoOutput(true);
            http.setRequestProperty("Authorization", "Token " + this.apiToken + ":" + this.apiSecret);
            http.setRequestProperty("Content-Type", "application/json; charset=utf-8");

            String data = "{\"name\":\"" + name + "\",\"description\":\"" + description + "\"}";

            byte[] out = data.getBytes(StandardCharsets.UTF_8);
            OutputStream stream = http.getOutputStream();
            stream.write(out);

//            System.out.println("[DEBUG] createBook: " + http.getResponseCode() + " " + http.getResponseMessage());

            String lines = "";
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(http.getInputStream(), StandardCharsets.UTF_8));
                String line;
                while ((line = br.readLine()) != null) {
                    lines += line;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            http.disconnect();

            Gson gson = new GsonBuilder().create();

            return gson.fromJson(lines, BookStackBook.class);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public BookStackChapter createChapter(int bookId, String name, String description) {
        try {

            URL url = new URL(this.url + "/api/chapters");
            HttpURLConnection http = (HttpURLConnection)url.openConnection();
            http.setRequestMethod("POST");
            http.setDoOutput(true);
            http.setRequestProperty("Authorization", "Token " + this.apiToken + ":" + this.apiSecret);
            http.setRequestProperty("Content-Type", "application/json; charset=utf-8");

            String data = "{\"book_id\":" + bookId + ",\"name\":\"" + name + "\",\"description\":\"" + description + "\"}";

            byte[] out = data.getBytes(StandardCharsets.UTF_8);
            OutputStream stream = http.getOutputStream();
            stream.write(out);

//            System.out.println("[DEBUG] createChapter: " + http.getResponseCode() + " " + http.getResponseMessage());

            String lines = "";
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(http.getInputStream(), StandardCharsets.UTF_8));
                String line;
                while ((line = br.readLine()) != null) {
                    lines += line;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            http.disconnect();

            Gson gson = new GsonBuilder().create();

            return gson.fromJson(lines, BookStackChapter.class);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public BookStackPage createPage(ContentType createFor, int bookOrChapterId, String name, String markdownContent) {
        try {

            if (createFor == ContentType.PAGE) {
                System.out.println("ERROR: CAN'T CREATE PAGE FOR CONTENT TYPE 'PAGE'!");
                return null;
            }

            URL url = new URL(this.url + "/api/pages");
            HttpURLConnection http = (HttpURLConnection)url.openConnection();
            http.setRequestMethod("POST");
            http.setDoOutput(true);
            http.setRequestProperty("Authorization", "Token " + this.apiToken + ":" + this.apiSecret);
            http.setRequestProperty("Content-Type", "application/json; charset=utf-8");

            //TODO add replaceText method to add char sequences that will be replaced on page creation

            //This is needed because otherwise the wiki would just throw an error while trying to parse the json
            //(it seems to hate backslashes with a passion)
            markdownContent = markdownContent.replace("\\", "\\\\");
            markdownContent = markdownContent.replace("\\/", "\\\\/");
            markdownContent = markdownContent.replace("\n", "\\n").replace("\r", "\\r");
            markdownContent = markdownContent.replace("\u2019", "\\u2019");
            markdownContent = markdownContent.replace("\"", "\\\"");

//            System.out.println("[DEBUG] createPage: " + name + ": markdownContent = " + markdownContent);

            String data;
            if (createFor == ContentType.BOOK) {
                data = "{\"book_id\":" + bookOrChapterId + ",\"name\":\"" + name + "\",\"markdown\":\"" + markdownContent + "\"}";
            } else {
                data = "{\"chapter_id\":" + bookOrChapterId + ",\"name\":\"" + name + "\",\"markdown\":\"" + markdownContent + "\"}";
            }

//            System.out.println("[DEBUG] createPage: " + name + ": data = " + data);

            byte[] out = data.getBytes(StandardCharsets.UTF_8);
            OutputStream stream = http.getOutputStream();
            stream.write(out);

//            System.out.println("[DEBUG] createPage: " + name + ": " + http.getResponseCode() + " " + http.getResponseMessage());

            String lines = "";
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(http.getInputStream(), StandardCharsets.UTF_8));
                String line;
                while ((line = br.readLine()) != null) {
                    lines += line;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            http.disconnect();

            Gson gson = new GsonBuilder().create();

            return gson.fromJson(lines, BookStackPage.class);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public enum ContentType {
        PAGE,
        CHAPTER,
        BOOK;
    }

    public @interface Nullable {
    }

}
