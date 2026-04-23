package ru.kpfu.itis.group400.stashkov.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.kpfu.itis.group400.stashkov.aop.BenchMark;
import ru.kpfu.itis.group400.stashkov.aop.Count;
import ru.kpfu.itis.group400.stashkov.model.Note;
import ru.kpfu.itis.group400.stashkov.service.CustomUserDetails;
import ru.kpfu.itis.group400.stashkov.service.NoteService;

@Controller
@RequestMapping("/notes")
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @Count
    @BenchMark
    @GetMapping
    public String listMyNotes(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        model.addAttribute("notes", noteService.getNotesByUser(userDetails.getUser()));
        return "notes";
    }

    @Count
    @BenchMark
    @GetMapping("/public")
    public String listPublicNotes(Model model) {
        model.addAttribute("notes", noteService.getNotesByPublic());
        return "public_notes";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("note", new Note());
        return "note_form";
    }

    @PostMapping("/create")
    public String createNote(@AuthenticationPrincipal CustomUserDetails userDetails,
                             @RequestParam("title") String title,
                             @RequestParam("content") String content,
                             @RequestParam(value="isPublic", required = false, defaultValue = "false")
                                 boolean isPublic) {
        noteService.createNote(title, content, isPublic, userDetails.getUser());
        return "redirect:/notes";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@AuthenticationPrincipal CustomUserDetails userDetails,
                               Model model,
                               @PathVariable("id") Long id) {
        Note note = noteService.getNoteByIdAndUser(id, userDetails.getUser()).orElseThrow(() ->
                new RuntimeException("Note id not found!"));
        model.addAttribute("note", note);
        return "note_form";
    }

    @PostMapping("/{id}/edit")
    public String updateNote(@AuthenticationPrincipal CustomUserDetails userDetails,
                             @PathVariable("id") Long id,
                             @RequestParam("title") String title,
                             @RequestParam("content") String content,
                             @RequestParam(value="isPublic", required = false, defaultValue = "false")
                                 boolean isPublic) {
        noteService.updateNote(id, title, content, isPublic, userDetails.getUser());
        return "redirect:/notes";
    }

    @PostMapping("/{id}/delete")
    public String deleteNote(@AuthenticationPrincipal CustomUserDetails userDetails,
                             @PathVariable("id") Long id) {
        noteService.deleteNote(id, userDetails.getUser());
        return "redirect:/notes";
    }

}
