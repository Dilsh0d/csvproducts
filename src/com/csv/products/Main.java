package com.csv.products;

import com.csv.products.exception.DirectoryPathNotFolderException;
import com.csv.products.exception.DirectoryPathNotFoundException;
import com.csv.products.exception.JavaArgNotFountDirectoryPathException;
import com.csv.products.model.FindCheapestProductModel;
import com.csv.products.task.ReadProductCsvFileTask;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author dilsh0d
 */
public class Main {

    public static String COMMA_DELIMITER = ";";


    public static void main(String[] args) {
        if(args == null || args.length==0) {
            throw new JavaArgNotFountDirectoryPathException("Please enter in arg directory path where csv files place!");
        }
        String pathDirectoryCsv = args[0];
        File directory = new File(pathDirectoryCsv);

        if(!directory.exists()) {
            throw new DirectoryPathNotFoundException("Please check to exists in arg directory path where csv files place!");
        }

        if(!directory.isDirectory()){
            throw new DirectoryPathNotFolderException("Please check your enter path is directory!");
        }

        if(args.length>1) {
            COMMA_DELIMITER = args[1];// COMMA_DELIMITER for example ("," or ";" or ":")
        }

        Main cheapestProduct = new Main();
        cheapestProduct.execute(directory);
        cheapestProduct.writeCheapestProduct(directory);
    }

    private void execute(File directory) {
        String[] fileNames =  directory.list();
        CountDownLatch downLatch = new CountDownLatch(fileNames.length);
        ExecutorService executorService = Executors.newFixedThreadPool(8);
        for(String csvFile:directory.list()) {
            executorService.execute(new ReadProductCsvFileTask(directory.getAbsolutePath()+File.separator+csvFile, downLatch));
        }
        try {
            downLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        executorService.shutdown();
    }

    private void writeCheapestProduct(File directory){
        try {
            FileWriter writer = new FileWriter(directory.getAbsolutePath()+File.separator+ "cheapest1000products.csv");
            FindCheapestProductModel.cheapestProductList.forEach(product -> {
                try {
                    CreateCsvFile.writeLine(writer,
                            Arrays.asList(String.valueOf(product.getId()), product.getName(), product.getCondition(), product.getState(), String.valueOf(product.getPrice())));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
