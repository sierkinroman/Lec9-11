## About
There are two entities: **Book \*-- Author** (one author has many books).

REST API:
1) CRUD Book:
   - **POST /api/books** - create new book
     ```
     {
         "title": "bookTitle",
         "isbn": "isbn",
         "publishedDate": "2022-01-20",
         "authorId": 1
     }
     ```
   - **GET /api/books/{id}** - get book by id
   - **GET /api/books** - get all books
   - **PUT /api/books/{id}** - update book with given id
     ```
     {
         "title": "newTitle",
         "isbn": "newIsbn",
         "publishedDate": "2022-01-22",
         "authorId": 1
     }
     ```
   - **DELETE /api/books/{id}** - delete book with given id
<br><br>
2) Search Book by two fields (authorId or year) with pagination:
    - **POST /api/books/_search**
      ```
      {
          "authorId": 1,
          "year": 2002,
          "page": 1,
          "size": 3
      }
      ```
3) Get all Authors:
    - **GET /api/authors**

## Set up database connection
The database will be created automatically 
(present parameter in jdbc url *createDatabaseIfNotExist=true*).

But check **mysql port**, **username** and **password** in application.properties

Scripts schema.sql and data.sql will execute automatically on startup.

## Run application
Run the `main` method from `Lec911Application` class.

or

- Open terminal in project's root directory
- `mvn spring-boot:run`
