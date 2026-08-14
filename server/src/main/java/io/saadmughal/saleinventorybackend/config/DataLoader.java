package io.saadmughal.saleinventorybackend.config;

import io.saadmughal.saleinventorybackend.entity.Customer;
import io.saadmughal.saleinventorybackend.entity.Product;
import io.saadmughal.saleinventorybackend.entity.ProductDetail;
import io.saadmughal.saleinventorybackend.entity.Purchase;
import io.saadmughal.saleinventorybackend.entity.PurchaseStatus;
import io.saadmughal.saleinventorybackend.entity.Sale;
import io.saadmughal.saleinventorybackend.entity.SaleStatus;
import io.saadmughal.saleinventorybackend.entity.Supplier;
import io.saadmughal.saleinventorybackend.repository.CustomerRepository;
import io.saadmughal.saleinventorybackend.repository.ProductDetailRepository;
import io.saadmughal.saleinventorybackend.repository.ProductRepository;
import io.saadmughal.saleinventorybackend.repository.PurchaseRepository;
import io.saadmughal.saleinventorybackend.repository.SaleRepository;
import io.saadmughal.saleinventorybackend.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads sample data into the database on application startup
 * Only runs if database is empty
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final ProductDetailRepository productDetailRepository;
    private final CustomerRepository customerRepository;
    private final SupplierRepository supplierRepository;
    private final PurchaseRepository purchaseRepository;
    private final SaleRepository saleRepository;

    @Override
    @Transactional
    public void run(String... args) {
        // Check if data already exists
        if (productRepository.count() > 0) {
            log.info("Database already contains data. Skipping data loading.");
            return;
        }

        log.info("Loading sample data into database...");

        // Load data in order of dependencies
        List<Product> products = loadProducts();
        loadProductDetails(products);
        List<Customer> customers = loadCustomers();
        List<Supplier> suppliers = loadSuppliers();
        loadInitialPurchases(products, suppliers);
        loadSampleSales(products, customers);

        log.info("Sample data loaded successfully!");
        log.info("Products: {}", productRepository.count());
        log.info("Customers: {}", customerRepository.count());
        log.info("Suppliers: {}", supplierRepository.count());
        log.info("Purchases: {}", purchaseRepository.count());
        log.info("Sales: {}", saleRepository.count());
    }

    private List<Product> loadProducts() {
        log.info("Loading products...");

        List<Product> products = new ArrayList<>();

        products.add(Product.builder()
                .code("P-1001")
                .name("Wireless Mouse")
                .unitPrice(new BigDecimal("25.99"))
                .currentStock(0) // Will be set by purchases
                .active(true)
                .build());

        products.add(Product.builder()
                .code("P-1002")
                .name("Mechanical Keyboard")
                .unitPrice(new BigDecimal("89.99"))
                .currentStock(0)
                .active(true)
                .build());

        products.add(Product.builder()
                .code("P-1003")
                .name("USB-C Cable")
                .unitPrice(new BigDecimal("12.50"))
                .currentStock(0)
                .active(true)
                .build());

        products.add(Product.builder()
                .code("P-1004")
                .name("Laptop Stand")
                .unitPrice(new BigDecimal("45.00"))
                .currentStock(0)
                .active(true)
                .build());

        products.add(Product.builder()
                .code("P-1005")
                .name("Webcam HD")
                .unitPrice(new BigDecimal("65.00"))
                .currentStock(0)
                .active(false) // Inactive product for testing
                .build());

        return productRepository.saveAll(products);
    }

    private void loadProductDetails(List<Product> products) {
        log.info("Loading product details...");

        List<ProductDetail> details = new ArrayList<>();

        details.add(ProductDetail.builder()
                .product(products.get(0))
                .brand("Logitech")
                .category("Electronics")
                .description("Wireless mouse with ergonomic design and long battery life")
                .minStockLevel(10)
                .taxRate(17.0)
                .build());

        details.add(ProductDetail.builder()
                .product(products.get(1))
                .brand("Corsair")
                .category("Electronics")
                .description("RGB mechanical keyboard with Cherry MX switches")
                .minStockLevel(5)
                .taxRate(17.0)
                .build());

        details.add(ProductDetail.builder()
                .product(products.get(2))
                .brand("Anker")
                .category("Accessories")
                .description("Durable USB-C charging and data cable, 6ft length")
                .minStockLevel(20)
                .taxRate(17.0)
                .build());

        details.add(ProductDetail.builder()
                .product(products.get(3))
                .brand("Rain Design")
                .category("Accessories")
                .description("Aluminum laptop stand with adjustable height")
                .minStockLevel(8)
                .taxRate(17.0)
                .build());

        details.add(ProductDetail.builder()
                .product(products.get(4))
                .brand("Logitech")
                .category("Electronics")
                .description("1080p webcam with auto-focus and built-in microphone")
                .minStockLevel(5)
                .taxRate(17.0)
                .build());

        productDetailRepository.saveAll(details);
    }

    private List<Customer> loadCustomers() {
        log.info("Loading customers...");

        List<Customer> customers = new ArrayList<>();

        customers.add(Customer.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .phone("+92-300-1234567")
                .address("123 Main Street, Lahore")
                .blocked(false)
                .build());

        customers.add(Customer.builder()
                .name("Jane Smith")
                .email("jane.smith@example.com")
                .phone("+92-321-9876543")
                .address("456 Park Avenue, Karachi")
                .blocked(false)
                .build());

        customers.add(Customer.builder()
                .name("Ali Ahmed")
                .email("ali.ahmed@example.com")
                .phone("+92-333-5555555")
                .address("789 Garden Road, Islamabad")
                .blocked(true) // Blocked customer for testing
                .build());

        customers.add(Customer.builder()
                .name("Sarah Khan")
                .email("sarah.khan@example.com")
                .phone("+92-345-7777777")
                .address("321 Liberty Market, Lahore")
                .blocked(false)
                .build());

        customers.add(Customer.builder()
                .name("Mike Wilson")
                .email("mike.wilson@example.com")
                .phone("+92-300-9999999")
                .address("654 Blue Area, Islamabad")
                .blocked(false)
                .build());

        return customerRepository.saveAll(customers);
    }

    private List<Supplier> loadSuppliers() {
        log.info("Loading suppliers...");

        List<Supplier> suppliers = new ArrayList<>();

        suppliers.add(Supplier.builder()
                .name("Tech Distributors Inc")
                .email("contact@techdist.com")
                .phone("+92-42-11111111")
                .companyName("Tech Distributors International")
                .address("Industrial Area, Lahore")
                .active(true)
                .build());

        suppliers.add(Supplier.builder()
                .name("Electronics Wholesale")
                .email("sales@elecwholesale.com")
                .phone("+92-21-22222222")
                .companyName("Electronics Wholesale Ltd")
                .address("Saddar, Karachi")
                .active(true)
                .build());

        suppliers.add(Supplier.builder()
                .name("Global Gadgets")
                .email("info@globalgadgets.com")
                .phone("+92-51-33333333")
                .companyName("Global Gadgets Corporation")
                .address("I-10 Markaz, Islamabad")
                .active(false) // Inactive supplier for testing
                .build());

        suppliers.add(Supplier.builder()
                .name("Prime Components")
                .email("orders@primecomp.com")
                .phone("+92-42-44444444")
                .companyName("Prime Components Pakistan")
                .address("Ferozepur Road, Lahore")
                .active(true)
                .build());

        suppliers.add(Supplier.builder()
                .name("Smart Solutions")
                .email("support@smartsol.com")
                .phone("+92-21-55555555")
                .companyName("Smart Solutions Pvt Ltd")
                .address("Clifton, Karachi")
                .active(true)
                .build());

        return supplierRepository.saveAll(suppliers);
    }

    private void loadInitialPurchases(List<Product> products, List<Supplier> suppliers) {
        log.info("Loading initial purchases to set product stock...");

        List<Purchase> purchases = new ArrayList<>();

        // Purchase 1: Wireless Mouse from Tech Distributors
        Purchase p1 = Purchase.builder()
                .product(products.get(0))
                .supplier(suppliers.get(0))
                .date(LocalDateTime.now().minusDays(30))
                .quantity(50)
                .unitCost(new BigDecimal("18.00"))
                .totalCost(new BigDecimal("900.00"))
                .status(PurchaseStatus.RECEIVED)
                .build();
        products.get(0).setCurrentStock(50);
        purchases.add(p1);

        // Purchase 2: Mechanical Keyboard from Electronics Wholesale
        Purchase p2 = Purchase.builder()
                .product(products.get(1))
                .supplier(suppliers.get(1))
                .date(LocalDateTime.now().minusDays(25))
                .quantity(30)
                .unitCost(new BigDecimal("65.00"))
                .totalCost(new BigDecimal("1950.00"))
                .status(PurchaseStatus.RECEIVED)
                .build();
        products.get(1).setCurrentStock(30);
        purchases.add(p2);

        // Purchase 3: USB-C Cables from Prime Components
        Purchase p3 = Purchase.builder()
                .product(products.get(2))
                .supplier(suppliers.get(3))
                .date(LocalDateTime.now().minusDays(20))
                .quantity(100)
                .unitCost(new BigDecimal("8.00"))
                .totalCost(new BigDecimal("800.00"))
                .status(PurchaseStatus.RECEIVED)
                .build();
        products.get(2).setCurrentStock(100);
        purchases.add(p3);

        // Purchase 4: Laptop Stands from Smart Solutions
        Purchase p4 = Purchase.builder()
                .product(products.get(3))
                .supplier(suppliers.get(4))
                .date(LocalDateTime.now().minusDays(15))
                .quantity(25)
                .unitCost(new BigDecimal("32.00"))
                .totalCost(new BigDecimal("800.00"))
                .status(PurchaseStatus.RECEIVED)
                .build();
        products.get(3).setCurrentStock(25);
        purchases.add(p4);

        // Purchase 5: Webcams from Tech Distributors (low stock scenario)
        Purchase p5 = Purchase.builder()
                .product(products.get(4))
                .supplier(suppliers.get(0))
                .date(LocalDateTime.now().minusDays(10))
                .quantity(3) // Below minStockLevel of 5
                .unitCost(new BigDecimal("48.00"))
                .totalCost(new BigDecimal("144.00"))
                .status(PurchaseStatus.RECEIVED)
                .build();
        products.get(4).setCurrentStock(3);
        purchases.add(p5);

        purchaseRepository.saveAll(purchases);
        productRepository.saveAll(products);
    }

    private void loadSampleSales(List<Product> products, List<Customer> customers) {
        log.info("Loading sample sales...");

        List<Sale> sales = new ArrayList<>();

        // Sale 1: John Doe buys Wireless Mouse
        Sale s1 = Sale.builder()
                .product(products.get(0))
                .customer(customers.get(0))
                .date(LocalDateTime.now().minusDays(5))
                .quantity(2)
                .unitPrice(new BigDecimal("25.99"))
                .totalPrice(new BigDecimal("51.98"))
                .status(SaleStatus.CONFIRMED)
                .build();
        products.get(0).setCurrentStock(products.get(0).getCurrentStock() - 2);
        sales.add(s1);

        // Sale 2: Jane Smith buys Mechanical Keyboard
        Sale s2 = Sale.builder()
                .product(products.get(1))
                .customer(customers.get(1))
                .date(LocalDateTime.now().minusDays(4))
                .quantity(1)
                .unitPrice(new BigDecimal("89.99"))
                .totalPrice(new BigDecimal("89.99"))
                .status(SaleStatus.CONFIRMED)
                .build();
        products.get(1).setCurrentStock(products.get(1).getCurrentStock() - 1);
        sales.add(s2);

        // Sale 3: Sarah Khan buys USB-C Cables
        Sale s3 = Sale.builder()
                .product(products.get(2))
                .customer(customers.get(3))
                .date(LocalDateTime.now().minusDays(3))
                .quantity(5)
                .unitPrice(new BigDecimal("12.50"))
                .totalPrice(new BigDecimal("62.50"))
                .status(SaleStatus.CONFIRMED)
                .build();
        products.get(2).setCurrentStock(products.get(2).getCurrentStock() - 5);
        sales.add(s3);

        // Sale 4: Mike Wilson buys Laptop Stand
        Sale s4 = Sale.builder()
                .product(products.get(3))
                .customer(customers.get(4))
                .date(LocalDateTime.now().minusDays(2))
                .quantity(1)
                .unitPrice(new BigDecimal("45.00"))
                .totalPrice(new BigDecimal("45.00"))
                .status(SaleStatus.CONFIRMED)
                .build();
        products.get(3).setCurrentStock(products.get(3).getCurrentStock() - 1);
        sales.add(s4);

        // Sale 5: John Doe buys USB-C Cables
        Sale s5 = Sale.builder()
                .product(products.get(2))
                .customer(customers.get(0))
                .date(LocalDateTime.now().minusDays(1))
                .quantity(3)
                .unitPrice(new BigDecimal("12.50"))
                .totalPrice(new BigDecimal("37.50"))
                .status(SaleStatus.CONFIRMED)
                .build();
        products.get(2).setCurrentStock(products.get(2).getCurrentStock() - 3);
        sales.add(s5);

        // Sale 6: Cancelled sale example
        Sale s6 = Sale.builder()
                .product(products.get(1))
                .customer(customers.get(1))
                .date(LocalDateTime.now().minusHours(12))
                .quantity(1)
                .unitPrice(new BigDecimal("89.99"))
                .totalPrice(new BigDecimal("89.99"))
                .status(SaleStatus.CANCELLED)
                .build();
        // Note: Stock not decreased for cancelled sales
        sales.add(s6);

        saleRepository.saveAll(sales);
        productRepository.saveAll(products);
    }
}
