CREATE DATABASE NewsAppDb;
GO

-- Server-level login (the "account-password")
CREATE LOGIN news_app WITH
    PASSWORD = '<SET_LOCALLY>',
    CHECK_POLICY = ON,
    DEFAULT_DATABASE = NewsAppDb;
GO

USE NewsAppDb;
GO
CREATE USER news_app FOR LOGIN news_app;
GO

-- 4. Least privilege: EXECUTE on stored procedures only.
--    No SELECT, no INSERT, no direct table access.
GRANT EXECUTE ON SCHEMA::dbo TO news_app;
GO