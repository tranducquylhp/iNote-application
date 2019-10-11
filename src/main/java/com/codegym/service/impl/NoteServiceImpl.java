package com.codegym.service.impl;

import com.codegym.model.Note;
import com.codegym.repository.NoteRepository;
import com.codegym.service.NoteService;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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

    @Override
    public Page<Note> findAllByTitle(String title, Pageable pageable) {
        return noteRepository.findAllByTitle(title, pageable);
    }

    //export excel

    private static HSSFCellStyle createStyleForTitle(HSSFWorkbook workbook) {
        HSSFFont font = workbook.createFont();
        font.setBold(true);
        HSSFCellStyle style = workbook.createCellStyle();
        style.setFont(font);
        return style;
    }

    @Override
    public File exportExcel() throws IOException {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("Notes sheet");

        List<Note> notes = (List<Note>) noteRepository.findAll();

        int rownum = 0;
        Cell cell;
        Row row;
        //
        HSSFCellStyle style = createStyleForTitle(workbook);

        row = sheet.createRow(rownum);

        // Id
        cell = row.createCell(0, CellType.STRING);
        cell.setCellValue("Id");
        cell.setCellStyle(style);
        // Title
        cell = row.createCell(1, CellType.STRING);
        cell.setCellValue("Title");
        cell.setCellStyle(style);
        // Content
        cell = row.createCell(2, CellType.STRING);
        cell.setCellValue("Content");
        cell.setCellStyle(style);
        // Note type
        cell = row.createCell(3, CellType.STRING);
        cell.setCellValue("Note Type");
        cell.setCellStyle(style);


        // Data
        for (Note note : notes) {
            rownum++;
            row = sheet.createRow(rownum);

            // Id
            cell = row.createCell(0, CellType.NUMERIC);
            cell.setCellValue(note.getId());
            // Title
            cell = row.createCell(1, CellType.STRING);
            cell.setCellValue(note.getTitle());
            // Content
            cell = row.createCell(2, CellType.STRING);
            cell.setCellValue(note.getContent());
            // Note type
            cell = row.createCell(3, CellType.STRING);
            cell.setCellValue(note.getNoteType().getName());
        }
        File file = new File("C:/demo/Note.xls");
        file.getParentFile().mkdirs();

        FileOutputStream outFile = new FileOutputStream(file);
        workbook.write(outFile);
        System.out.println("Created file: " + file.getAbsolutePath());
        return file;
    }
}
