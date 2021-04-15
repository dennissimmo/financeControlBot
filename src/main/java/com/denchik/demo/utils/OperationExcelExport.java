package com.denchik.demo.utils;

import com.denchik.demo.model.Category;
import com.denchik.demo.model.Operation;
import com.denchik.demo.model.User;
import com.denchik.demo.service.OperationService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class OperationExcelExport {
    private static final SimpleDateFormat formatData = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private List<XSSFSheet> sheetsCategory;
    private List<Operation> operationList;
    private List<Category> distinctCategories;
    private OperationService operationService;
    private User user;
    public OperationExcelExport () {

    }
    public OperationExcelExport(List<Operation> operationList, List<Category> categories, OperationService operationService, User user) {
        this.operationList = operationList;
        this.distinctCategories = categories;
        this.operationService = operationService;
        this.user = user;
        List<String> sheetNames = new ArrayList<>();
        for (Category category : distinctCategories) {
            sheetNames.add(category.getName());
        }
        workbook = new XSSFWorkbook();
        for(String sheetName : sheetNames) {
            workbook.createSheet(sheetName);
        }
        sheetsCategory = new ArrayList<>();
        for (int sheetID = 0; sheetID < sheetNames.size(); sheetID++) {
            sheetsCategory.add(workbook.getSheetAt(sheetID));
        }
    }
    public void writeHeaderRow () {
        XSSFFont font = workbook.createFont();
        font.setFontHeightInPoints((short)12);
        font.setFontName("Calibri");
        font.setColor(IndexedColors.BLACK.getIndex());
        font.setBold(true);
        font.setItalic(false);
        for (int i = 0; i < sheetsCategory.size(); i++) {
            Row headerRow = sheetsCategory.get(i).createRow(0);
            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            //System.out.println("Index of LIGHT_GREEN = " + IndexedColors.BRIGHT_GREEN1.getIndex());
            headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            //headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerCellStyle.setFont(font);
           /* Cell id = headerRow.createCell(0);
            id.setCellValue("ID");
            id.setCellStyle(headerCellStyle);*/
            Cell number = headerRow.createCell(0);
            number.setCellValue("№ ");
            number.setCellStyle(headerCellStyle);
            Cell date = headerRow.createCell(1);
            date.setCellValue("Date");
            date.setCellStyle(headerCellStyle);
            Cell sum = headerRow.createCell(2);
            sum.setCellValue("Sum");
            sum.setCellStyle(headerCellStyle);
            Cell category = headerRow.createCell(3);
            category.setCellValue("Category");
            category.setCellStyle(headerCellStyle);
            Cell note = headerRow.createCell(4);
            note.setCellValue("Note");
            note.setCellStyle(headerCellStyle);
        }
    }
    public void writeCellsDataRows () {
        for (int sheetIndex = 0; sheetIndex < sheetsCategory.size(); sheetIndex++) {
            int startRowIndex = 1;
            List<Operation> operationByCategory = operationService.findOperationByCategoryAndUser(distinctCategories.get(sheetIndex),user);
            for (int j = 0; j < operationByCategory.size() ; j++) {
                sheetsCategory.get(sheetIndex).createRow(startRowIndex).createCell(0).setCellValue(startRowIndex);
                sheetsCategory.get(sheetIndex).getRow(startRowIndex).createCell(1).setCellValue(formatData.format(operationByCategory.get(j).getCreateAt()));
                sheetsCategory.get(sheetIndex).getRow(startRowIndex).createCell(2).setCellValue(operationByCategory.get(j).addSignForOperation(operationByCategory.get(j)));
                sheetsCategory.get(sheetIndex).getRow(startRowIndex).createCell(3).setCellValue(operationByCategory.get(j).getCategory().getName());
                sheetsCategory.get(sheetIndex).getRow(startRowIndex).createCell(4).setCellValue(operationByCategory.get(j).getNote());
                for (int columnNumber = 0; columnNumber < 6; columnNumber++) {
                    sheetsCategory.get(sheetIndex).autoSizeColumn(columnNumber);
                }
                startRowIndex++;
            }
        }

    }
    public ByteArrayInputStream export () {
        writeHeaderRow();
        writeCellsDataRows();
      ByteArrayOutputStream outputStream = null;
        try {
          outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            workbook.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ByteArrayInputStream(outputStream.toByteArray());
    }

}
