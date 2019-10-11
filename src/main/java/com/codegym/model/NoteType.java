package com.codegym.model;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "noteType")
public class NoteType {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String description;

    @OneToMany(targetEntity = Note.class)
    private List<Note> noteList;

    public NoteType() {
    }

    public NoteType(Long id, String name, String description, List<Note> noteList) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.noteList = noteList;
    }

    public NoteType(String name, String description, List<Note> noteList) {
        this.name = name;
        this.description = description;
        this.noteList = noteList;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Note> getNoteList() {
        return noteList;
    }

    public void setNoteList(List<Note> noteList) {
        this.noteList = noteList;
    }
}
