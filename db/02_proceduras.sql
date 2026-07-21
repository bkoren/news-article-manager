USE NewsAppDb
GO

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
		[SourceId], 
		[Title], 
		[Description],
        [Link], 
		[PublishedAt], 
		[ImagePath]
    FROM [dbo].[Article]
    WHERE[IDArticle] = @ArticleID;

    IF @@ROWCOUNT = 0 RETURN 1;          

    RETURN 0;
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
    FROM        [dbo].[Article] a
    INNER JOIN  [dbo].[Source]  s ON [s].[IDSource] = [a].[SourceID]
    ORDER BY    [a].[PublishedAt] DESC;       

    RETURN 0;                             
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
    SET [SourceID]    = @SourceID,
        [Title]       = @Title,
        [Description] = @Description,
        [Link]        = @Link,
        [PublishedAt] = @PublishedAt,
        [ImagePath]   = @ImagePath
    WHERE [IDArticle] = @ArticleID;

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
    SET XACT_ABORT ON; -- any error auto-rolls back the whole TRAN (no TRY/CATCH)
  
    SELECT 
		@ImagePath = [ImagePath]
    FROM [dbo].[Article]
    WHERE [IDArticle] = @ArticleID;

    IF @@ROWCOUNT = 0 
	RETURN 1;           

    BEGIN TRANSACTION;
        DELETE FROM [dbo].[ArticleAuthor]   WHERE [ArticleID] = @ArticleID;  
        DELETE FROM [dbo].[ArticleCategory] WHERE [ArticleID] = @ArticleID;  
        DELETE FROM [dbo].[Article]         WHERE [IDArticle] = @ArticleID;  
    COMMIT TRANSACTION;

    RETURN 0;
END;
GO

