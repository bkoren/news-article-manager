USE NewsAppDb
GO

---------------------- Article -------------------------
---------------------- CREATE -------------------------
CREATE OR ALTER PROC [dbo].[p_Article_Insert]
    @SourceID    INT,
    @Title       NVARCHAR(300),
    @Description NVARCHAR(MAX),
    @Link        NVARCHAR(500),
    @PublishedAt DATETIME,
    @ImagePath   NVARCHAR(300),
    @NewId       INT OUTPUT              
AS
BEGIN
    SET NOCOUNT ON;                      

    INSERT INTO [dbo].[Article]
        ([SourceID], [Title], [Description], [Link], [PublishedAt], [ImagePath])
    VALUES
        (@SourceID, @Title, @Description, @Link, @PublishedAt, @ImagePath);

    SET @NewId = SCOPE_IDENTITY();       

	RETURN 0;    
END;
GO

---------------------- READ -------------------------
CREATE OR ALTER PROC [dbo].[p_Article_GetById]
    @ArticleID INT
AS
BEGIN 
    SET NOCOUNT ON;

    SELECT 
		[IDArticle], 
		[SourceID], 
		[Title], 
		[Description],
        [Link], 
		[PublishedAt], 
		[ImagePath]
    FROM 
		[dbo].[Article]
    WHERE
		[IDArticle] = @ArticleID;
		
END;
GO

---------------------- READ -------------------------
CREATE OR ALTER PROCEDURE [dbo].[p_Article_GetAll]
AS
BEGIN
    SET NOCOUNT ON;

    SELECT  [a].[IDArticle],
            [a].[Title],
            [s].[Name] AS SourceName,  
            [a].[PublishedAt],
            [a].[SourceID],
            [a].[ImagePath]
    FROM        
		[dbo].[Article] AS a
    INNER JOIN  
		[dbo].[Source] AS s ON [s].[IDSource] = [a].[SourceID]
    ORDER BY    
		[a].[PublishedAt] DESC;       
    
END;
GO

---------------------- UPDATE -------------------------
CREATE OR ALTER PROCEDURE [dbo].[p_Article_Update]
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
    
	IF @@ROWCOUNT = 0 
	RETURN 1;
	
	RETURN 0;
END;
GO

---------------------- DELETE -------------------------
CREATE OR ALTER PROCEDURE [dbo].[p_Article_Delete]
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

    IF @@ROWCOUNT = 0 
	RETURN 1;           
   
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

    RETURN 0;
END;
GO

---------------------- Author -------------------------
---------------------- CREATE -------------------------
CREATE OR ALTER PROCEDURE [dbo].[p_Author_Insert]
    @Name NVARCHAR(200)
AS
BEGIN
    SET NOCOUNT ON

    INSERT INTO [dbo].[Author] ([Name]) 
	VALUES (@Name);
    
    RETURN 0;
END;
GO

---------------------- READ -------------------------
CREATE OR ALTER PROCEDURE [dbo].[p_Author_GetById]
    @AuthorID INT
AS
BEGIN
    SET NOCOUNT ON;

    SELECT [IDAuthor], 
		[Name]
    FROM
		[dbo].[Author]
    WHERE
		[IDAuthor] = @AuthorID;   
END;
GO

---------------------- READ -------------------------
CREATE OR ALTER PROCEDURE [dbo].[p_Author_GetAll]
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

---------------------- UPDATE -------------------------
CREATE OR ALTER PROCEDURE [dbo].[p_Author_Update]
    @AuthorID INT,
    @Name     NVARCHAR(200)
AS
BEGIN
    SET NOCOUNT ON;

    UPDATE [dbo].[Author]
    SET 
		[Name] = @Name
    WHERE 
		[IDAuthor] = @AuthorID;

	IF @@ROWCOUNT = 0 
	RETURN 1;

    RETURN 0;
END;
GO

---------------------- DELETE -------------------------
CREATE OR ALTER PROCEDURE [dbo].[p_Author_Delete]
    @AuthorID INT
AS
BEGIN
    SET NOCOUNT ON;
   
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

    RETURN 0;

END;
GO

---------------------- Category -------------------------
---------------------- CREATE -------------------------
CREATE OR ALTER PROC [dbo].[p_Category_Insert]
	@Name NVARCHAR(200)
AS
BEGIN
	SET NOCOUNT ON;

	INSERT INTO [dbo].[Category] ([Name])
	VALUES (@Name);

	RETURN 0;
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


---------------------- Source -------------------------
---------------------- CREATE -------------------------
CREATE OR ALTER PROC [dbo].[p_Source_Insert]
	@Name NVARCHAR(200),
	@FeedUrl NVARCHAR(500)
AS
BEGIN
	SET NOCOUNT ON;

	INSERT INTO [dbo].[Source] ([Name], [FeedUrl])
	VALUES (@Name, @FeedUrl);

	RETURN 0;
END;
GO

---------------------- READ -------------------------
CREATE OR ALTER PROC [dbo].[p_Source_GetById]
	@SourceID INT
AS
BEGIN
	SET NOCOUNT ON;

	SELECT 
		[Name],
		[FeedUrl]
	FROM 
		[dbo].[Source]
	WHERE 
		[IDSource] = @SourceID;

END;
GO

---------------------- READ -------------------------
CREATE OR ALTER PROC [dbo].[p_Source_GetAll]
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

---------------------- UPDATE -------------------------
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

	RETURN 0;

END;
GO

---------------------- DELETE -------------------------
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




