# Σύστημα Υιοθεσίας Κατοικίδιων

Αυτό είναι ένα σύστημα υιοθεσίας κατοικίδιων που αναπτύχθηκε ως μέρος της εργασίας για το μάθημα Κατανεμημένων Συστημάτων.

## Δομή Project

```
PetAdoptionSystem/
├── backend/           # Spring Boot εφαρμογή
├── frontend/          # Frontend HTML/JS/CSS
└── README.md          # Αυτό το αρχείο
```

##  Τεχνολογίες

- **Backend**: Spring Boot, Spring Security, JPA, H2 Database
- **Frontend**: HTML5, CSS3, Vanilla JavaScript
- **Authentication**: JWT Tokens


### Προαπαιτούμενα
- Java 17 ή νεότερη
- Maven

### Εκτέλεση Backend

```bash
cd backend
mvn spring-boot:run
```

Η εφαρμογή θα είναι διαθέσιμη στο: http://localhost:8080

### Χρήση Frontend

Ανοίξτε το αρχείο `frontend/index.html` στον browser σας.

##  API Endpoints

- `POST /api/users/register` - Εγγραφή νέου χρήστη
- `POST /api/auth/login` - Σύνδεση χρήστη
- `GET /api/pets` - Λήψη λίστας διαθέσιμων κατοικίδιων
- `POST /api/adopt` - Υποβολή αίτησης υιοθεσίας

##  Ομάδα Ανάπτυξης

it2022133