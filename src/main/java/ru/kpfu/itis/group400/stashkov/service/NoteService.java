package ru.kpfu.itis.group400.stashkov.service;

import org.springframework.stereotype.Service;
import ru.kpfu.itis.group400.stashkov.model.Note;
import ru.kpfu.itis.group400.stashkov.model.Role;
import ru.kpfu.itis.group400.stashkov.model.User;
import ru.kpfu.itis.group400.stashkov.repository.NoteRepository;
import ru.kpfu.itis.group400.stashkov.repository.RoleRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class NoteService {

    private final NoteRepository noteRepository;
    private final RoleRepository roleRepository;

    public NoteService(NoteRepository noteRepository, RoleRepository roleRepository) {
        this.noteRepository = noteRepository;
        this.roleRepository = roleRepository;
    }

    public Note createNote(String title, String content, boolean isPublic, User author) {
        Note note = new Note();
        note.setTitle(title);
        note.setContent(content);
        note.setAuthor(author);
        note.setPublic(isPublic);
        note.setCreatedAt(OffsetDateTime.now());
        return noteRepository.save(note);
    }

    public Note updateNote(Long id, String title, String content, boolean isPublic, User author) {
        Note note = noteRepository.findById(id).orElseThrow(() -> new RuntimeException("Note not found"));
        if (!note.getAuthor().getId().equals(author.getId())) {
            throw new SecurityException("You are not allowed to update this note");
        }
        note.setTitle(title);
        note.setContent(content);
        note.setPublic(isPublic);
        return noteRepository.save(note);
    }

    public void deleteNote(Long id, User author) {
        Note note = noteRepository.findById(id).orElseThrow(() -> new RuntimeException("Note not found"));
        if (!note.getAuthor().getId().equals(author.getId())) {
            throw new SecurityException("You are not allowed to delete this note");
        }
        noteRepository.delete(note);
    }

    public List<Note> getNotesByPublic() {
        return noteRepository.findByIsPublicTrue();
    }

    public List<Note> getNotesByUser(User author) {
        return noteRepository.findByAuthor(author);
    }

    public Optional<Note> getNoteByIdAndUser(Long id, User user) {
        Optional<Note> note = noteRepository.findById(id);
        if (note.isPresent() && note.get().getAuthor().getId().equals(user.getId())) {
            return note;
        }
        return Optional.empty();
    }

    public List<Note> getAllNotes(User user) {
        String role = roleRepository.findByName("ROLE_ADMIN").orElseThrow(() ->
                new RuntimeException("unexisting role")).getName();
        if (user.getRoles().stream()
                .map(r -> r.getName()).toList().contains(role)) {
            return noteRepository.findAll();
        } else {
            throw new SecurityException("You are not allowed to get all notes note");
        }
    }

    public void deleteNoteByIdAdmin(Long id, User user) {
        String role = roleRepository.findByName("ROLE_ADMIN").orElseThrow(() ->
                new RuntimeException("unexisting role")).getName();
        if (user.getRoles().stream()
                .map(r -> r.getName()).toList().contains(role)) {
            noteRepository.deleteById(id);
        } else {
            throw new SecurityException("You are not allowed to delete this note");
        }

    }

}
