GO

CREATE TABLE [dbo].[Source] 
(
    [IDSource]  INT IDENTITY(1,1) NOT NULL,
    [Name]      NVARCHAR(100)     NOT NULL,
    [FeedUrl]   NVARCHAR(400)     NOT NULL,

    CONSTRAINT PK_Source PRIMARY KEY ([IDSource]),
    CONSTRAINT UQ_Source_FeedUrl UNIQUE ([FeedUrl])
);
GO

CREATE TABLE [dbo].[Author] 
(
    [IDAuthor]  INT IDENTITY(1,1) NOT NULL,
    [Name]  NVARCHAR(150)		  NOT NULL,   

    CONSTRAINT PK_Author PRIMARY KEY ([IDAuthor]),
    CONSTRAINT UQ_Author_FullName UNIQUE ([Name])
);
GO

CREATE TABLE [dbo].[Category] 
(
    [IDCategory] INT IDENTITY(1,1) NOT NULL,
    [Name]		 NVARCHAR(100)     NOT NULL,

    CONSTRAINT PK_Category PRIMARY KEY ([IDCategory]),
    CONSTRAINT UQ_Category_Name UNIQUE ([Name])
);
GO

CREATE TABLE [dbo].[Article] 
(
    [IDArticle]		INT IDENTITY(1,1) NOT NULL,
    [SourceID]		INT               NOT NULL,
    [Title]			NVARCHAR(300)     NOT NULL,
    [Description]	NVARCHAR(MAX)     NULL,
    [Link]			NVARCHAR(500)     NOT NULL,
    [PublishedAt]	DATETIME	       NULL,
    [ImagePath]		NVARCHAR(300)     NULL,

    CONSTRAINT PK_Article PRIMARY KEY ([IDArticle]),
    CONSTRAINT UQ_Article_Link UNIQUE ([Link]),
    CONSTRAINT FK_Article_Source
        FOREIGN KEY ([SourceID]) REFERENCES [dbo].[Source] ([IDSource])       
);
GO

CREATE TABLE [dbo].[ArticleAuthor] 
(
    ArticleID  INT NOT NULL,
    AuthorID   INT NOT NULL,

    CONSTRAINT PK_ArticleAuthor PRIMARY KEY ([ArticleID], [AuthorID]),
	CONSTRAINT FK_ArticleAuthor_Article
        FOREIGN KEY ([ArticleID]) REFERENCES [dbo].[Article] ([IDArticle]),            
	CONSTRAINT FK_ArticleAuthor_Author
        FOREIGN KEY ([AuthorID]) REFERENCES [dbo].[Author] ([IDAuthor])        
);
GO

CREATE TABLE [dbo].[ArticleCategory] 
(
    ArticleID   INT NOT NULL,
    CategoryID  INT NOT NULL,

    CONSTRAINT PK_ArticleCategory PRIMARY KEY ([ArticleId], [CategoryId]),    
	CONSTRAINT FK_ArticleCategory_Article 
		FOREIGN KEY ([ArticleID]) REFERENCES [dbo].[Article] ([IDArticle]),
    CONSTRAINT FK_ArticleCategory_Category 
		FOREIGN KEY ([CategoryID]) REFERENCES [dbo].[Category] ([IDCategory])        
);
GO

CREATE TABLE [dbo].[User] 
(
    [IDUser]		INT IDENTITY(1,1) NOT NULL,
    [Username]		NVARCHAR(50)      NOT NULL,
    [PasswordHash]	NVARCHAR(255)     NOT NULL,
    [PasswordSalt]	NVARCHAR(100)     NOT NULL,
    [Role]			NVARCHAR(10)      NOT NULL,
  
	CONSTRAINT PK_User PRIMARY KEY ([IDUser]),
    CONSTRAINT UQ_User_Username UNIQUE ([Username]),
    CONSTRAINT CK_User_Role CHECK ([Role] IN (N'ADMIN', N'USER'))
);
GO


