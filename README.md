# News Article Manager (Programiranje u Javi 1)

Desktop Swing application for managing news articles.

**In short:** the app downloads articles from RSS feeds, saves them into a local
MS SQL Server database, and lets you browse and manage them in a desktop window.
You log in with a user account; administrators can also load new articles from
the feeds. Setting it up means: create the database, tell the app how to connect
to it, then build and run.

Built as a second-year project for the course Programming in Java 1 at Algebra University, Zagreb.

## Requirements

- JDK 21
- Maven 3.9+
- MS SQL Server running locally

## Setup

### 1. Choose a database password

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

### 2. Create the database and schema

Run the scripts **in this exact order** — each one depends on the previous.
Open a command prompt in the project root folder and run:

```
sqlcmd -S localhost -E -i db/00_provision.sql
sqlcmd -S localhost -E -i db/01_init.sql
sqlcmd -S localhost -E -i db/02_proc_crud.sql
sqlcmd -S localhost -E -i db/03_proc_delete_all.sql
sqlcmd -S localhost -E -i db/04_proc_user.sql
```

### 3. Configure the connection

Copy `config.properties.example` to `config.properties` (same folder), then fill
in your values:

```
server   = localhost
database = NewsAppDb
user     = news_app
password = <the password you set in step 1>
```

### 4. Build and run

From the project root folder:

```
mvn clean install
mvn -pl app exec:java
```

## First use

1. Log in with the seeded admin account: username `admin`, password `Admin123!`
2. Open **Admin -> Load new articles** to fill the database with articles and
   images from the RSS feeds.
