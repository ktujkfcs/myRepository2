/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mytest;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Iterator;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author And
 */ 

public class testJUnitIntegerEN {
    
  
@Test
    public void testJUnitIntegerRU() throws Exception {
        
        try{
        convert.text.NumberFormat w= new convert.text.NumberFormat("en");
        w.loadDirectory();
        InputStream in = new FileInputStream((getClass().getResource("/datatest/DataExel_INTEGER_EN.xls").getPath()));
        HSSFWorkbook wb = new HSSFWorkbook(in);
        Sheet sheet = wb.getSheetAt(0);
        Iterator<Row> it = sheet.iterator();
        String inString = "", inNumber = "";
        while (it.hasNext()) {
            Row row = it.next();
            Iterator<Cell> cells = row.iterator();
            Cell cell = cells.next();
            int cellType = cell.getCellType();
            inNumber = (cell.getStringCellValue());
            inNumber = inNumber.substring(1, inNumber.length() - 1);
            cells.hasNext();
            cell = cells.next();
            inString = cell.getStringCellValue();
            System.out.println(inNumber + "\notvet: " + inString);
            String s=inNumber;
            String result = w.format(inNumber);
            System.out.println("result " + result);
            assertEquals("Ошибка в числе: " + inNumber, inString, result);
        }
         } catch (NullPointerException e) {
            throw new RuntimeException("использование пустой ссылки, возможно файл был перемещен\n" + e.toString());
        }
    }
}
