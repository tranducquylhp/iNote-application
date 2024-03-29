package com.codegym.service;

import com.codegym.model.Note;
import com.codegym.model.NoteType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public interface NoteService {
    Page<Note> findAll(Pageable pageable);

    void save(Note note);

    void delete(Long noteId);

    List<Integer> getNumberPage(Page<Note> notes);

    Note findById(long id);

    Iterable<Note> findAll();

    Page<Note> findAllByTitleContaining(String title, Pageable pageable);

    Page<Note> findAllByNoteType(NoteType noteType, Pageable pageable);

    File exportExcel() throws IOException;

    void saveNoteInFileTxt();

    void writeJSON();

    void importExcel() throws IOException;

    void importJSON();
}
