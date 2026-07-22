USE NewsAppDb
GO	

CREATE OR ALTER PROC [dbo].[p_DeleteAllData]
AS
BEGIN   
    SET NOCOUNT ON;
	
	BEGIN TRAN;        
        
    BEGIN TRY                 
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
        
		COMMIT TRAN;
    END TRY

    BEGIN CATCH    
		ROLLBACK TRAN;

        THROW;
    END CATCH
END;
GO
