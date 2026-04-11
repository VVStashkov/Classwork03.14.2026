package ru.kpfu.itis.group400.stashkov.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.kpfu.itis.group400.stashkov.model.Note;
import ru.kpfu.itis.group400.stashkov.model.Role;
import ru.kpfu.itis.group400.stashkov.model.User;
import ru.kpfu.itis.group400.stashkov.repository.NoteRepository;
import ru.kpfu.itis.group400.stashkov.repository.RoleRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NoteServiceTest {

    @Mock
    private NoteRepository noteRepository;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private NoteService noteService;

    private User createUser(Long id, String username, String roleName) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        Role role = new Role();
        role.setName(roleName);
        user.setRoles(List.of(role));
        return user;
    }

    private Note createNote(Long id, String title, User author, boolean isPublic) {
        Note note = new Note();
        note.setId(id);
        note.setTitle(title);
        note.setAuthor(author);
        note.setCreatedAt(OffsetDateTime.now());
        note.setPublic(isPublic);
        return note;
    }

    @Test
    void createNote_shouldSaveAndReturnNote() {
        User author = createUser(1L, "author", "ROLE_USER");
        Note savedNote = createNote(10L, "New note", author, true);
        given(noteRepository.save(any(Note.class))).willReturn(savedNote);

        Note result = noteService.createNote("New note", "Content", true, author);

        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getTitle()).isEqualTo("New note");
        assertThat(result.getAuthor()).isEqualTo(author);
        assertThat(result.isPublic()).isTrue();
        verify(noteRepository).save(any(Note.class));
    }

    @Test
    void updateNote_whenAuthorMatches_shouldUpdateAndSave() {
        User author = createUser(1L, "author", "ROLE_USER");
        Note existingNote = createNote(5L, "Old title", author, false);
        given(noteRepository.findById(5L)).willReturn(Optional.of(existingNote));
        given(noteRepository.save(any(Note.class))).willAnswer(inv -> inv.getArgument(0));

        Note updated = noteService.updateNote(5L, "New title", "New content", true, author);

        assertThat(updated.getTitle()).isEqualTo("New title");
        assertThat(updated.getContent()).isEqualTo("New content");
        assertThat(updated.isPublic()).isTrue();
        verify(noteRepository).save(existingNote);
    }

    @Test
    void updateNote_whenAuthorMismatch_shouldThrowSecurityException() {
        User author = createUser(1L, "author", "ROLE_USER");
        User other = createUser(2L, "other", "ROLE_USER");
        Note existingNote = createNote(5L, "Title", author, false);
        given(noteRepository.findById(5L)).willReturn(Optional.of(existingNote));

        assertThatThrownBy(() -> noteService.updateNote(5L, "New", "New", false, other))
                .isInstanceOf(SecurityException.class)
                .hasMessageContaining("not allowed to update");
    }

    @Test
    void deleteNote_whenAuthorMatches_shouldDelete() {
        User author = createUser(1L, "author", "ROLE_USER");
        Note note = createNote(5L, "To delete", author, false);
        given(noteRepository.findById(5L)).willReturn(Optional.of(note));

        noteService.deleteNote(5L, author);

        verify(noteRepository).delete(note);
    }

    @Test
    void deleteNote_whenAuthorMismatch_shouldThrowSecurityException() {
        User author = createUser(1L, "author", "ROLE_USER");
        User other = createUser(2L, "other", "ROLE_USER");
        Note note = createNote(5L, "To delete", author, false);
        given(noteRepository.findById(5L)).willReturn(Optional.of(note));

        assertThatThrownBy(() -> noteService.deleteNote(5L, other))
                .isInstanceOf(SecurityException.class);
    }

    @Test
    void getNotesByPublic_shouldReturnPublicNotes() {
        User author = createUser(1L, "author", "ROLE_USER");
        Note publicNote = createNote(1L, "Public", author, true);
        given(noteRepository.findByIsPublicTrue()).willReturn(List.of(publicNote));

        List<Note> notes = noteService.getNotesByPublic();

        assertThat(notes).hasSize(1);
        verify(noteRepository).findByIsPublicTrue();
    }

    @Test
    void getNotesByUser_shouldReturnUserNotes() {
        User author = createUser(1L, "author", "ROLE_USER");
        Note note = createNote(1L, "User note", author, false);
        given(noteRepository.findByAuthor(author)).willReturn(List.of(note));

        List<Note> notes = noteService.getNotesByUser(author);

        assertThat(notes).hasSize(1);
    }

    @Test
    void getNoteByIdAndUser_whenOwner_shouldReturnNote() {
        User author = createUser(1L, "author", "ROLE_USER");
        Note note = createNote(1L, "Note", author, false);
        given(noteRepository.findById(1L)).willReturn(Optional.of(note));

        Optional<Note> result = noteService.getNoteByIdAndUser(1L, author);

        assertThat(result).isPresent().contains(note);
    }

    @Test
    void getNoteByIdAndUser_whenNotOwner_shouldReturnEmpty() {
        User author = createUser(1L, "author", "ROLE_USER");
        User other = createUser(2L, "other", "ROLE_USER");
        Note note = createNote(1L, "Note", author, false);
        given(noteRepository.findById(1L)).willReturn(Optional.of(note));

        Optional<Note> result = noteService.getNoteByIdAndUser(1L, other);

        assertThat(result).isEmpty();
    }

    @Test
    void getAllNotes_whenUserIsAdmin_shouldReturnAllNotes() {
        User admin = createUser(1L, "admin", "ROLE_ADMIN");
        Role adminRole = new Role();
        adminRole.setName("ROLE_ADMIN");
        given(roleRepository.findByName("ROLE_ADMIN")).willReturn(Optional.of(adminRole));
        List<Note> allNotes = List.of(createNote(1L, "Note1", admin, true));
        given(noteRepository.findAll()).willReturn(allNotes);

        List<Note> result = noteService.getAllNotes(admin);

        assertThat(result).isEqualTo(allNotes);
    }

    @Test
    void getAllNotes_whenUserNotAdmin_shouldThrowSecurityException() {
        User user = createUser(1L, "user", "ROLE_USER");
        Role adminRole = new Role();
        adminRole.setName("ROLE_ADMIN");
        given(roleRepository.findByName("ROLE_ADMIN")).willReturn(Optional.of(adminRole));

        assertThatThrownBy(() -> noteService.getAllNotes(user))
                .isInstanceOf(SecurityException.class);
    }

    @Test
    void deleteNoteByIdAdmin_whenUserIsAdmin_shouldDelete() {
        User admin = createUser(1L, "admin", "ROLE_ADMIN");
        Role adminRole = new Role();
        adminRole.setName("ROLE_ADMIN");
        given(roleRepository.findByName("ROLE_ADMIN")).willReturn(Optional.of(adminRole));

        noteService.deleteNoteByIdAdmin(100L, admin);

        verify(noteRepository).deleteById(100L);
    }

    @Test
    void deleteNoteByIdAdmin_whenUserNotAdmin_shouldThrowSecurityException() {
        User user = createUser(1L, "user", "ROLE_USER");
        Role adminRole = new Role();
        adminRole.setName("ROLE_ADMIN");
        given(roleRepository.findByName("ROLE_ADMIN")).willReturn(Optional.of(adminRole));

        assertThatThrownBy(() -> noteService.deleteNoteByIdAdmin(100L, user))
                .isInstanceOf(SecurityException.class);
    }
}