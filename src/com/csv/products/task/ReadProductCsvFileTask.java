package com.csv.products.task;

import com.csv.products.model.FindCheapestProductModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.concurrent.CountDownLatch;

import static com.csv.products.Main.COMMA_DELIMITER;

/**
 * @author dilsh0d
 */
public class ReadProductCsvFileTask implements Runnable {

    private String filePath;
    private CountDownLatch downLatch;

    public ReadProductCsvFileTask(String filePath, CountDownLatch downLatch) {
        this.filePath = filePath;
        this.downLatch = downLatch;
    }

    @Override
    public void run() {
        File f = new File(filePath);
        try (BufferedReader b = new BufferedReader(new FileReader(f))) {
            String readLine = "";
            long biginTime = System.currentTimeMillis();
            while ((readLine = b.readLine()) != null) {
                String[] values = readLine.split(COMMA_DELIMITER);
                FindCheapestProductModel.isCheapestProductAndAddStack(values);
            }
            long endTime = System.currentTimeMillis();
            long inSeconds = (endTime-biginTime)/1000;
            System.out.println("Done file name: \"" + filePath + "\" code execution time in secunds: " + String.valueOf(inSeconds)+" seconds");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            downLatch.countDown();
        }
    }
}
