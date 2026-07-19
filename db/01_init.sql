CREATE TABLE `Source` (
  `SourceId` int PRIMARY KEY AUTO_INCREMENT,
  `Name` nvarchar(100) NOT NULL,
  `FeedUrl` nvarchar(400) UNIQUE NOT NULL COMMENT 'RSS feed address',
  `SiteUrl` nvarchar(400)
);

CREATE TABLE `Author` (
  `AuthorId` int PRIMARY KEY AUTO_INCREMENT,
  `FullName` nvarchar(150) UNIQUE NOT NULL,
  `Email` nvarchar(200)
);

CREATE TABLE `Category` (
  `CategoryId` int PRIMARY KEY AUTO_INCREMENT,
  `Name` nvarchar(100) UNIQUE NOT NULL
);

CREATE TABLE `Article` (
  `ArticleId` int PRIMARY KEY AUTO_INCREMENT,
  `SourceId` int NOT NULL,
  `Title` nvarchar(300) NOT NULL,
  `Summary` nvarchar(max),
  `Link` nvarchar(500) NOT NULL,
  `PublishedAt` datetime2,
  `ImagePath` nvarchar(300),
  `CreatedAt` datetime2 NOT NULL DEFAULT (SYSUTCDATETIME())
);

CREATE TABLE `ArticleAuthor` (
  `ArticleId` int NOT NULL,
  `AuthorId` int NOT NULL,
  PRIMARY KEY (`ArticleId`, `AuthorId`)
);

CREATE TABLE `ArticleCategory` (
  `ArticleId` int NOT NULL,
  `CategoryId` int NOT NULL,
  PRIMARY KEY (`ArticleId`, `CategoryId`)
);

CREATE TABLE `AppUser` (
  `UserId` int PRIMARY KEY AUTO_INCREMENT,
  `Username` nvarchar(50) UNIQUE NOT NULL,
  `PasswordHash` nvarchar(255) NOT NULL,
  `PasswordSalt` nvarchar(100) NOT NULL,
  `Role` nvarchar(10) NOT NULL COMMENT 'CHECK (Role IN (''ADMIN'',''USER''))',
  `CreatedAt` datetime2 NOT NULL DEFAULT (SYSUTCDATETIME())
);

CREATE INDEX `Article_index_0` ON `Article` (`SourceId`);

CREATE INDEX `Article_index_1` ON `Article` (`PublishedAt`);

CREATE INDEX `ArticleAuthor_index_2` ON `ArticleAuthor` (`AuthorId`);

CREATE INDEX `ArticleCategory_index_3` ON `ArticleCategory` (`CategoryId`);

ALTER TABLE `Source` COMMENT = 'One row per RSS feed the parser reads from';

ALTER TABLE `Author` COMMENT = 'Feeds give the author as one free-text string';

ALTER TABLE `Category` COMMENT = 'The category tags coming out of the feed';

ALTER TABLE `Article` COMMENT = 'The main entity (the "Film" of the spec)';

ALTER TABLE `ArticleAuthor` COMMENT = 'Many-to-many bridge: Article to Author, cascade on both sides';

ALTER TABLE `ArticleCategory` COMMENT = 'Many-to-many bridge: Article to Category, cascade on both sides';

ALTER TABLE `AppUser` COMMENT = 'Login accounts. Stands alone, not linked to articles';

ALTER TABLE `Article` ADD FOREIGN KEY (`SourceId`) REFERENCES `Source` (`SourceId`);

ALTER TABLE `ArticleAuthor` ADD FOREIGN KEY (`ArticleId`) REFERENCES `Article` (`ArticleId`);

ALTER TABLE `ArticleAuthor` ADD FOREIGN KEY (`AuthorId`) REFERENCES `Author` (`AuthorId`);

ALTER TABLE `ArticleCategory` ADD FOREIGN KEY (`ArticleId`) REFERENCES `Article` (`ArticleId`);

ALTER TABLE `ArticleCategory` ADD FOREIGN KEY (`CategoryId`) REFERENCES `Category` (`CategoryId`);
