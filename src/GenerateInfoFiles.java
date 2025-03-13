import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class GenerateInfoFiles {

    private static final String[] NOMBRES = {"Juan", "Maria", "Pedro", "Luisa", "Carlos"};
    private static final String[] APELLIDOS = {"Gomez", "Lopez", "Martinez", "Garcia", "Rodriguez"};
    private static final String[] PRODUCTOS = {"Lapiz", "Cuaderno", "Borrador", "Regla", "Tijeras"};

    public static void main(String[] args) {
        try {
            createSalesManInfoFile(5);
            createProductsFile(5);
            for (int i = 0; i < 5; i++) {
                createSalesMenFile(10, NOMBRES[i] + " " + APELLIDOS[i], 1000 + i);
            }
            System.out.println("Archivos generados exitosamente.");
        } catch (IOException e) {
            System.err.println("Error generando archivos: " + e.getMessage());
        }
    }

    public static void createSalesMenFile(int randomSalesCount, String name, long id) throws IOException {
        try (FileWriter writer = new FileWriter("salesman_" + id + ".txt")) {
            Random random = new Random();
            for (int i = 0; i < randomSalesCount; i++) {
                int productId = random.nextInt(5) + 1;
                int quantity = random.nextInt(10) + 1;
                writer.write("CC;" + id + "\n");
                writer.write(productId + ";" + quantity + ";\n");
            }
        }
    }

    public static void createProductsFile(int productsCount) throws IOException {
        try (FileWriter writer = new FileWriter("products.txt")) {
            Random random = new Random();
            for (int i = 1; i <= productsCount; i++) {
                String productName = PRODUCTOS[i - 1];
                double price = 1000 + random.nextInt(5000);
                writer.write(i + ";" + productName + ";" + price + "\n");
            }
        }
    }

    public static void createSalesManInfoFile(int salesmanCount) throws IOException {
        try (FileWriter writer = new FileWriter("salesman_info.txt")) {
            Random random = new Random();
            for (int i = 0; i < salesmanCount; i++) {
                String nombre = NOMBRES[random.nextInt(NOMBRES.length)];
                String apellido = APELLIDOS[random.nextInt(APELLIDOS.length)];
                long id = 1000 + i;
                writer.write("CC;" + id + ";" + nombre + ";" + apellido + "\n");
            }
        }
    }
}
