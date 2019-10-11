package com.codegym.controller;

import com.codegym.model.Note;
import com.codegym.model.NoteType;
import com.codegym.service.NoteService;
import com.codegym.service.NoteTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

@Controller
public class NoteController {
    @Autowired
    private NoteService noteService;

    @Autowired
    private NoteTypeService noteTypeService;

    @ModelAttribute("noteTypes")
    public Iterable<NoteType> noteTypeList() {
        return noteTypeService.findAll();
    }

    @GetMapping("/notes")
    public ModelAndView noteList(@PageableDefault(value = 3) Pageable pageable) {
        ModelAndView modelAndView = new ModelAndView("/note/list");
        Page<Note> notes = noteService.findAll(pageable);
        List<Integer> notePages = noteService.getNumberPage(notes);
        modelAndView.addObject("notes", notes);
        modelAndView.addObject("notePages", notePages);
        return modelAndView;
    }

    @GetMapping("/note/create")
    public ModelAndView createNoteForm() {
        ModelAndView modelAndView = new ModelAndView("/note/create");
        modelAndView.addObject("note", new Note());
        return modelAndView;
    }

    @PostMapping("/note/create")
    public ModelAndView saveNote(@ModelAttribute Note note) {
        noteService.save(note);
        ModelAndView modelAndView = new ModelAndView("redirect:/notes");
        return modelAndView;
    }

    @GetMapping("/note/edit/{id}")
    public ModelAndView editNoteForm(@PathVariable("id") long id) {
        Note note = noteService.findById(id);
        ModelAndView modelAndView = new ModelAndView("/note/edit");
        modelAndView.addObject("note", note);
        return modelAndView;
    }

    @PostMapping("/note/edit")
    public ModelAndView editNote(@ModelAttribute("note") Note note) {
        noteService.save(note);
        ModelAndView modelAndView = new ModelAndView("redirect:/notes");
        return modelAndView;
    }

    @GetMapping("/note/delete/{id}")
    public ModelAndView deleteNoteForm(@PathVariable long id) {
        Note note = noteService.findById(id);
        ModelAndView modelAndView = new ModelAndView("/note/delete", "note", note);
        return modelAndView;
    }

    @PostMapping("/note/delete")
    public ModelAndView deleteNote(@ModelAttribute Note note) {
        noteService.delete(note.getId());
        ModelAndView modelAndView = new ModelAndView("redirect:/notes");
        return modelAndView;
    }

    @GetMapping("/note/view/{id}")
    public ModelAndView viewNote(@PathVariable long id) {
        Note note = noteService.findById(id);
        ModelAndView modelAndView = new ModelAndView("/note/view", "note", note);
        return modelAndView;
    }
}
