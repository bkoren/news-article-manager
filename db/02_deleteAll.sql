USE NewsAppDb
GO	

CREATE PROCEDURE [dbo].[DeleteAllData]
AS
BEGIN   
    SET NOCOUNT ON;
	
    BEGIN TRY                 
        BEGIN TRANSACTION;
            DELETE FROM [dbo].[ArticleAuthor];    
            DELETE FROM [dbo].[ArticleCategory];   
            DELETE FROM [dbo].[Article];                       
			DELETE FROM [dbo].[Author];
            DELETE FROM [dbo].[Category];
            DELETE FROM [dbo].[Source];
			
            DBCC CHECKIDENT ('[dbo].[Article]',  RESEED, 0);
            DBCC CHECKIDENT ('[dbo].[Author]',   RESEED, 0);
            DBCC CHECKIDENT ('[dbo].[Category]', RESEED, 0);
            DBCC CHECKIDENT ('[dbo].[Source]',   RESEED, 0);           
        COMMIT TRANSACTION;
    END TRY

    BEGIN CATCH    
		ROLLBACK TRANSACTION;
        THROW;
    END CATCH
END;
GO