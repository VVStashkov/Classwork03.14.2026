package ru.kpfu.itis.group400.stashkov.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.kpfu.itis.group400.stashkov.model.Note;
import ru.kpfu.itis.group400.stashkov.model.Role;
import ru.kpfu.itis.group400.stashkov.model.User;
import ru.kpfu.itis.group400.stashkov.service.CustomUserDetails;
import ru.kpfu.itis.group400.stashkov.service.NoteService;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.instanceOf;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NoteController.class)
class NoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NoteService noteService;

    private User createUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        Role role = new Role();
        role.setName("ROLE_USER");
        user.setRoles(List.of(role));
        return user;
    }

    private CustomUserDetails createUserDetails() {
        return new CustomUserDetails(createUser());
    }

    private Note createFullNote(Long id, String title, String content, boolean isPublic, User author) {
        Note note = new Note();
        note.setId(id);
        note.setTitle(title);
        note.setContent(content);
        note.setCreatedAt(OffsetDateTime.now());
        note.setPublic(isPublic);
        note.setAuthor(author);
        return note;
    }

    @Test
    void listMyNotes_shouldReturnNotesViewWithNotes() throws Exception {
        User user = createUser();
        Note note = createFullNote(1L, "My note", "My content", false, user);
        given(noteService.getNotesByUser(any(User.class))).willReturn(List.of(note));

        mockMvc.perform(get("/notes")
                        .with(user(createUserDetails())))
                .andExpect(status().isOk())
                .andExpect(view().name("notes"))
                .andExpect(model().attributeExists("notes"))
                .andExpect(model().attribute("notes", List.of(note)));
    }

    @Test
    void listPublicNotes_shouldReturnPublicNotesView() throws Exception {
        User user = createUser();
        Note publicNote = createFullNote(2L, "Public note", "Public content", true, user);
        given(noteService.getNotesByPublic()).willReturn(List.of(publicNote));

        mockMvc.perform(get("/notes/public")
                        .with(user(createUserDetails())))
                .andExpect(status().isOk())
                .andExpect(view().name("public_notes"))
                .andExpect(model().attributeExists("notes"))
                .andExpect(model().attribute("notes", List.of(publicNote)));
    }

    @Test
    void showCreateForm_shouldReturnNoteFormWithEmptyNote() throws Exception {
        mockMvc.perform(get("/notes/create")
                        .with(user(createUserDetails())))
                .andExpect(status().isOk())
                .andExpect(view().name("note_form"))
                .andExpect(model().attributeExists("note"))
                .andExpect(model().attribute("note", instanceOf(Note.class)));
    }

    @Test
    void createNote_shouldCallServiceAndRedirectToNotes() throws Exception {
        mockMvc.perform(post("/notes/create")
                        .with(user(createUserDetails()))
                        .with(csrf())
                        .param("title", "New title")
                        .param("content", "New content")
                        .param("isPublic", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/notes"));

        verify(noteService).createNote(eq("New title"), eq("New content"), eq(true), any(User.class));
    }

    @Test
    void showEditForm_whenNoteExists_shouldReturnFormWithNote() throws Exception {
        User user = createUser();
        Note note = createFullNote(10L, "Editable note", "Editable content", false, user);
        given(noteService.getNoteByIdAndUser(eq(10L), any(User.class))).willReturn(Optional.of(note));

        mockMvc.perform(get("/notes/10/edit")
                        .with(user(createUserDetails())))
                .andExpect(status().isOk())
                .andExpect(view().name("note_form"))
                .andExpect(model().attributeExists("note"))
                .andExpect(model().attribute("note", note));
    }

    @Test
    void updateNote_shouldCallServiceAndRedirect() throws Exception {
        mockMvc.perform(post("/notes/5/edit")
                        .with(user(createUserDetails()))
                        .with(csrf())
                        .param("title", "Updated title")
                        .param("content", "Updated content")
                        .param("isPublic", "false"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/notes"));

        verify(noteService).updateNote(eq(5L), eq("Updated title"), eq("Updated content"), eq(false), any(User.class));
    }

    @Test
    void deleteNote_shouldCallServiceAndRedirect() throws Exception {
        mockMvc.perform(post("/notes/7/delete")
                        .with(user(createUserDetails()))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/notes"));

        verify(noteService).deleteNote(eq(7L), any(User.class));
    }
}