USE NewsAppDb
GO

CREATE OR ALTER PROC [dbo].[p_Article_Create]
    @SourceID    INT,
    @Title       NVARCHAR(300),
    @Description NVARCHAR(MAX),
    @Link        NVARCHAR(500),
    @PublishedAt DATETIME,
    @ImagePath   NVARCHAR(300)
AS
BEGIN
    SET NOCOUNT ON;                      

	IF EXISTS(SELECT 1 FROM [dbo].[Article] WHERE [Link] = @Link)
	BEGIN
		SELECT [IDArticle] FROM [dbo].[Article] WHERE [Link] = @Link;

		RETURN;
	END;

    INSERT INTO [dbo].[Article]
        ([SourceID], [Title], [Description], [Link], [PublishedAt], [ImagePath])
    VALUES
        (@SourceID, @Title, @Description, @Link, @PublishedAt, @ImagePath);

	SELECT CAST(SCOPE_IDENTITY() AS INT);          
END;
GO

CREATE OR ALTER PROC [dbo].[p_Article_Read]
AS
BEGIN
    SET NOCOUNT ON;

	SELECT
		[a].[IDArticle],
		[a].[SourceID],
		[a].[Title],
		[a].[Description],
		[a].[Link],
		[a].[PublishedAt],
		[a].[ImagePath],
		[s].[IDSource],
		[s].[Name],
		[s].[FeedUrl]
    FROM        
		[dbo].[Article] AS a
    JOIN  
		[dbo].[Source] AS s ON [s].[IDSource] = [a].[SourceID]
    ORDER BY    
		[a].[PublishedAt] DESC;           
END;
GO

CREATE OR ALTER PROC [dbo].[p_Article_Update]
    @ArticleID   INT,
    @SourceID	 INT,
    @Title       NVARCHAR(300),
    @Description NVARCHAR(MAX),
    @Link        NVARCHAR(500),
    @PublishedAt DATETIME2,
    @ImagePath   NVARCHAR(300)
AS
BEGIN
    SET NOCOUNT ON;

    UPDATE [dbo].[Article]
    SET 
		[SourceID]    = @SourceID,
        [Title]       = @Title,
        [Description] = @Description,
        [Link]        = @Link,
        [PublishedAt] = @PublishedAt,
        [ImagePath]   = @ImagePath
    WHERE 
		[IDArticle] = @ArticleID;  
END;
GO

CREATE OR ALTER PROC [dbo].[p_Article_Delete]
    @ArticleID INT     
AS
BEGIN
    SET NOCOUNT ON;    
  
	BEGIN TRAN
	BEGIN TRY
		SELECT [imagePath] FROM [dbo].[Article] WHERE [IDArticle] = @ArticleID;

		DELETE FROM [dbo].[ArticleAuthor]   WHERE [ArticleID] = @ArticleID;  
		DELETE FROM [dbo].[ArticleCategory] WHERE [ArticleID] = @ArticleID;  
		DELETE FROM [dbo].[Article]         WHERE [IDArticle] = @ArticleID;  
	
	COMMIT TRAN;
	END TRY

	BEGIN CATCH
		ROLLBACK TRAN;

		THROW;
	END CATCH
	
END;
GO

CREATE OR ALTER PROC [dbo].[p_Article_AddAuthor]
	@ArticleID INT,
	@AuthorID  INT
AS
BEGIN
	SET NOCOUNT ON;

	IF EXISTS (
		SELECT 1 FROM [dbo].[ArticleAuthor] 
		WHERE [ArticleID] = @ArticleID AND [AuthorID] = @AuthorID
	) RETURN;

	INSERT INTO [dbo].[ArticleAuthor] 
		(ArticleID, AuthorID)
	VALUES
		(@ArticleID, @AuthorID);

END;
GO

CREATE OR ALTER PROC [dbo].[p_Article_GetAuthors]
	@ArticleID INT
AS
BEGIN
	SET NOCOUNT ON;

	SELECT 
		[a].[IDAuthor],
		[a].[Name]
	FROM 
		[ArticleAuthor] AS aa
	JOIN
		[Author] AS a ON [aa].[AuthorID] = [a].[IDAuthor]
	WHERE
		[aa].[ArticleID] = @ArticleID

END;
GO

CREATE OR ALTER PROC [dbo].[p_Article_ClearAuthors]
	@ArticleID INT
AS
BEGIN
	SET NOCOUNT ON;

	DELETE FROM [ArticleAuthor] WHERE [ArticleID] = @ArticleID;

END;
GO

CREATE OR ALTER PROC [dbo].[p_Article_AddCategory]
	@ArticleID  INT,
	@CategoryID INT
AS
BEGIN
	SET NOCOUNT ON;

	IF EXISTS (
		SELECT 1 FROM [dbo].[ArticleCategory] 
		WHERE [ArticleID] = @ArticleID AND [CategoryID] = @CategoryID
	) RETURN;

	INSERT INTO [dbo].[ArticleCategory] 
		(ArticleID, CategoryID)
	VALUES
		(@ArticleID, @CategoryID);

END;
GO

CREATE OR ALTER PROC [dbo].[p_Article_GetCategories]
	@ArticleID INT
AS
BEGIN
	SET NOCOUNT ON;

	SELECT 
		[c].[IDCategory],
		[c].[Name]
	FROM 
		[ArticleCategory] AS ac
	JOIN
		[Category] AS c ON [ac].[CategoryID] = [c].[IDCategory]
	WHERE
		[ac].[ArticleID] = @ArticleID

END;
GO

