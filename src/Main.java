import java.io.*;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        try {
            // Cargar vendedores desde CSV
            Map<Long, Vendedor> vendedores = leerVendedoresDesdeCSV("./archivos_csv/vendedor.csv"); // Busca en la carpeta raíz

            // Cargar productos desde CSV
            Map<Integer, Producto> productos = leerProductosDesdeCSV("./archivos_csv/productos.csv"); // Busca en la carpeta raíz

            // Generar archivos de ventas para cada vendedor
            generarArchivosDeVentas(vendedores, productos);

            // Procesar ventas y generar reportes
            procesarVentas(vendedores, productos);
            generarReporteVendedores(vendedores, "../reporte_vendedores.csv"); // Genera en la carpeta raíz
            generarReporteProductos(productos, "../reporte_productos.csv"); // Genera en la carpeta raíz

            System.out.println("Archivos generados y reportes creados exitosamente.");
        } catch (IOException e) {
            System.err.println("Error procesando archivos: " + e.getMessage());
        }
    }

    private static Map<Long, Vendedor> leerVendedoresDesdeCSV(String filename) throws IOException {
        Map<Long, Vendedor> vendedores = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            boolean primeraLinea = true; // Para saltar la cabecera
            while ((line = reader.readLine()) != null) {
                if (primeraLinea) {
                    primeraLinea = false;
                    continue; // Saltar la cabecera
                }
                String[] parts = line.split(";");
                long id = Long.parseLong(parts[1]); // NúmeroDocumento
                String nombres = parts[2];
                String apellidos = parts[3];
                vendedores.put(id, new Vendedor(nombres, apellidos));
            }
        }
        return vendedores;
    }

    private static Map<Integer, Producto> leerProductosDesdeCSV(String filename) throws IOException {
        Map<Integer, Producto> productos = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            boolean primeraLinea = true; // Para saltar la cabecera
            while ((line = reader.readLine()) != null) {
                if (primeraLinea) {
                    primeraLinea = false;
                    continue; // Saltar la cabecera
                }
                String[] parts = line.split(";");
                int id = Integer.parseInt(parts[0]); // ID
                String nombre = parts[1];
                double precio = Double.parseDouble(parts[2]);
                productos.put(id, new Producto(nombre, precio));
            }
        }
        return productos;
    }

    private static void generarArchivosDeVentas(Map<Long, Vendedor> vendedores, Map<Integer, Producto> productos) throws IOException {
        Random random = new Random();
        for (Map.Entry<Long, Vendedor> entry : vendedores.entrySet()) {
            long idVendedor = entry.getKey();
            try (FileWriter writer = new FileWriter("../salesman_" + idVendedor + ".txt")) { // Genera en la carpeta raíz
                // Escribir información del vendedor
                writer.write("CC;" + idVendedor + "\n");

                // Generar ventas aleatorias
                for (int i = 0; i < 10; i++) { // 10 ventas por vendedor
                    int idProducto = random.nextInt(productos.size()) + 1;
                    int cantidad = random.nextInt(10) + 1; // Cantidad entre 1 y 10
                    writer.write(idProducto + ";" + cantidad + ";\n");
                }
            }
        }
    }

    private static void procesarVentas(Map<Long, Vendedor> vendedores, Map<Integer, Producto> productos) throws IOException {
        File folder = new File(".."); // Busca en la carpeta raíz
        File[] files = folder.listFiles((_, name) -> name.startsWith("salesman_") && name.endsWith(".txt"));
        if (files != null) {
            for (File file : files) {
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line;
                    long idVendedor = -1;
                    while ((line = reader.readLine()) != null) {
                        String[] parts = line.split(";");
                        if (parts[0].equals("CC")) {
                            idVendedor = Long.parseLong(parts[1]);
                        } else {
                            int idProducto = Integer.parseInt(parts[0]);
                            int cantidad = Integer.parseInt(parts[1]);
                            Vendedor vendedor = vendedores.get(idVendedor);
                            Producto producto = productos.get(idProducto);
                            if (vendedor != null && producto != null) {
                                vendedor.agregarVenta(producto, cantidad);
                            }
                        }
                    }
                }
            }
        }
    }

    private static void generarReporteVendedores(Map<Long, Vendedor> vendedores, String filename) throws IOException {
        List<Vendedor> listaVendedores = new ArrayList<>(vendedores.values());
        listaVendedores.sort((v1, v2) -> Double.compare(v2.getTotalVentas(), v1.getTotalVentas()));
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write("Nombre Vendedor;TotalVentas\n"); // Cabecera
            for (Vendedor v : listaVendedores) {
                writer.write(v.getNombre() + ";" + v.getTotalVentas() + "\n"); // Datos
            }
        }
    }

    private static void generarReporteProductos(Map<Integer, Producto> productos, String filename) throws IOException {
        List<Producto> listaProductos = new ArrayList<>(productos.values());
        listaProductos.sort((p1, p2) -> Integer.compare(p2.getCantidadVendida(), p1.getCantidadVendida()));
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write("Nombre Producto;CantidadVendida\n"); // Cabecera
            for (Producto p : listaProductos) {
                writer.write(p.getNombre() + ";" + p.getCantidadVendida() + "\n"); // Datos
            }
        }
    }
}

class Vendedor {
    private String nombre;
    private String apellido;
    private double totalVentas;

    public Vendedor(String nombre, String apellido) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.totalVentas = 0;
    }
    public void agregarVenta(Producto producto, int cantidad) {
        this.totalVentas += producto.getPrecio() * cantidad;
        producto.vender(cantidad);
    }

    public double getTotalVentas() {
        return totalVentas;
    }

    public String getNombre() {
        return nombre + " " + apellido;
    }
}

class Producto {
    private String nombre;
    private double precio;
    private int cantidadVendida;

    public Producto(String nombre, double precio) {
        this.nombre = nombre;
        this.precio = precio;
        this.cantidadVendida = 0;
    }

    public void vender(int cantidad) {
        this.cantidadVendida += cantidad;
    }

    public double getPrecio() {
        return precio;
    }

    public String getNombre() {
        return nombre;
    }

    public int getCantidadVendida() {
        return cantidadVendida;
    }
}