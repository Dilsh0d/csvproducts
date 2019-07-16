package com.csv.products.model;

import com.csv.products.dto.Product;

import java.util.*;

/**
 * @author dilsh0d
 */
public class FindCheapestProductModel {

    public static List<Product> cheapestProductList = new ArrayList<>(1_000);
    public static Map<Integer, List<Product>> productByIdListMap = new HashMap<>();

    public static synchronized void isCheapestProductAndAddStack(String[] values) {
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
                Product lastProduct = getLastProductCheapestList();
                if (lastProduct.getId().equals(newProduct.getId())) {
                    removeMoreThanPriceProductFromSame20ItemsAndReplace(newProduct);
                } else {
                    removeProductAndAdd(lastProduct, newProduct);
                }
            }
        }
    }

    private static void addNewCheapestProduct(Product product) {
        addProductByIdListMap(product);
        cheapestProductList.add(product);
        cheapestProductList.sort((o1, o2) -> {
            if (o1.getPrice() < o2.getPrice()) {
                return -1;
            } else if (o1.getPrice() > o2.getPrice()) {
                return 1;
            } else {
                return 0;
            }
        });
    }

    private static  void addProductByIdListMap(Product product) {
        if (!productByIdListMap.containsKey(product.getId())) {
            List<Product> sameProducts = Collections.synchronizedList(new ArrayList<>());
            productByIdListMap.put(product.getId(), sameProducts);
        }
        if (productByIdListMap.get(product.getId()).size() < 20) {
            productByIdListMap.get(product.getId()).add(product);
            productByIdListMap.get(product.getId()).sort((o1, o2) -> {
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

    private static  void removeProductAndAdd(Product lastProduct, Product product) {
        cheapestProductList.remove(lastProduct);
        productByIdListMap.get(lastProduct.getId()).remove(lastProduct);
        if(productByIdListMap.get(lastProduct.getId()).size() == 0) {
            productByIdListMap.remove(lastProduct.getId());
        }
        addNewCheapestProduct(product);
    }

    private static  void removeMoreThanPriceProductFromSame20ItemsAndReplace(Product product) {
        Product lastProduct = productByIdListMap.get(product.getId())
                .get(productByIdListMap.get(product.getId()).size() - 1);

        if (lastProduct.getPrice() > product.getPrice()) {
            cheapestProductList.remove(lastProduct);
            productByIdListMap.get(lastProduct.getId()).remove(lastProduct);
            if(productByIdListMap.get(lastProduct.getId()).size() == 0) {
                productByIdListMap.remove(lastProduct.getId());
            }
            addNewCheapestProduct(product);
        }
    }

    private static  Product getLastProductCheapestList() {
        return cheapestProductList.get(cheapestProductList.size()-1);
    }

    private static  boolean isLessThanPriceProductFromList(Product product) {
        Product lastProduct = cheapestProductList.get(cheapestProductList.size()-1);
        if (lastProduct.getPrice() > product.getPrice()) {
            return true;
        }
        return false;
    }

    private static  boolean isEmptyProductList() {
        return cheapestProductList.isEmpty();
    }

    private static  boolean isLessThanProductList1000() {
        return cheapestProductList.size() < 1000;
    }

    private static  boolean isLessThanPriceProductFromSame20Items(Product product) {
        Product lastProduct = productByIdListMap.get(product.getId())
                .get(productByIdListMap.get(product.getId()).size() - 1);

        if (lastProduct.getPrice() > product.getPrice()) {
            return true;
        }
        return false;
    }


    private static  boolean isLessThan20Items(Integer id) {
        if (productByIdListMap.containsKey(id)) {
            return productByIdListMap.size() < 20;
        }
        return true;
    }

    private static  boolean isEquals20Items(Integer id) {
        if (productByIdListMap.containsKey(id)) {
            return productByIdListMap.size()==20;
        }
        return false;
    }

}
