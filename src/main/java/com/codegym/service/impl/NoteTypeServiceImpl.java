package com.codegym.service.impl;

import com.codegym.model.NoteType;
import com.codegym.repository.NoteRepository;
import com.codegym.repository.NoteTypeRepository;
import com.codegym.service.NoteTypeService;
import org.springframework.beans.factory.annotation.Autowired;

public class NoteTypeServiceImpl implements NoteTypeService {
    @Autowired
    private NoteTypeRepository noteTypeRepository;
    @Override
    public Iterable<NoteType> findAll() {
        return noteTypeRepository.findAll();
    }

    @Override
    public NoteType findById(long id) {
        return noteTypeRepository.findOne(id);
    }
}
