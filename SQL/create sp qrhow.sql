USE [ONIDO]
GO
/****** Object:  StoredProcedure [dbo].[QRHow]    Script Date: 13/10/2020 1:46:12 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
ALTER PROCEDURE [dbo].[QRHow] AS
BEGIN
DECLARE @Object AS INT;
DECLARE @ResponseText AS VARCHAR(8000);
DECLARE @node AS VARCHAR(8000);

DECLARE  @posTmp  TABLE (
	[num]		int
) 


DECLARE @Body AS VARCHAR(8000) ='{
    "what": "how"
}'  

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
	num int
)

SELECT num FROM @posTmp 
EXCEPT SELECT num FROM dbo.QRlist OUTPUT


END;
 