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

    SELECT  [a].[IDArticle],
            [a].[Title],
            [a].[PublishedAt],
            [a].[SourceID],
            [a].[ImagePath],
            [s].[Name] AS SourceName
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
    @ArticleID INT,
    @ImagePath NVARCHAR(300) OUTPUT       
AS
BEGIN
    SET NOCOUNT ON;    
  
    SELECT 
		@ImagePath = [ImagePath] 
	FROM 
		[dbo].[Article] 
	WHERE 
		[IDArticle] = @ArticleID;          
   
    BEGIN TRAN;

	BEGIN TRY
		DELETE FROM [dbo].[ArticleAuthor]   WHERE [ArticleID] = @ArticleID;  
		DELETE FROM [dbo].[ArticleCategory] WHERE [ArticleID] = @ArticleID;  
		DELETE FROM [dbo].[Article]         WHERE [IDArticle] = @ArticleID;      
		
		COMMIT TRAN;
	END TRY
	BEGIN CATCH
		ROLLBACK TRAN;

		THROW;
	END CATCH;    
END;
GO

CREATE OR ALTER PROC [dbo].[p_Author_Create]
    @Name NVARCHAR(200)
AS
BEGIN
    SET NOCOUNT ON

    INSERT INTO [dbo].[Author] ([Name]) 
	VALUES (@Name);    

	SELECT CAST(SCOPE_IDENTITY() AS INT);   
END;
GO

CREATE OR ALTER PROC [dbo].[p_Author_Read]
AS
BEGIN
    SET NOCOUNT ON;

    SELECT 
		[IDAuthor], [Name]
    FROM 
		[dbo].[Author]
    ORDER BY 
		[Name];                         		
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
	BEGIN TRAN;
		
	BEGIN TRY;		
		DELETE FROM [dbo].[ArticleAuthor]	WHERE [AuthorID] = @AuthorID
		DELETE FROM [dbo].[Author]			WHERE [IDAuthor] = @AuthorID;   

		COMMIT TRAN;
	END TRY
	BEGIN CATCH
		ROLLBACK TRAN;

		THROW;
	END CATCH;  
END;
GO

CREATE OR ALTER PROC [dbo].[p_Category_Create]
	@Name NVARCHAR(200)
AS
BEGIN
	SET NOCOUNT ON;

	INSERT INTO [dbo].[Category] ([Name])
	VALUES (@Name);

	SELECT CAST(SCOPE_IDENTITY() AS INT);
END;
GO

---------------------- READ -------------------------
CREATE OR ALTER PROC [dbo].[p_Category_GetById]
	@CategoryID INT
AS
BEGIN
	SET NOCOUNT ON;

	SELECT
		[IDCategory],
		[Name]
	FROM
		[dbo].[Category]
	WHERE 
		[IDCategory] = @CategoryID;
END;
GO

---------------------- READ -------------------------
CREATE OR ALTER PROC [dbo].[p_Category_GetAll]
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

---------------------- UPDATE -------------------------
CREATE OR ALTER PROC [dbo].[p_Category_Update]
	@CategoryID INT,
	@Name NVARCHAR(200)
AS
BEGIN
	SET NOCOUNT ON;

	UPDATE [dbo].[Category]
	SET
		[Name] = @Name
	WHERE
		[IDCategory] = @CategoryID;

	IF @@ROWCOUNT = 0 
	RETURN 1;
	
	RETURN 0;

END;
GO

---------------------- DELETE -------------------------
CREATE OR ALTER PROC [dbo].[p_Category_Delete]
	@CategoryID INT
AS
BEGIN
	SET NOCOUNT ON;

	BEGIN TRAN;

	BEGIN TRY
		DELETE FROM [dbo].[ArticleCategory] WHERE [CategoryID] = @CategoryID;
		DELETE FROM [dbo].[Category] WHERE [IDCategory] = @CategoryID;

		COMMIT TRAN;
	END TRY
	BEGIN CATCH
		ROLLBACK TRAN;

		THROW;
	END CATCH;

	IF @@ROWCOUNT = 0
	RETURN 1;

	RETURN 0;

END;
GO

CREATE OR ALTER PROC [dbo].[p_Source_Create]
	@Name NVARCHAR(200),
	@FeedUrl NVARCHAR(500)
AS
BEGIN
	SET NOCOUNT ON;

	INSERT INTO [dbo].[Source] ([Name], [FeedUrl])
	VALUES (@Name, @FeedUrl);

	RETURN CAST(SCOPE_IDENTITY() AS INT);
END;
GO

CREATE OR ALTER PROC [dbo].[p_Source_Read]
AS
BEGIN
	SET NOCOUNT ON;

	SELECT
		[Name],
		[FeedUrl]
	FROM
		[dbo].[Source]
	ORDER BY
		[Name];
END;
GO

CREATE OR ALTER PROC [dbo].[p_Source_Update]
	@SourceID INT,
	@Name NVARCHAR(200),
	@FeedUrl NVARCHAR(500)
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
	@SourceID INT,
	@result INT OUTPUT
AS
BEGIN
	SET NOCOUNT ON;

	IF EXISTS (SELECT 1 FROM [dbo].[Article] WHERE [SourceID] = @SourceID)
	SET @result = 2;

	DELETE FROM [dbo].[Source] WHERE [IDSource] = @SourceID;
	SET @result = 0;	
END;
GO




