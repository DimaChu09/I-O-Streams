package basket;

import java.io.*;

public class Basket implements Serializable {

    private static final long serialVersionUID = 1L;

    protected String[][] productsList; // двумерный массив-список товаров с ценами, доступных к покупке
    protected int[] itemsInCart; // массив количества каждого товара в корзине
    protected int bill = 0;


    public Basket(String[][] productsList) {
        this.productsList = productsList;
        this.itemsInCart = new int[productsList.length];
    }

    public void addToCart(int productNum, int amount) {
        itemsInCart[productNum] += amount;
        bill += Integer.parseInt(productsList[productNum][1]) * amount;
    }

    public void printCart() {
        System.out.println("Ваша корзина покупок:");
        for (int i = 0; i < itemsInCart.length; i++) {
            if (itemsInCart[i] != 0) {
                System.out.println(productsList[i][0] + ", " + productsList[i][1] + " руб/шт: "
                        + itemsInCart[i] + " шт, " + (Integer.parseInt(productsList[i][1]) * itemsInCart[i]) + " руб");
            }
        }
        System.out.println("Общая стоимость: " + bill);
    }

    public void saveBin(File file) {
        try (FileOutputStream outputStream = new FileOutputStream(file);
             ObjectOutputStream objOutStream = new ObjectOutputStream(outputStream)) {
            objOutStream.writeObject(this);
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static Basket loadFromBinFile(File file) {
        Basket basket = null;
        try (FileInputStream inputStream = new FileInputStream(file);
             ObjectInputStream objInpStream = new ObjectInputStream(inputStream)) {
            basket = (Basket) objInpStream.readObject();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return basket;
    }
}
