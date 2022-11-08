import basket.Basket;

import java.io.File;
import java.util.Scanner;

public class Main {
    final static String[][] PRODUCTS = {{"Молоко", "100"}, {"Крупа", "50"}, {"Чай", "80"}, {"Сахар", "60"}};

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Basket basket = new Basket(PRODUCTS);
        File basketTxt = new File("basket.txt");
        if (basketTxt.exists()) {
            String[][] loadedBasket = Basket.loadFromFile(basketTxt);
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
            } catch (NumberFormatException e) {
                System.out.println("Вы ввели что-то совсем непонятное");
                continue;
            }
            if (productNumber < 0 || productNumber > PRODUCTS.length) {
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
            basket.saveTxt(basketTxt);
        }
        basket.printCart();
    }

    static void printList() {
        System.out.println("Наименование товара, цена\n");
        for (int i = 0; i < PRODUCTS.length; i++) {
            System.out.println(PRODUCTS[i][0] + ", " + PRODUCTS[i][1] + " руб/шт");
        }
    }
}
