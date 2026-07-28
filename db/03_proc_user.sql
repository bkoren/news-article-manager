 /*   
   algorithm : PBKDF2WithHmacSHA256
   iterations: 120000
   salt      : 16 random bytes, Base64 text     (24 chars)
   hash      : 32-byte derived key, Base64 text (44 chars)
*/

USE NewsAppDb;
GO

CREATE OR ALTER PROC [dbo].[p_User_Register]
	@Username NVARCHAR(50),
	@PasswordHash NVARCHAR(255)
AS
BEGIN
	SET NOCOUNT ON;

	INSERT INTO [dbo].[User]
		([Username], [PasswordHash])
	VALUES
		(@Username, @PasswordHash);

END;
GO

CREATE OR ALTER PROC [dbo].[p_User_Exists]
	@Username NVARCHAR(50)
AS
BEGIN
	SET NOCOUNT ON;
		
	IF EXISTS(SELECT TOP 1 [IDUser] FROM [User] WHERE [Username] = @Username)
	RETURN 1;

	RETURN 0;
END;
GO

CREATE OR ALTER PROCEDURE [dbo].[p_Admin_Register]
    @Username     NVARCHAR(50),
    @PasswordHash NVARCHAR(255)
AS
BEGIN    
    SET NOCOUNT ON;                                 
   
    INSERT INTO [dbo].[User] 
		([Username], [PasswordHash], [Role])
    VALUES 
		(@Username, @PasswordHash, N'ADMIN');                                     
END;
GO

CREATE OR ALTER PROC [dbo].[p_User_GetByUsername]
	@Username NVARCHAR(50)
AS
BEGIN
	SET NOCOUNT ON;

	SELECT
		[IDUser],
		[Username],
		[PasswordHash],
		[Role]
	FROM 
		[dbo].[User]
	WHERE
		[Username] = @Username;

END;
GO

EXEC [dbo].[p_Admin_Register]
    @Username     = N'admin',
    @PasswordHash = N'OFTe5dHfyIpOKu2xP63MCQ==$SPzPDpssUhSisbnVwbapjmJvmG6cNlNhNhtZwvkTtAU='
	
	--Password: Admin123!
	