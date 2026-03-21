<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Мои заметки</title>
</head>
<body>
<h2>Мои заметки</h2>
<a href="/notes/create">Создать новую заметку</a>
<br><br>
<#if notes?size == 0>
    <p>У вас пока нет заметок.</p>
<#else>
    <table border="1">
        <tr>
            <th>Название</th>
            <th>Содержание</th>
            <th>Дата создания</th>
            <th>Публичная</th>
            <th>Действия</th>
        </tr>
        <#list notes as note>
            <tr>
                <td>${note.title}</td>
                <td>${note.content?html}</td>
                <td>${note.createdAt}</td>
                <td>${note.public?string("Да", "Нет")}</td>
                <td>
                    <a href="/notes/${note.id}/edit">Редактировать</a>
                    <form action="/notes/${note.id}/delete" method="post" style="display:inline;">
                        <button type="submit" onclick="return confirm('Удалить заметку?')">Удалить</button>
                    </form>
                </td>
            </tr>
        </#list>
    </table>
</#if>
<br>
<a href="/">На главную</a>
</body>
</html>