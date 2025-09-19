TradeWave
E-Commerce Backend

TradeWave, Spring Boot kullanarak mikroservise evrilebilir katmanlı mimari ile geliştirilen öğretici bir e-ticaret backend uygulamasıdır.

🎯 Amaçlar
Spring Boot katmanlı mimariyi ileri seviye öğrenmek
Middleware, DTO-Entity mapping, JWT authentication, Exception Handling, Unit Test kültürü edinmek
Gerçekçi e-ticaret backend akışını kurmak
Mikroservis mimarisine evrilmek

## Domain Bazlı Akış Diyagramı (Görselleştirilmiş)

```
KULLANICI
   │
   ▼
┌─────────────┬─────────────┬─────────────┬─────────────┬─────────────┐
│   USER      │  PRODUCT    │   ORDER     │   PAYMENT   │   SELLER    │
│─────────────│─────────────│─────────────│─────────────│─────────────│
│ Controller  │ Controller  │ Controller  │ Controller  │ Controller  │
│ Service     │ Service     │ Service     │ Service     │ Service     │
│ Repository  │ Repository  │ Repository  │ Repository  │ Repository  │
│ Model       │ Model       │ Model       │ Model       │ Model       │
└─────┬───────┴─────┬───────┴─────┬───────┴─────┬───────┴─────┬───────┘
      │             │             │             │
      ▼             ▼             ▼             ▼
┌─────────────┬─────────────┬─────────────┬─────────────┐
│ CATEGORY    │   CART      │  ADDRESS    │   STORE     │
│─────────────│─────────────│─────────────│─────────────│
│ Controller  │ Controller  │ Controller  │ Controller  │
│ Service     │ Service     │ Service     │ Service     │
│ Repository  │ Repository  │ Repository  │ Repository  │
│ Model       │ Model       │ Model       │ Model       │
└─────┬───────┴─────┬───────┴─────┬───────┴─────┬───────┘
      │             │             │
      ▼             ▼             ▼
                    [VERİTABANI]
                         ▲
                         │
        ┌────────────────┼────────────────┐
        │                │                │
 [JWT Security]  [Exception Handler]  [Mapper]
```

Bu diyagram, domainlerin ve ortak katmanların ilişkilerini daha görsel ve anlaşılır şekilde sunar.
