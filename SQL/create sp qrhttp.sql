USE [ONIDO]
GO
/****** Object:  StoredProcedure [dbo].[QRhttpRequest]    Script Date: 13/10/2020 12:43:45 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
ALTER PROCEDURE [dbo].[QRhttpRequest]
@Body AS VARCHAR(8000)
 AS
BEGIN
DECLARE @Object AS INT;
DECLARE @ResponseText AS VARCHAR(8000);
DECLARE @node AS VARCHAR(8000);

DECLARE  @posTmp  TABLE (
	[guild]		VARCHAR(20),
	[unit]		VARCHAR(20), 
	[label]		VARCHAR(200), 
	[dateTim]	datetime,
	[username]	varchar(200),
	[stateRYG]	varchar(200),
	[state]		int
) 


--DECLARE @Body AS VARCHAR(8000) =
--'{
--    "what": 1,
--    "ever": "you",
--    "need": "to send as the body"
--}'  

EXEC sp_OACreate 'MSXML2.XMLHTTP', @Object OUT;
EXEC sp_OAMethod @Object, 'open', NULL, 'post','https://us-central1-otdo-96e7c.cloudfunctions.net/addMessage', 'false'

EXEC sp_OAMethod @Object, 'setRequestHeader', null, 'Content-Type', 'application/json'
--EXEC sp_OAMethod @Object, 'send', null, null
EXEC sp_OAMethod @Object, 'send', null, @body

EXEC sp_OAMethod @Object, 'responseText', @ResponseText OUTPUT
PRINT 'Response text: ' + @responseText;
EXEC sp_OADestroy @Object

insert into @posTmp
SELECT *
FROM OPENJSON (@responseText)
 WITH (
	guild VARCHAR(20),
	unit VARCHAR(20),
    label VARCHAR(200),
	dateTim datetime)
CROSS APPLY OPENJSON (@responseText)
  WITH (
    username varchar(200)	'$.username',
	stateRYG varchar(200)	 '$.stateRYG',
	state int				'$.state'

	)
WHERE guild IS NOT NULL AND username IS NOT NULL 

select * from @posTmp
insert into ONIDO.dbo.QRSQL select * from @posTmp

END;
 
