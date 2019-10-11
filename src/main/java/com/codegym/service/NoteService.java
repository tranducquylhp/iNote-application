package com.codegym.service;

import com.codegym.model.Note;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NoteService {
    Page<Note> findAll(Pageable pageable);
    void save(Note note);
    void delete(Long noteId);
    List<Integer> getNumberPage(Page<Note> notes);
    Note findById(long id);
    Iterable<Note> findAll();
}