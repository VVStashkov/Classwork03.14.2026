package ru.kpfu.itis.group400.stashkov.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.kpfu.itis.group400.stashkov.model.Note;
import ru.kpfu.itis.group400.stashkov.model.Role;
import ru.kpfu.itis.group400.stashkov.model.User;
import ru.kpfu.itis.group400.stashkov.service.CustomUserDetails;
import ru.kpfu.itis.group400.stashkov.service.NoteService;

import java.time.OffsetDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminNoteController.class)
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NoteService noteService;

    @Test
    public void getAllNotesTest() throws Exception {
        User user = new User();
        user.setUsername("admin");
        Role role = new Role();
        role.setName("ADMIN");
        user.setRoles(List.of(role));
        CustomUserDetails customUserDetails = new CustomUserDetails(user);
        Note note = new Note(1l, "title", "content", OffsetDateTime.now(), true, user);
        given(noteService.getAllNotes(user)).willReturn(List.of(note));

        mockMvc.perform(get("/admin/notes")
                .with(user(customUserDetails)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].title").value("title"))
                .andExpect(jsonPath("$[0].content").value("content"))
                .andExpect(jsonPath("$[0].public").value(true));

    }

    @Test
    public void deleteNoteTest() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("admin");
        Role role = new Role();
        role.setName("ADMIN");
        user.setRoles(List.of(role));
        CustomUserDetails customUserDetails = new CustomUserDetails(user);
        Note note = new Note(1l, "title", "content", OffsetDateTime.now(), true, user);

        mockMvc.perform(delete("/admin/notes/1")
                        .with(user(customUserDetails))
                .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk());
    }
}
