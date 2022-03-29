/****** Script for SelectTopNRows command from SSMS  ******/
DECLARE @Body AS NVARCHAR(MAX) ='{
    "what": "send",
    "num": "1604916480"	
}'

-- 1604916480
-- 1604913906

exec dbo.QRhttpRequest @Body