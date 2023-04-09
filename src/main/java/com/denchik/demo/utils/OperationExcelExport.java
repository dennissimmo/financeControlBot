package com.denchik.demo.utils;

import com.denchik.demo.model.Category;
import com.denchik.demo.model.Operation;
import com.denchik.demo.model.User;
import com.denchik.demo.service.OperationService;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
public class OperationExcelExport {
    private static final SimpleDateFormat formatData = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private static final Integer OPERATION_LIMIT = 500;
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private List<XSSFSheet> sheetsOfCategories;
    private List<Category> distinctCategories;
    private OperationService operationService;
    private User user;
    public OperationExcelExport () {

    }
    public OperationExcelExport(List<Operation> operationList, List<Category> categories, OperationService operationService, User user) {
        // store passed instances to local ones
        this.distinctCategories = categories;
        this.operationService = operationService;
        this.user = user;
        List<String> sheetNames = new ArrayList<>();
        String firstSheetName = "Історія транзакцій";
        // Create list of sheet names
        sheetNames.add(firstSheetName);
        for (Category category : distinctCategories) {
            String categoryName = category.getName();
            sheetNames.add(categoryName);
            System.out.println(categoryName);
        }
        //log.info("Count distinctCategories = {}",sheetNames.size());
        // Create a Workbook
        workbook = new XSSFWorkbook();
        // Create a separate Sheet for each sheetName
        for(String sheetName : sheetNames) {
            workbook.createSheet(sheetName);
        }
        //log.info("Coun created sheets = {}", workbook.getNumberOfSheets());
        sheetsOfCategories = new ArrayList<>();
        // Store Sheet of each Category in a list
        for (int sheetID = 0; sheetID < sheetNames.size(); sheetID++) {
            sheetsOfCategories.add(workbook.getSheetAt(sheetID));
        }
    }
    public void writeHeaderRow() {
        // Create header for each Sheet of specific Category
        XSSFFont font = workbook.createFont();
        font.setFontHeightInPoints((short)12);
        font.setFontName("Calibri");
        font.setColor(IndexedColors.BLACK.getIndex());
        font.setBold(true);
        font.setItalic(false);
        for (int i = 0; i < sheetsOfCategories.size(); i++) {
            Row headerRow = sheetsOfCategories.get(i).createRow(0);
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
    public void writeCellsDataRows() {
        // Define styles of Cells
        CellStyle income = workbook.createCellStyle();
        income.setFillForegroundColor(IndexedColors.BRIGHT_GREEN1.getIndex());
        //System.out.println("Index of LIGHT_GREEN = " + IndexedColors.BRIGHT_GREEN1.getIndex());
        income.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        CellStyle expense = workbook.createCellStyle();
        expense.setFillForegroundColor(IndexedColors.RED1.getIndex());
        //System.out.println("Index of LIGHT_GREEN = " + IndexedColors.BRIGHT_GREEN1.getIndex());
        expense.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        List<Operation> allUserOperations = operationService.getUserOperationsOrdered(user)
                .stream().limit(OPERATION_LIMIT).collect(Collectors.toList());
        //log.info("Count user operations = {}",allUserOperations.size());
        int startRow = 1;
        int indexOperationSheet = 0;
        // Iterate over all operations and create Row and Cells for each record
        for (int i = 0; i < allUserOperations.size(); i++) {
            Operation currentOperation = allUserOperations.get(allUserOperations.size() - 1 - i);
            XSSFRow operationRow = sheetsOfCategories.get(indexOperationSheet).createRow(startRow);
            operationRow.createCell(0).setCellValue(startRow);
            operationRow.createCell(1).setCellValue(formatData.format(currentOperation.getCreateAt()));
            // Style income and loss in different colour
            String operationAmountWithSign = currentOperation.addSignForOperation(currentOperation);
            Cell amount = operationRow.createCell(2);
            if (operationAmountWithSign.contains("+")) {
                amount.setCellStyle(income);
            } else {
                amount.setCellStyle(expense);
            }
            amount.setCellValue(operationAmountWithSign);
            operationRow.createCell(3).setCellValue(currentOperation.getCategory().getName());
            operationRow.createCell(4).setCellValue(currentOperation.getNote());
            operationRow.createCell(5).setCellValue(currentOperation.getCurrentBalance() + " ₴");
            for (int columnNumber = 0; columnNumber < 7; columnNumber++) {
                sheetsOfCategories.get(indexOperationSheet).autoSizeColumn(columnNumber);
            }
            startRow++;
        }

        // Iterate over each sheet of specific category
        for (int sheetIndex = 1; sheetIndex < sheetsOfCategories.size(); sheetIndex++) {
            int startRowIndex = 1;
            List<Operation> operationsByCategory = operationService.findOperationByCategoryAndUser(distinctCategories.get(sheetIndex - 1), user)
                    .stream().limit(OPERATION_LIMIT).collect(Collectors.toList());
            for (int columnNumber = 0; columnNumber < 6; columnNumber++) {
                sheetsOfCategories.get(sheetIndex).autoSizeColumn(columnNumber);
            }
            for (int j = 0; j < operationsByCategory.size() ; j++) {
                Operation currentOperation = operationsByCategory.get(operationsByCategory.size() - 1 - j);
                XSSFRow operationRow = sheetsOfCategories.get(sheetIndex).createRow(startRowIndex);
                operationRow.createCell(0).setCellValue(startRowIndex);
                operationRow.createCell(1).setCellValue(formatData.format(currentOperation.getCreateAt()));
                operationRow.createCell(2).setCellValue(currentOperation.addSignForOperation(currentOperation));
                operationRow.createCell(3).setCellValue(currentOperation.getCategory().getName());
                operationRow.createCell(4).setCellValue(currentOperation.getNote());
                startRowIndex++;
            }
        }
    }
    public ByteArrayOutputStream export() {
        // Create header
        writeHeaderRow();
        // Create Rows, Cells and fill them with operations data
        writeCellsDataRows();

        ByteArrayOutputStream outputStream = null;
        try {
            outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return outputStream;
    }

}
