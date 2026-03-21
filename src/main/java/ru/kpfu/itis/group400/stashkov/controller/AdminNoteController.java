package ru.kpfu.itis.group400.stashkov.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.kpfu.itis.group400.stashkov.model.Note;
import ru.kpfu.itis.group400.stashkov.service.CustomUserDetails;
import ru.kpfu.itis.group400.stashkov.service.NoteService;

import java.util.List;

@RestController
@RequestMapping("/admin/notes")
public class AdminNoteController {

    private final NoteService noteService;

    public AdminNoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping
    public List<Note> getAllNotes(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return noteService.getAllNotes(userDetails.getUser());
    }

    @DeleteMapping("/{id}")
    public void deleteNote(@AuthenticationPrincipal CustomUserDetails userDetails,
                           @PathVariable Long id) {
        noteService.deleteNoteByIdAdmin(id,  userDetails.getUser());
    }
}