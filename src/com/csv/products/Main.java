package com.csv.products;

import com.csv.products.dto.Product;
import com.csv.products.exception.DirectoryPathNotFolderException;
import com.csv.products.exception.DirectoryPathNotFoundException;
import com.csv.products.exception.JavaArgNotFountDirectoryPathException;
import com.csv.products.task.Cheapest1000ProductsTask;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static String COMMA_DELIMITER = ";";

    public static List<Product> cheapestProductList = Collections.synchronizedList(new ArrayList<>());
    public static ConcurrentHashMap<Integer,List<Product>> productByIdListMap = new ConcurrentHashMap<>();



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
    }

    private void execute(File directory) {
        String[] fileNames =  directory.list();
        CountDownLatch downLatch = new CountDownLatch(fileNames.length);
        ExecutorService executorService = Executors.newFixedThreadPool(8);
        for(String csvFile:directory.list()) {
            executorService.execute(new Cheapest1000ProductsTask(directory.getAbsolutePath()+File.separator+csvFile, downLatch));
        }
        try {
            downLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        executorService.shutdown();
        System.out.println(cheapestProductList);
    }
}
