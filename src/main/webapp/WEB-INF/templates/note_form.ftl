<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>${note.id?has_content?then("Редактирование", "Создание")} заметки</title>
</head>
<body>
<h2>${note.id?has_content?then("Редактирование", "Создание")} заметки</h2>
<form action="${note.id?has_content?then('/notes/' + note.id + '/edit', '/notes/create')}" method="post">
    <label>Название:</label>
    <input type="text" name="title" value="${(note.title)!''}" required>
    <br>
    <label>Содержание:</label><br>
    <textarea name="content" rows="5" cols="40" required>${(note.content)!''}</textarea>
    <br>
    <label>Публичная:</label>
    <input type="checkbox" name="isPublic" value="true" ${note.public?then('checked', '')}>
    <br>
    <button type="submit">Сохранить</button>
</form>
<br>
<a href="/notes">Отмена</a>
</body>
</html>