package com.codegym.service.impl;

import com.codegym.model.Note;
import com.codegym.model.NoteType;
import com.codegym.repository.NoteRepository;
import com.codegym.repository.NoteTypeRepository;
import com.codegym.service.NoteService;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class NoteServiceImpl implements NoteService {
    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private NoteTypeRepository noteTypeRepository;

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
        for (int i = 0; i < notePage; i++) {
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
    public Page<Note> findAllByTitleContaining(String title, Pageable pageable) {
        return noteRepository.findAllByTitleContaining(title, pageable);
    }

    @Override
    public Page<Note> findAllByNoteType(NoteType noteType, Pageable pageable) {
        return noteRepository.findAllByNoteType(noteType, pageable);
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
        File file = new File("C:/demo/Note Export.xls");
        file.getParentFile().mkdirs();

        FileOutputStream outFile = new FileOutputStream(file);
        workbook.write(outFile);
        System.out.println("Created file: " + file.getAbsolutePath());
        return file;
    }

    //Read txt
    @Override
    public void saveNoteInFileTxt() {
        BufferedReader br = null;

        try {
            br = new BufferedReader(new FileReader("C:\\demo\\Note Import.txt"));

            String textInALine = br.readLine();

            while ((textInALine = br.readLine()) != null) {
                System.out.println(textInALine);
                String[] noteString = textInALine.split(";");
                Note note = new Note();
                note.setTitle(noteString[0]);
                note.setContent(noteString[1]);
                long idNoteType = Long.parseLong(noteString[2]);
                note.setNoteType(noteTypeRepository.findOne(idNoteType));
                noteRepository.save(note);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //export JSON
    @Override
    public void writeJSON() {
        JSONArray noteListJSON = new JSONArray();
        List<Note> notes = (List<Note>) noteRepository.findAll();
        for (int i=0; i<notes.size(); i++){

            JSONObject noteDetail = new JSONObject();

            JSONObject noteObject = new JSONObject();
            Note note = notes.get(i);
            noteDetail.put("id", note.getId());
            noteDetail.put("title", note.getTitle());
            noteDetail.put("content", note.getContent());
            noteDetail.put("noteTypeId", note.getNoteType().getId());

            noteObject.put("note", noteDetail);

            noteListJSON.add(noteObject);
        }
        //Write JSON file
        try (FileWriter file = new FileWriter("C:/demo/Note Export.json")) {

            file.write(noteListJSON.toJSONString());
            file.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void importJSON() {
        //JSON parser object to parse read file
        JSONParser jsonParser = new JSONParser();

        try (FileReader reader = new FileReader("C:/demo/Note Import.json"))
        {
            //Read JSON file
            Object obj = jsonParser.parse(reader);

            JSONArray noteListJSON = (JSONArray) obj;
            System.out.println(noteListJSON);

            //Iterate over employee array
            for (int i=0; i<noteListJSON.size(); i++) {
                JSONObject noteObject = (JSONObject) noteListJSON.get(i);
                Note note = parseNoteObject(noteObject);
                noteRepository.save(note);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Note parseNoteObject(JSONObject note) {
        JSONObject noteObject = (JSONObject) note.get("note");

        String title = (String) noteObject.get("title");

        String content = (String) noteObject.get("content");

        Long noteTypeId = (Long) noteObject.get("noteTypeId");
        return new Note(title,content,noteTypeRepository.findOne(noteTypeId));
    }

    //import Excel
    @Override
    public void importExcel() throws IOException {
        // Get file
        String excelFilePath = "C:/demo/Note Import.xlsx";
        InputStream inputStream = new FileInputStream(new File(excelFilePath));

        // Get workbook
        Workbook workbook = getWorkbook(inputStream, excelFilePath);

        // Get sheet
        Sheet sheet = workbook.getSheetAt(0);

        // Get all rows
        Iterator<Row> iterator = sheet.iterator();
        while (iterator.hasNext()) {
            Row nextRow = iterator.next();
            if (nextRow.getRowNum() == 0) {
                // Ignore header
                continue;
            }

            // Get all cells
            Iterator<Cell> cellIterator = nextRow.cellIterator();

            // Read cells and set value for book object
            Note note = new Note();
            while (cellIterator.hasNext()) {
                //Read cell
                Cell cell = cellIterator.next();
                Object cellValue = getCellValue(cell);
                if (cellValue == null || cellValue.toString().isEmpty()) {
                    continue;
                }
                // Set value for book object
                int columnIndex = cell.getColumnIndex();
                switch (columnIndex) {
                    case 0:
                        note.setTitle((String) getCellValue(cell));
                        break;
                    case 1:
                        note.setContent((String) getCellValue(cell));
                        break;
                    case 2:
                        long id = new BigDecimal((double) cellValue).intValue();
                        note.setNoteType(noteTypeRepository.findOne(id));
                        break;
                    default:
                        break;
                }

            }
            noteRepository.save(note);
        }

        workbook.close();
        inputStream.close();
    }

    // Get Workbook
    private static Workbook getWorkbook(InputStream inputStream, String excelFilePath) throws IOException {
        Workbook workbook = null;
        if (excelFilePath.endsWith("xlsx")) {
            workbook = new XSSFWorkbook(inputStream);
        } else if (excelFilePath.endsWith("xls")) {
            workbook = new HSSFWorkbook(inputStream);
        } else {
            throw new IllegalArgumentException("The specified file is not Excel file");
        }

        return workbook;
    }

    // Get cell value
    private static Object getCellValue(Cell cell) {
        CellType cellType = cell.getCellTypeEnum();
        Object cellValue = null;
        switch (cellType) {
            case BOOLEAN:
                cellValue = cell.getBooleanCellValue();
                break;
            case FORMULA:
                Workbook workbook = cell.getSheet().getWorkbook();
                FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
                cellValue = evaluator.evaluate(cell).getNumberValue();
                break;
            case NUMERIC:
                cellValue = cell.getNumericCellValue();
                break;
            case STRING:
                cellValue = cell.getStringCellValue();
                break;
            case _NONE:
            case BLANK:
            case ERROR:
                break;
            default:
                break;
        }

        return cellValue;
    }
}
