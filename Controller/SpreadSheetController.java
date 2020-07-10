/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author Sandy
 */
public class SpreadSheetController {
    public SpreadSheetController(){
        
    }
    public SpreadSheetController(String text ){
        
    }
    public void createAllSpreadsheet(String excelFilePath, ArrayList<String> data, String fileName){
         XSSFWorkbook workbook = new XSSFWorkbook(); 
         XSSFSheet sheet = workbook.createSheet("All Data");
        //Set<String> keyset = data.keySet();
        int rownum = 0;
        for(int i = 0; i < data.size();i++)//for (String key : keyset)
        {
            String IPP = data.get(i);//keyset.iterator().next();
            //System.out.println("In Excel" + IPP);
            Row row = sheet.createRow(rownum++);
            //ArrayList objArr = data.get(IPP);
            int cellnum = 0;
            Cell cell = row.createCell(cellnum++);
            cell.setCellValue(IPP);
            /*
            for (Object obj : objArr)
            {
               Cell cell = row.createCell(cellnum++);
               if(obj instanceof String)
                    cell.setCellValue(key);
                else if(obj instanceof Double)
                    cell.setCellValue((Double)obj);
            }
            */
        }
        try
        {
            //Write the workbook in file system
            FileOutputStream out = new FileOutputStream(new File(excelFilePath));
            workbook.write(out);
            out.close();
            System.out.println(fileName + " written successfully on disk.");
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
    }
    
    
     public void writeExcel( String excelFilePath, ArrayList<String> IPP, ArrayList<ArrayList<double[]>> PValueCell, ArrayList<ArrayList<double[]>> FCValueCell, ArrayList<ArrayList<Integer>> agreeCell) throws IOException {
                //Blank workbook
                
        XSSFWorkbook workbook = new XSSFWorkbook(); 
         
        //Create a blank sheet
        XSSFSheet sheet = workbook.createSheet("ANC data");
          
        //This data needs to be written (Object[])
        HashMap<Integer, ArrayList<String>> data = new HashMap<Integer, ArrayList<String>>();
        ArrayList<String> title = new ArrayList<String>(); //("IPP","P-Value", "P-Value", "P-Value", "P-Value","Fold Change","Fold Change","Fold Change","Fold Change");
       
        title.add("IPP");
        for(int i = 0; i < IPP.size(); i ++){
            ArrayList<String> IP = new ArrayList<String>();
            IP.add(IPP.get(i));
            data.put(i+1,IP);
        }
        
        for(int i = 0; i < PValueCell.size(); i ++){
            title.add("Pvalue");
            for(int j = 0; j <PValueCell.get(i).size(); j++){
                for(int k = 0; k < PValueCell.get(i).get(j).length; k++ ){
                  
                    Integer key = k+1;
                    
                     ArrayList<String> IP = data.get(key);
                    IP.add(PValueCell.get(i).get(j)[k] + "");

                   data.put(key,IP);
                }
        
            }
        }
        
        for(int i = 0; i < FCValueCell.size(); i ++){
            title.add("Fold Change");
            for(int j = 0; j <FCValueCell.get(i).size(); j++){
                for(int k = 0; k < FCValueCell.get(i).get(j).length; k++ ){
                    Integer key = k+1;
                     ArrayList<String> IP = data.get(key);
                    IP.add(FCValueCell.get(i).get(j)[k] + "");

                   data.put(key,IP);
                }
        
            }
        }
        
        int pointer1 = 0;
        int pointer2 = 0;
        int index1 = 0;
        int index2 = 0;
        
        for(int k = 0; k < FCValueCell.get(0).get(0).length; k++ ){
            Integer key = k+1;
            ArrayList<String> IP = data.get(key);
            
            
                    
            if(pointer1 < agreeCell.get(0).size()){
                index1 = agreeCell.get(0).get(pointer1);
            }else{
                index1 = -1;
            }
            if(pointer2 < agreeCell.get(1).size()){
                index2 = agreeCell.get(1).get(pointer2);
            }else{
                index2 = -1;
            }
          
            if(k == index1){
                IP.add("1");   
                pointer1++;
            }else{
                IP.add("0");
            }

                       
            if(k == index2){
                IP.add("1");
                pointer2++;
            }else{
                IP.add("0");
             }
  
        }
        
                  
                        
        data.put(0, title);

        //Iterate over data and write to sheet
        Set<Integer> keyset = data.keySet();
        int rownum = 0;
        for (int key : keyset)
        {
            Row row = sheet.createRow(rownum++);
            ArrayList<String> objArr = data.get(key);
            int cellnum = 0;
            for (String obj : objArr)
            {
               Cell cell = row.createCell(cellnum++);
                    cell.setCellValue((String)obj);
               
            }
        }
        try
        {
            //Write the workbook in file system
            FileOutputStream out = new FileOutputStream(new File(excelFilePath));
            workbook.write(out);
            out.close();
            System.out.println("ALL written successfully on disk.");
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
    }
     
      public void writeHitsExcel( String excelFilePath, ArrayList<String> IPP, ArrayList<ArrayList<double[]>> PValueCell, ArrayList<ArrayList<double[]>> FCValueCell, ArrayList<ArrayList<Integer>> agreeCell) throws IOException {
                //Blank workbook
                
        XSSFWorkbook workbook = new XSSFWorkbook(); 
         
        //Create a blank sheet
        XSSFSheet sheet = workbook.createSheet("Hits Unioned");
          
        //This data needs to be written (Object[])
        HashMap<Integer, ArrayList<String>> data = new HashMap<Integer, ArrayList<String>>();
        ArrayList<String> title = new ArrayList<String>(); //("IPP","P-Value", "P-Value", "P-Value", "P-Value","Fold Change","Fold Change","Fold Change","Fold Change");
       
        int numUnions = 1;
        
        title.add("IPP");
        Integer pointerIndex = 1;
        //Integer previousIndex = 1;
        ArrayList<String> header = new ArrayList<String>();
        for(int num = 0; num <agreeCell.size()-1; num ++){
            int hitsout = agreeCell.size() - num;
            
            //if(num > 0){
            //    header.add("union");
            //}
            header.add(hitsout+"of"+agreeCell.size());
            data.put(pointerIndex,header);
            pointerIndex++;
            
            
            for(int ind = 0; ind < agreeCell.get(num).size(); ind++){
                ArrayList<String> IP = new ArrayList<String>();
                int key = agreeCell.get(num).get(ind);
                IP.add(IPP.get(key));
                for(int i= 0; i < PValueCell.size();i++){
                    IP.add(PValueCell.get(i).get(0)[key] + "");
                }
                for(int k = 0; k < FCValueCell.size(); k++ ){
                        IP.add(FCValueCell.get(k).get(0)[key] + "");
                }
                IP.add(numUnions+ "");
                data.put(pointerIndex, IP);
                pointerIndex++;
            }
            
            numUnions++;
        }
       
        Set<Integer> keyset = data.keySet();
        int rownum = 0;
        for (int key : keyset)
        {
            Row row = sheet.createRow(rownum++);
            ArrayList<String> objArr = data.get(key);
            int cellnum = 0;
            for (String obj : objArr)
            {
               Cell cell = row.createCell(cellnum++);
                    cell.setCellValue((String)obj);
 
            }
        }
        try
        {
            //Write the workbook in file system
            FileOutputStream out = new FileOutputStream(new File(excelFilePath));
            workbook.write(out);
            out.close();
            System.out.println("Selected.xlsx written successfully on disk.");
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
    }
    
}
