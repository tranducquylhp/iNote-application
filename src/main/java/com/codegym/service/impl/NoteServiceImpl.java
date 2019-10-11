package com.codegym.service.impl;

import com.codegym.model.Note;
import com.codegym.repository.NoteRepository;
import com.codegym.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

public class NoteServiceImpl implements NoteService {
    @Autowired
    private NoteRepository noteRepository;
    @Override
    public Page<Note> findAll(Pageable pageable) {
        return noteRepository.findAll(pageable);
    }

    @Override
    public void save(Note note) {
        noteRepository.save(note);
    }

    @Override
    public void delete(Long noteId) {
        noteRepository.delete(noteId);
    }

    @Override
    public List<Integer> getNumberPage(Page<Note> notes) {
        int notePage = notes.getTotalPages();
        List<Integer> notePages = new ArrayList<>();
        for (int i=0; i<notePage; i++) {
            notePages.add(i);
        }
        return notePages;
    }

    @Override
    public Note findById(long id) {
        return noteRepository.findOne(id);
    }

    @Override
    public Iterable<Note> findAll() {
        return noteRepository.findAll();
    }
}
