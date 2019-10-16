package com.codegym.controller;

import com.codegym.model.Note;
import com.codegym.model.NoteType;
import com.codegym.service.NoteService;
import com.codegym.service.NoteTypeService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

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
    public ModelAndView noteList(@RequestParam("s") Optional<String> s, @PageableDefault(value = 3) Pageable pageable,
                                 HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView("/note/list");
        Page<Note> notes;
        if(s.isPresent()){
            notes = noteService.findAllByTitleContaining(s.get(), pageable);
            modelAndView.addObject("titleSearch", s.get());
        } else {
            notes = noteService.findAll(pageable);
        }
        List<Integer> notePages = noteService.getNumberPage(notes);
        modelAndView.addObject("notes", notes);
        modelAndView.addObject("notePages", notePages);

        if (request.getParameter("message")!= null){
            modelAndView.addObject("message",request.getParameter("message"));
        }
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
        modelAndView.addObject("message","Create successful");
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
        modelAndView.addObject("message","Edit successful");
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
        modelAndView.addObject("message", "Delete successfull");
        return modelAndView;
    }

    @GetMapping("/note/view/{id}")
    public ModelAndView viewNote(@PathVariable long id) {
        Note note = noteService.findById(id);
        ModelAndView modelAndView = new ModelAndView("/note/view", "note", note);
        return modelAndView;
    }



    @GetMapping("note/excel")
    public ModelAndView exportNoteExcel() throws IOException {
        File file = noteService.exportExcel();
        ModelAndView modelAndView = new ModelAndView("/note/export", "message","Export done. File: "+  file.getAbsolutePath() );
        return modelAndView;
    }

    @GetMapping("/note/readTxt")
    public ModelAndView readTxt(){
        noteService.saveNoteInFileTxt();
        ModelAndView modelAndView = new ModelAndView("redirect:/notes","message","Inserts Successful");
        return modelAndView;
    }

    @GetMapping("/note/noteType")
    public ModelAndView searchByNoteType(@RequestParam("noteTypeId") String noteTypeId, @PageableDefault(value = 2) Pageable pageable){
        ModelAndView modelAndView = new ModelAndView("/note/noteType");

        long id = Long.parseLong(noteTypeId);
        NoteType noteType = noteTypeService.findById(id);
        Page<Note> notes = noteService.findAllByNoteType(noteType, pageable);
        modelAndView.addObject("noteType", noteType);

        modelAndView.addObject("notes",notes);

        List<Integer> notePages = noteService.getNumberPage(notes);
        modelAndView.addObject("notePages", notePages);
        return modelAndView;
    }

    @GetMapping("/note/writeJSON")
    public ModelAndView writeJSON(){

        noteService.writeJSON();
        ModelAndView modelAndView = new ModelAndView("redirect:/notes");
        modelAndView.addObject("message","Export successful");
        return modelAndView;
    }

    @GetMapping("/note/importJSON")
    public ModelAndView importJSON(){
        noteService.importJSON();
        ModelAndView modelAndView = new ModelAndView("redirect:/notes");
        modelAndView.addObject("message","Import successful");
        return modelAndView;
    }

    @GetMapping("/note/importExcel")
    public ModelAndView importExcel() throws IOException {
        noteService.importExcel();

        ModelAndView modelAndView = new ModelAndView("redirect:/notes");
        modelAndView.addObject("message","Import successful");
        return modelAndView;
    }
}
