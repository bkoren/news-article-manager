# News Article Manager (Programming in Java 1)

Desktop Swing application for managing news articles.

**Summary:** the app downloads articles from RSS feeds, saves them into a local
MS SQL Server database, and lets you browse and manage them in a desktop window.
You log in with a user account; administrators can also load new articles from
the feeds. Setting it up means: create the database, tell the app how to connect
to it, then build and run.

Built as a project for the course Programming in Java 1 at Algebra University, Zagreb.

## 🚀 Features

### Users and roles

* Login form with simple registration for new users
* Two roles: **Administrator** and **User**

### Administrator

* Load new articles from RSS feeds (articles and images are saved locally)
* Delete all data from the database, including downloaded images

### User

* Browse and manage articles (create, read, update, delete)
* Manage related entities and link them to articles
* Drag and drop to link entities to an article
* Export an entity to XML (JAXB)

### Interface

* Organized views using tabs (`JTabbedPane`), each panel in its own class
* Tables with custom table models (`JTable` + `AbstractTableModel`)
* Navigation with menus (`JMenu`)
* Modern dark look with FlatLaf

## 🛠️ Tech Stack

* Java 21
* Java Swing + FlatLaf
* Maven (multi-module: `app` → `dao` → `utilities`)
* MS SQL Server with stored procedures (Repository pattern)
* JDBC (`CallableStatement` / `PreparedStatement`)
* RSS / XML parsing with `HttpURLConnection`
* JAXB for XML export

## 📡 RSS Feeds

Articles are loaded from public NASA RSS feeds.

> ⚠️ **Note:** These feeds are run by NASA, not by me. They may change, move,
> or go offline in the future. If loading fails, the feed addresses in the
> project may need to be updated.

## Requirements

- JDK 21
- Maven 3.9+
- MS SQL Server running locally
- `sqlcmd` (command-line tool for running SQL scripts)

## Setup

### 1 Clone the repository

git clone https://github.com/bkoren/news-article-manager.git
cd news-article-manager

### 2. Choose a database password

Pick a password for the application's database login. You will need it twice:
once in the provisioning script, and once in your config file.

Open `db/00_provision.sql` and replace `<SET_LOCALLY>` with your password:

```sql
CREATE LOGIN news_app WITH
    PASSWORD = '<SET_LOCALLY>',
    CHECK_POLICY = ON,
    DEFAULT_DATABASE = NewsAppDb;
GO

CREATE USER news_app FOR LOGIN news_app;
GO
```

If you want to use a login you already have, replace `news_app` with your own
login name instead.

### 3. Create the database and schema

Run the scripts **in this exact order** — each one depends on the previous.
Open a command prompt in the project root folder and run:

```
sqlcmd -S localhost -E -i db/00_provision.sql
sqlcmd -S localhost -E -i db/01_init.sql
sqlcmd -S localhost -E -i db/02_proc_crud.sql
sqlcmd -S localhost -E -i db/03_proc_delete_all.sql
sqlcmd -S localhost -E -i db/04_proc_user.sql
```

### 4. Configure the connection

Copy `config.properties.example` to `config.properties` (same folder), then fill
in your values:

```
server   = localhost
database = NewsAppDb
user     = news_app
password = <the password you set in step 1>
```

> 🔒 `config.properties` is listed in `.gitignore`, so your password is never
> uploaded to GitHub.

### 5. Build and run

From the project root folder:

```
mvn clean install
mvn -pl app exec:java
```

## First use

1. Log in with the seeded admin account: username `admin`, password `Admin123!`
2. Open **Admin -> Load new articles** to fill the database with articles and
   images from the RSS feeds.

> 💡 This is a default password for local testing. Change it if you use the
> app outside your own computer.
