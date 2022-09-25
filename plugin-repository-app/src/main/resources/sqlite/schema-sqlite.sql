--
-- Table structure for table plugin
--
-- DROP TABLE IF EXISTS [plugin];
CREATE TABLE [plugin] (
  [plugin_id] VARCHAR(32)  NULL PRIMARY KEY,
  [name] VARCHAR(45)  NOT NULL,
  [build] DATE  NULL,
  [version] VARCHAR(45)  NOT NULL,
  [description] VARCHAR(255)  NULL,
  [compatible_version] VARCHAR(45)  NULL,
  [os] VARCHAR(45)  NULL,
  [arch] VARCHAR(45)  NULL,
  [dependency] VARCHAR(255)  NULL,
  [category_id] VARCHAR(32)  NULL,
  [published] TINYINT(1) DEFAULT '0' NULL,
  [deleted] TINYINT(1) DEFAULT '0' NULL
)

--
-- Table structure for table category
--
-- DROP TABLE IF EXISTS [category];
CREATE TABLE [category] (
  [category_id] VARCHAR(32)  NULL PRIMARY KEY,
  [name] VARCHAR(45)  NOT NULL,
  [parent_id] VARCHAR(32)  NULL
)

--
-- Table structure for table user
--
-- DROP TABLE IF EXISTS [user];
CREATE TABLE [user] (
[user_id] VARCHAR(32)  NULL PRIMARY KEY,
[username] VARCHAR(45)  UNIQUE NULL,
[password_hash] VARCHAR(64)  NULL,
[salt] VARCHAR(32)  NULL,
[role] VARCHAR(45)  NULL
)