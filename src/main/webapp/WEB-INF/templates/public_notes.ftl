<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Публичные заметки</title>
</head>
<body>
<h2>Публичные заметки</h2>
<#if notes?size == 0>
    <p>Нет публичных заметок.</p>
<#else>
    <#list notes as note>
        <div style="border:1px solid #ccc; margin-bottom:10px; padding:10px;">
            <h3>${note.title}</h3>
            <p>${note.content?html}</p>
            <small>Автор: ${note.author.username} | Дата: ${note.createdAt}</small>
        </div>
    </#list>
</#if>
<br>
<a href="/">На главную</a>
</body>
</html>