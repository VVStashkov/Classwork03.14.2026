package ru.kpfu.itis.group400.stashkov.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.kpfu.itis.group400.stashkov.model.Note;
import ru.kpfu.itis.group400.stashkov.model.User;

import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Long> {

    @Query("SELECT n FROM Note n WHERE n.author = :author")
    List<Note> findByAuthor(@Param("author") User author);

    @Query("SELECT n FROM Note n WHERE n.isPublic = true ORDER BY n.createdAt DESC")
    List<Note> findByIsPublicTrue();

}
