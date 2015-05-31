package de.dawanda.dawandaclient.networking;

/**
 * Created by emanuele on 29.05.15.
 */
public class CommandFactory {

    public static BaseCommand makeCategoriesCommand(CommandListener<String> listener) {
        return new BaseCommand("category.json", "ISO-8859-1", listener);
    }

    public static BaseCommand makeProductsCommand(CommandListener<String> listener) {
        return new BaseCommand("products.json", "ISO-8859-1", listener);
    }

}
