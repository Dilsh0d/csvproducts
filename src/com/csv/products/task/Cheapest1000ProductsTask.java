package com.csv.products.task;

import com.csv.products.Main;
import com.csv.products.dto.Product;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static com.csv.products.Main.COMMA_DELIMITER;

/**
 * @author dilsh0d
 */
public class Cheapest1000ProductsTask implements Runnable {

    private String filePath;
    private CountDownLatch downLatch;

    public Cheapest1000ProductsTask(String filePath, CountDownLatch downLatch) {
        this.filePath = filePath;
        this.downLatch = downLatch;
    }

    @Override
    public void run() {
        File f = new File(filePath);
        try (BufferedReader b = new BufferedReader(new FileReader(f))) {
            String readLine = "";
            while ((readLine = b.readLine()) != null) {
                String[] values = readLine.split(COMMA_DELIMITER);
                addCheapestProductStack(values);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        downLatch.countDown();
    }

    private void addCheapestProductStack(String[] values) {
        Product newProduct = new Product.Builder(values[0])
                .name(values[1])
                .condition(values[2])
                .state(values[3])
                .price(values[4])
                .build();

        if (newProduct.getId() != null && newProduct.getPrice() != null) {
            if (isEmptyProductList()) {
                addNewCheapestProduct(newProduct);
            } else if (isLessThanProductList1000()) {
                if (isLessThan20Items(newProduct.getId())) {
                    addNewCheapestProduct(newProduct);
                } else if (isEquals20Items(newProduct.getId()) && isLessThanPriceProductFromSame20Items(newProduct)) {
                    removeMoreThanPriceProductFromSame20ItemsAndReplace(newProduct);
                }
            } else if (isLessThanPriceProductFromList(newProduct)) {
                Product lastProduct = getLastProductChepestList();
                if (lastProduct.getId().equals(newProduct.getId())) {
                    removeMoreThanPriceProductFromSame20ItemsAndReplace(newProduct);
                } else {
                    removeProductAndAdd(lastProduct, newProduct);
                }
            }
        }
    }

    private void addNewCheapestProduct(Product product) {
        addProductByIdListMap(product);
        Main.cheapestProductList.add(product);
        Main.cheapestProductList.sort((o1, o2) -> {
            if (o1.getPrice() < o2.getPrice()) {
                return -1;
            } else if (o1.getPrice() > o2.getPrice()) {
                return 1;
            } else {
                return 0;
            }
        });
    }

    private void addProductByIdListMap(Product product) {
        if (!Main.productByIdListMap.containsKey(product.getId())) {
            List<Product> sameProducts = Collections.synchronizedList(new ArrayList<>());
            Main.productByIdListMap.put(product.getId(), sameProducts);
        }
        if (Main.productByIdListMap.get(product.getId()).size() < 20) {
            Main.productByIdListMap.get(product.getId()).add(product);
            Main.productByIdListMap.get(product.getId()).sort((o1, o2) -> {
                if (o1.getPrice() < o2.getPrice()) {
                    return -1;
                } else if (o1.getPrice() > o2.getPrice()) {
                    return 1;
                } else {
                    return 0;
                }
            });
        }
    }

    private void removeProductAndAdd(Product lastProduct, Product product) {
        Main.cheapestProductList.remove(lastProduct);
        Main.productByIdListMap.get(lastProduct.getId()).remove(lastProduct);
        addNewCheapestProduct(product);
    }

    private void removeMoreThanPriceProductFromSame20ItemsAndReplace(Product product) {
        Product lastProduct = Main.productByIdListMap.get(product.getId())
                .get(Main.productByIdListMap.get(product.getId()).size() - 1);

        if (lastProduct.getPrice() > product.getPrice()) {
            Main.cheapestProductList.remove(lastProduct);
            Main.productByIdListMap.get(lastProduct.getId()).remove(lastProduct);
            addNewCheapestProduct(product);
        }
    }

    private Product getLastProductChepestList() {
        return Main.cheapestProductList.get(Main.cheapestProductList.size()-1);
    }

    private boolean isLessThanPriceProductFromList(Product product) {
        Product lastProduct = Main.cheapestProductList.get(Main.cheapestProductList.size()-1);
        if (lastProduct.getPrice() > product.getPrice()) {
            return true;
        }
        return false;
    }

    private boolean isEmptyProductList() {
        return Main.cheapestProductList.isEmpty();
    }

    private boolean isLessThanProductList1000() {
        return Main.cheapestProductList.size() < 1000;
    }

    private boolean isLessThanPriceProductFromSame20Items(Product product) {
        Product lastProduct = Main.productByIdListMap.get(product.getId())
                .get(Main.productByIdListMap.get(product.getId()).size() - 1);

        if (lastProduct.getPrice() > product.getPrice()) {
            return true;
        }
        return false;
    }


    private boolean isLessThan20Items(Integer id) {
        if (Main.productByIdListMap.containsKey(id)) {
            return Main.productByIdListMap.size() < 20;
        }
        return true;
    }

    private boolean isEquals20Items(Integer id) {
        if (Main.productByIdListMap.containsKey(id)) {
            return Main.productByIdListMap.size()==20;
        }
        return false;
    }
}
