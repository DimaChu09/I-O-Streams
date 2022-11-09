package org.example;


import org.example.basket.Basket;
import org.example.log.ClientLog;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.Scanner;

public class Main {
    final static String[][] PRODUCTS = {{"Молоко", "100"}, {"Крупа", "50"}, {"Чай", "80"}, {"Сахар", "60"}};

    static boolean isBasketLoadEnabled = false;
    static String basketLoadFromFileName = "basket.json";
    static String basketLoadFormat = "json";

    static boolean isBasketSaveEnabled = false;
    static String basketSaveToFileName = "basket.json";
    static String basketSaveFormat = "json";

    static boolean isLogEnabled = false;
    static String logFileName = "log.csv";

    public static void main(String[] args) {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File("shop.xml"));
            // Получаем корневой элемент (config)
            Node root = doc.getDocumentElement();
            // Передаем корневой элемент методу парсинга xml файла
            readXML(root);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        System.out.println("isBasketLoadEnabled " + isBasketLoadEnabled);
//        System.out.println("basketLoadFromFileName " + basketLoadFromFileName);
//        System.out.println("basketLoadFormat " + basketLoadFormat);
//        System.out.println("isBasketSaveEnabled " + isBasketSaveEnabled);
//        System.out.println("basketSaveToFileName " + basketSaveToFileName);
//        System.out.println("basketSaveFormat " + basketSaveFormat);
//        System.out.println("isLogEnabled " + isLogEnabled);
//        System.out.println("logFileName " + logFileName);
        Scanner scanner = new Scanner(System.in);
        Basket basket = new Basket(PRODUCTS);
        // Не знаю зачем в названии файла он указан с расширением, если далее идет строка с этим расширением,
        // посчитал правильным отбросить расширение из названия и добавить расширение из соответствующего поля
        // иначе не вижу смысла в нем, не искать же файл basket.json.txt например? или basket.json.json...
        String basketLoadFileName = basketLoadFromFileName.split("\\.")[0] + "." + basketLoadFormat;
        String basketSaveFileName = basketSaveToFileName.split("\\.")[0] + "." + basketSaveFormat;
        File basketSaveFile = new File(basketSaveFileName);
        File basketLoadFile = new File(basketLoadFileName);
        File operationsLog = new File(logFileName);
        ClientLog log = new ClientLog();
        if (isBasketLoadEnabled == true) {
            if (basketLoadFile.exists()) {
                String[][] loadedBasket = PRODUCTS;
                if (basketLoadFormat.equals("json")) {
                    loadedBasket = Basket.loadFromJSON(basketLoadFile);
                } else if (basketLoadFormat.equals("txt")) {
                    loadedBasket = Basket.loadFromFile(basketLoadFile);
                }
                for (int i = 0; i < PRODUCTS.length; i++) {
                    for (int j = 0; j < loadedBasket.length; j++) {
                        if (loadedBasket[j][0].equals(PRODUCTS[i][0])) {
                            basket.addToCart(i, Integer.parseInt(loadedBasket[j][1]));
                        }
                    }
                }
                basket.printCart();
            } else {
                System.out.println("Ранее созданная корзина отсутствует, будет формироваться новая");
            }
        } else System.out.println("Выбрана опция создания нового файла корзины (load disabled)");
        System.out.println();
        while (true) {
            printList();
            System.out.println("Выберите номер продукта из списка и количество через пробел. " +
                    "Для завершения программы и вывода итогов введите end:");
            String choice = scanner.nextLine();

            if (choice.equals("end")) {
                break;
            }

            String parts[] = choice.split(" "); // создаем массив из номера товара и количества
            if (parts.length != 2) {
                System.out.println("Некорректный ввод! Нужно ввести два числа!");
                continue;
            }
            int productNumber;
            int productCount;
            try {
                productNumber = Integer.parseInt(parts[0]) - 1; //порядковый номер продукта в массиве,
                productCount = Integer.parseInt(parts[1]); // количество единиц данного продукта
                log.log(productNumber, productCount);
            } catch (NumberFormatException e) {
                System.out.println("Вы ввели что-то совсем непонятное");
                continue;
            }
            if (productNumber < 0 || productNumber >= PRODUCTS.length) {
                System.out.println("Выберите порядковый номер в соответствии с представленным списком");
                continue;
            } else if (productCount < 0) {
                System.out.println("Мы не можем положить в корзину отрицательное количество товара");
                continue;
            } else if (productCount == 0) {
                System.out.println("Вы ничего не положили в корзину");
                continue;
            }
            System.out.println("Вы положили в корзину: " + PRODUCTS[productNumber][0] + ", " + productCount + " шт");
            basket.addToCart(productNumber, productCount);
            if (isBasketSaveEnabled && basketSaveFormat.equals("json")) {
                basket.saveJSON(basketSaveFile);
            } else if (isBasketSaveEnabled && basketSaveFormat.equals("txt")) {
                basket.saveTxt(basketSaveFile);
            }
        }
        basket.printCart();
        if (isBasketSaveEnabled == false) System.out.println("Опция сохранения файла корзины отключена");
        if (isLogEnabled) {
            log.exportAsCSV(operationsLog);
        } else System.out.println("Опция сохранения Log-файла отключена");
    }

    static void printList() {
        System.out.println("Наименование товара, цена\n");
        for (int i = 0; i < PRODUCTS.length; i++) {
            System.out.println(PRODUCTS[i][0] + ", " + PRODUCTS[i][1] + " руб/шт");
        }
    }

    // Описываем метод парсинга xml
    static void readXML(Node node) {
        // Получаем список дочерних элементов корневого элемент (config)
        // их 3 - load, save, log
        NodeList paramGroups = node.getChildNodes();
        for (int i = 0; i < paramGroups.getLength(); i++) {
            Node loadSaveLog = paramGroups.item(i);
            if (Node.ELEMENT_NODE == loadSaveLog.getNodeType()) {
                // Запрашиваем список дочерних элементов у каждого элемента (у load, save и log)
                NodeList parametersValues = loadSaveLog.getChildNodes();
                for (int j = 0; j < parametersValues.getLength(); j++) {
                    Node paramValue = parametersValues.item(j);
                    // Выкидываем текстовые ноды из рассмотрения, в данном случае они ничего не несут
                    if (paramValue.getNodeType() != Node.TEXT_NODE) {
                        // И рассматриваем поля внутри каждого элемента присваивая их значения
                        // соответствующим переменным для дальнейшего написания логики поведения программы
                        if (loadSaveLog.getNodeName().equals("load")) {
                            if (paramValue.getNodeName().equals("enabled")) {
                                if (paramValue.getFirstChild().getTextContent().equals("true")) {
                                    isBasketLoadEnabled = true;
                                } else isBasketLoadEnabled = false;
                            } else if (paramValue.getNodeName().equals("fileName")) {
                                basketLoadFromFileName = paramValue.getFirstChild().getTextContent();
                            } else if (paramValue.getNodeName().equals("format")) {
                                basketLoadFormat = paramValue.getFirstChild().getTextContent();
                            }
                        } else if (loadSaveLog.getNodeName().equals("save")) {
                            if (paramValue.getNodeName().equals("enabled")) {
                                if (paramValue.getFirstChild().getTextContent().equals("true")) {
                                    isBasketSaveEnabled = true;
                                } else isBasketSaveEnabled = false;
                            } else if (paramValue.getNodeName().equals("fileName")) {
                                basketSaveToFileName = paramValue.getFirstChild().getTextContent();
                            } else if (paramValue.getNodeName().equals("format")) {
                                basketSaveFormat = paramValue.getFirstChild().getTextContent();
                            }
                        } else if (loadSaveLog.getNodeName().equals("log")) {
                            if (paramValue.getNodeName().equals("enabled")) {
                                if (paramValue.getFirstChild().getTextContent().equals("true")) {
                                    isLogEnabled = true;
                                } else isLogEnabled = false;
                            } else if (paramValue.getNodeName().equals("fileName")) {
                                logFileName = paramValue.getFirstChild().getTextContent();
                            }
                        }
                    }
                }
            }
        }
    }
}