package com.denchik.demo.utils;

import com.denchik.demo.model.Category;
import com.denchik.demo.model.Operation;
import com.denchik.demo.model.User;
import com.denchik.demo.service.OperationService;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
@Log4j2
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
        String firstSheetName = "Історія транзакцій";
        sheetNames.add(firstSheetName);
        for (Category category : distinctCategories) {
            sheetNames.add(category.getName());
        }
        //log.info("Count distinctCategories = {}",sheetNames.size());
        for (String sheet : sheetNames) {
            System.out.println(sheet);
        }
        workbook = new XSSFWorkbook();
        for(String sheetName : sheetNames) {
            workbook.createSheet(sheetName);
        }
        //log.info("Coun created sheets = {}", workbook.getNumberOfSheets());
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
            Cell currentBalance = headerRow.createCell(5);
            currentBalance.setCellValue("Balance amount");
            currentBalance.setCellStyle(headerCellStyle);
        }
    }
    public void writeCellsDataRows () {
        CellStyle income = workbook.createCellStyle();
        income.setFillForegroundColor(IndexedColors.BRIGHT_GREEN1.getIndex());
        //System.out.println("Index of LIGHT_GREEN = " + IndexedColors.BRIGHT_GREEN1.getIndex());
        income.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        CellStyle expense = workbook.createCellStyle();
        expense.setFillForegroundColor(IndexedColors.RED1.getIndex());
        //System.out.println("Index of LIGHT_GREEN = " + IndexedColors.BRIGHT_GREEN1.getIndex());
        expense.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        List<Operation> allUserOperations = operationService.getUserOperationsOrdered(user);
        //log.info("Count user operations = {}",allUserOperations.size());
        int startRow = 1;
        int indexOperationSheet = 0;
        for (int i = 0; i < allUserOperations.size(); i++) {
            sheetsCategory.get(indexOperationSheet).createRow(startRow).createCell(0).setCellValue(startRow);
            sheetsCategory.get(indexOperationSheet).getRow(startRow).createCell(1).setCellValue(formatData.format(allUserOperations.get(i).getCreateAt()));
            String operationAmountWithSign = allUserOperations.get(i).addSignForOperation(allUserOperations.get(i));
            if (operationAmountWithSign.contains("+")) {
                Cell amount = sheetsCategory.get(indexOperationSheet).getRow(startRow).createCell(2);
                amount.setCellStyle(income);
                amount.setCellValue(operationAmountWithSign);
            } else {
                Cell amount = sheetsCategory.get(indexOperationSheet).getRow(startRow).createCell(2);
                amount.setCellStyle(expense);
                amount.setCellValue(operationAmountWithSign);
            }
            sheetsCategory.get(indexOperationSheet).getRow(startRow).createCell(3).setCellValue(allUserOperations.get(i).getCategory().getName());
            sheetsCategory.get(indexOperationSheet).getRow(startRow).createCell(4).setCellValue(allUserOperations.get(i).getNote());
            sheetsCategory.get(indexOperationSheet).getRow(startRow).createCell(5).setCellValue(allUserOperations.get(i).getCurrentBalance() + " ₴");
            for (int columnNumber = 0; columnNumber < 7; columnNumber++) {
                sheetsCategory.get(indexOperationSheet).autoSizeColumn(columnNumber);
            }
            startRow++;
        }
        for (int sheetIndex = 1; sheetIndex < sheetsCategory.size(); sheetIndex++) {
            int startRowIndex = 1;
            List<Operation> operationByCategory = operationService.findOperationByCategoryAndUser(distinctCategories.get(sheetIndex - 1),user);
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
