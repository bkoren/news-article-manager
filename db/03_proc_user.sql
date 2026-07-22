 /*   
   algorithm : PBKDF2WithHmacSHA256
   iterations: 120000
   salt      : 16 random bytes, Base64 text     (24 chars)
   hash      : 32-byte derived key, Base64 text (44 chars)
*/

USE NewsAppDb;
GO

CREATE OR ALTER PROCEDURE [dbo].[p_User_Register]
    @Username     NVARCHAR(50),
    @PasswordHash NVARCHAR(255),
    @PasswordSalt NVARCHAR(100),
    @NewId        INT OUTPUT                       
AS
BEGIN
    SET NOCOUNT ON;

    IF EXISTS (SELECT 1 FROM [dbo].[User] WHERE [Username] = @Username)
    RETURN 2;                                  
    
    INSERT INTO [dbo].[User] ([Username], [PasswordHash], [PasswordSalt], [Role])
    VALUES (@Username, @PasswordHash, @PasswordSalt, N'USER');

    SET @NewId = SCOPE_IDENTITY();

    RETURN 0;                                      
END;
GO

CREATE OR ALTER PROCEDURE [dbo].[p_Admin_Register]
    @Username     NVARCHAR(50),
    @PasswordHash NVARCHAR(255),
    @PasswordSalt NVARCHAR(100)
AS
BEGIN    
    SET NOCOUNT ON;

    IF EXISTS (SELECT 1 FROM [dbo].[User] WHERE [Username] = @Username)
    RETURN 1;                                  
   
    INSERT INTO [dbo].[User] ([Username], [PasswordHash], [PasswordSalt], [Role])
    VALUES (@Username, @PasswordHash, @PasswordSalt, N'ADMIN');

    RETURN 0;                                      
END;
GO

CREATE OR ALTER PROCEDURE [dbo].[p_User_GetByUsername]
    @Username NVARCHAR(50)
AS
BEGIN
    SET NOCOUNT ON;

    SELECT [IDUser],
           [Username],
           [PasswordHash],
           [PasswordSalt],
           [Role]
    FROM 
		[dbo].[User]
    WHERE 
		[Username] = @Username;

    IF @@ROWCOUNT = 0 
	RETURN 1;                    

    RETURN 0;                                     
END;
GO

EXEC [dbo].[p_Admin_Register]
    @Username     = N'admin',
    @PasswordHash = N'Ydu+ZOnUa7L8+7ovo/d1PmRZmwnfX9nY4P75YEBkXqI=', 
    @PasswordSalt = N'v1SZ6Ur4a9qX/cvC+Y4HSQ=='; 
	
	--Password: Admin123!
	