CREATE OR ALTER PROC [dbo].[p_Article_ClearCategories]
	@ArticleID INT
AS
BEGIN
	SET NOCOUNT ON;

	DELETE FROM [ArticleCategory] WHERE [ArticleID] = @ArticleID;

END;
GO

CREATE OR ALTER PROC [dbo].[p_Author_Create]
    @Name NVARCHAR(200)
AS
BEGIN
    SET NOCOUNT ON

	IF EXISTS(SELECT 1 FROM [dbo].[Author] WHERE [Name] = @Name)
	BEGIN
		SELECT [IDAuthor] FROM [dbo].[Author] WHERE [Name] = @Name

		RETURN;
	END;

    INSERT INTO [dbo].[Author] ([Name]) VALUES (@Name);    

	SELECT CAST(SCOPE_IDENTITY() AS INT);   
END;
GO

CREATE OR ALTER PROC [dbo].[p_Author_Read]
AS
BEGIN
    SET NOCOUNT ON;

    SELECT 
		[IDAuthor],
		[Name]
    FROM 
		[dbo].[Author]
		
END;
GO

CREATE OR ALTER PROC [dbo].[p_Author_Update]
    @AuthorID INT,
    @Name     NVARCHAR(200)
AS
BEGIN
    UPDATE [dbo].[Author]
    SET 
		[Name] = @Name
    WHERE 
		[IDAuthor] = @AuthorID;
END;
GO

CREATE OR ALTER PROC [dbo].[p_Author_Delete]
    @AuthorID INT
AS
BEGIN     
	SET NOCOUNT ON;
	
	BEGIN TRAN
	BEGIN TRY
		DELETE FROM [dbo].[ArticleAuthor]	WHERE [AuthorID] = @AuthorID;
		DELETE FROM [dbo].[Author]			WHERE [IDAuthor] = @AuthorID;   
	COMMIT TRAN;
	END TRY
	BEGIN CATCH
		ROLLBACK TRAN;

		THROW;
	END CATCH
END;
GO

CREATE OR ALTER PROC [dbo].[p_Category_Create]
	@Name NVARCHAR(200)
AS
BEGIN
	SET NOCOUNT ON;

	IF EXISTS(SELECT 1 FROM [dbo].[Category] WHERE [Name] = @Name)
	BEGIN
		SELECT [IDCategory] FROM [dbo].[Category] WHERE [Name] = @Name

		RETURN;
	END;

	INSERT INTO [dbo].[Category] ([Name]) VALUES (@Name);

	SELECT CAST(SCOPE_IDENTITY() AS INT);
END;
GO

CREATE OR ALTER PROC [dbo].[p_Category_Read]
AS
BEGIN
	SET NOCOUNT ON;

	SELECT
		[IDCategory],
		[Name]
	FROM 
		[dbo].[Category]
	ORDER BY
		[Name];

END;
GO

CREATE OR ALTER PROC [dbo].[p_Category_Update]
	@CategoryID	 INT,
	@Name		 NVARCHAR(200)
AS
BEGIN
	SET NOCOUNT ON;

	UPDATE [dbo].[Category]
	SET
		[Name] = @Name
	WHERE
		[IDCategory] = @CategoryID;

END;
GO

CREATE OR ALTER PROC [dbo].[p_Category_Delete]
	@CategoryID INT
AS
BEGIN
	SET NOCOUNT ON;

	BEGIN TRAN
	BEGIN TRY
		DELETE FROM [dbo].[ArticleCategory]	 WHERE [CategoryID] = @CategoryID;
		DELETE FROM [dbo].[Category]		 WHERE [IDCategory] = @CategoryID;
	COMMIT TRAN;
	END TRY
	BEGIN CATCH
		ROLLBACK TRAN;

		THROW;
	END CATCH
END;
GO

CREATE OR ALTER PROC [dbo].[p_Source_Create]
	@Name		NVARCHAR(200),
	@FeedUrl	NVARCHAR(500)
AS
BEGIN
	SET NOCOUNT ON;

	IF EXISTS(SELECT 1 FROM [dbo].[Source] WHERE FeedUrl = @FeedUrl)
	BEGIN
		SELECT [IDSource] FROM [dbo].[Source] WHERE [FeedUrl] = @FeedUrl

		RETURN;
	END;

	INSERT INTO [dbo].[Source] ([Name], [FeedUrl]) VALUES (@Name, @FeedUrl);

	SELECT CAST(SCOPE_IDENTITY() AS INT);
END;
GO

CREATE OR ALTER PROC [dbo].[p_Source_Read]
AS
BEGIN
	SET NOCOUNT ON;

	SELECT
		[IDSource],
		[Name],
		[FeedUrl]
	FROM
		[dbo].[Source]

END;
GO

CREATE OR ALTER PROC [dbo].[p_Source_Update]
	@SourceID	INT,
	@Name		NVARCHAR(200),
	@FeedUrl	NVARCHAR(500)
AS
BEGIN
	SET NOCOUNT ON;

	UPDATE [dbo].[Source]
	SET
		[Name]	  = @Name,
		[FeedUrl] = @FeedUrl
	WHERE 
		[IDSource] = @SourceID
END;
GO

CREATE OR ALTER PROC [dbo].[p_Source_Delete]
	@SourceID INT
AS
BEGIN
	SET NOCOUNT ON;

	IF EXISTS (SELECT 1 FROM [dbo].[Article] WHERE [SourceID] = @SourceID)
	RETURN 2;

	DELETE FROM [dbo].[Source] WHERE [IDSource] = @SourceID;
	RETURN 0;	
END;
GO