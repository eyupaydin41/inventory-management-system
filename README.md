# Inventory Management System

## Gereksinimler
- Java 11 veya üzeri
- Maven 3.x
- PostgreSQL (veya desteklenen başka bir veritabanı)

## Kurulum ve Çalıştırma

1. **Veritabanını Kurun**
   - `database/inventory_management_system.sql` dosyasını PostgreSQL'de çalıştırarak veritabanını oluşturun.
   - Örnek komut:
     ```sh
     psql -U <kullanıcı_adı> -d <veritabani_adi> -f database/inventory_management_system.sql
     ```

2. **Veritabanı Bağlantı Ayarlarını Yapın**
   - `src/main/resources/application.properties` dosyasında veritabanı kullanıcı adı, şifre ve bağlantı adresini güncelleyin:
     ```
     javafx.datasource.url=jdbc:postgresql://localhost:5432/<veritabani_adi>
     javafx.datasource.username=<kullanıcı_adı>
     javafx.datasource.password=<şifre>
     ```

3. **Projeyi Derleyin**
   ```sh
   mvn clean install
   ```

4. **Projeyi Çalıştırın**
   ```sh
   mvn javafx:run
   ```
   veya
   ```sh
   java -jar target/inventory-management-system-<version>.jar
   ```

## Proje Hakkında
Bu uygulama, stok ve envanter yönetimi için geliştirilmiştir. Temel özellikler:
- Ürün ekleme, silme, güncelleme
- Stok takibi
- Raporlama

## Notlar
- Gerekli kütüphaneler Maven ile otomatik olarak indirilecektir.
- Eğer farklı bir veritabanı kullanacaksanız, `application.properties` dosyasındaki bağlantı ayarlarını güncellemeniz yeterlidir.
- Sorun yaşarsanız veya ek bilgiye ihtiyaç duyarsanız, proje sahibi ile iletişime geçebilirsiniz.

## Katkıda Bulunma
Katkıda bulunmak isterseniz, lütfen bir pull request gönderin veya issue açın.

---
