package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

// ===== ΚΥΡΙΑ ΕΦΑΡΜΟΓΗ =====
@SpringBootApplication
public class PetAdoptionApplication {
    public static void main(String[] args) {
        SpringApplication.run(PetAdoptionApplication.class, args);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

// ===== ΜΟΝΤΕΛΑ =====
@Entity
@Table(name = "users")
class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 20)
    private String username;

    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    @NotBlank
    @Size(max = 120)
    private String password;

    private String role = "USER";

    public User() {}

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}

@Entity
@Table(name = "pets")
class Pet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 50)
    private String name;

    @NotBlank
    @Size(max = 20)
    private String type;

    @NotBlank
    @Size(max = 50)
    private String breed;

    private Integer age;

    @NotBlank
    @Size(max = 10)
    private String gender;

    @Size(max = 500)
    private String description;

    private String imageUrl;

    private Boolean available = true;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getBreed() { return breed; }
    public void setBreed(String breed) { this.breed = breed; }
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public Boolean getAvailable() { return available; }
    public void setAvailable(Boolean available) { this.available = available; }
}

@Entity
@Table(name = "adoption_requests")
class AdoptionRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userEmail;
    private Long petId;
    private String petName;

    @Temporal(TemporalType.TIMESTAMP)
    private Date requestDate = new Date();

    private String status = "PENDING";

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }
    public Long getPetId() { return petId; }
    public void setPetId(Long petId) { this.petId = petId; }
    public String getPetName() { return petName; }
    public void setPetName(String petName) { this.petName = petName; }
    public Date getRequestDate() { return requestDate; }
    public void setRequestDate(Date requestDate) { this.requestDate = requestDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

// ===== REPOSITORIES =====
@Repository
interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
}

@Repository
interface PetRepository extends JpaRepository<Pet, Long> {
    List<Pet> findByAvailable(Boolean available);
    List<Pet> findByType(String type);
}

@Repository
interface AdoptionRequestRepository extends JpaRepository<AdoptionRequest, Long> {
    List<AdoptionRequest> findByUserEmail(String userEmail);
    Optional<AdoptionRequest> findByUserEmailAndPetId(String userEmail, Long petId);
}

// ===== SERVICES =====
@Service
class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public boolean checkPassword(User user, String rawPassword) {
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public User createUser(String username, String email, String password, String role) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        return userRepository.save(user);
    }

    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }
}

@Service
class PetService {
    @Autowired
    private PetRepository petRepository;

    public List<Pet> getAllPets() {
        return petRepository.findAll();
    }

    public List<Pet> getAvailablePets() {
        return petRepository.findByAvailable(true);
    }

    public List<Pet> getPetsByType(String type) {
        return petRepository.findByType(type);
    }

    public Optional<Pet> getPetById(Long id) {
        return petRepository.findById(id);
    }

    public Pet savePet(Pet pet) {
        return petRepository.save(pet);
    }

    public void deletePet(Long id) {
        petRepository.deleteById(id);
    }
}

@Service
class AdoptionService {
    @Autowired
    private AdoptionRequestRepository adoptionRequestRepository;

    public AdoptionRequest createAdoptionRequest(String userEmail, Long petId, String petName) {
        AdoptionRequest request = new AdoptionRequest();
        request.setUserEmail(userEmail);
        request.setPetId(petId);
        request.setPetName(petName);
        request.setRequestDate(new Date());
        request.setStatus("PENDING");

        return adoptionRequestRepository.save(request);
    }

    public List<AdoptionRequest> getUserAdoptions(String userEmail) {
        return adoptionRequestRepository.findByUserEmail(userEmail);
    }

    public boolean hasUserAdoptedPet(String userEmail, Long petId) {
        return adoptionRequestRepository.findByUserEmailAndPetId(userEmail, petId).isPresent();
    }
}

// ===== JWT Utilities =====
@Component
class JwtUtils {
    private final String SECRET_KEY = "petAdoptionSecretKey";

    public String generateToken(String username) {
        return Base64.getEncoder().encodeToString((username + "|" + System.currentTimeMillis()).getBytes());
    }

    public String validateToken(String token) {
        try {
            String decoded = new String(Base64.getDecoder().decode(token));
            String[] parts = decoded.split("\\|");
            if (parts.length == 2) {
                return parts[0];
            }
        } catch (Exception e) {
            // Token is invalid
        }
        return null;
    }
}

// ===== REST CONTROLLERS =====
@RestController
@RequestMapping("/api/auth")
class AuthController {
    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String username, @RequestParam String password) {
        try {
            User user = userService.findByUsername(username);
            if (user == null) {
                user = userService.findByEmail(username);
            }

            if (user != null && userService.checkPassword(user, password)) {
                String token = jwtUtils.generateToken(user.getUsername());
                return ResponseEntity.ok()
                        .body(Map.of(
                                "token", token,
                                "username", user.getUsername(),
                                "email", user.getEmail()
                        ));
            } else {
                return ResponseEntity.status(401).body("Λάθος όνομα χρήστη ή κωδικός πρόσβασης");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Σφάλμα κατά τη σύνδεση: " + e.getMessage());
        }
    }
}

@RestController
@RequestMapping("/api/users")
class UserRestController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String password) {

        if (userService.existsByUsername(username)) {
            return ResponseEntity.badRequest().body("Το όνομα χρήστη χρησιμοποιείται ήδη");
        }
        if (userService.existsByEmail(email)) {
            return ResponseEntity.badRequest().body("Το email χρησιμοποιείται ήδη");
        }

        try {
            User newUser = userService.createUser(username, email, password, "USER");
            return ResponseEntity.ok(Map.of(
                    "id", newUser.getId(),
                    "username", newUser.getUsername(),
                    "email", newUser.getEmail()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Σφάλμα κατά την εγγραφή: " + e.getMessage());
        }
    }
}

@RestController
@RequestMapping("/api/pets")
class PetRestController {
    @Autowired
    private PetService petService;

    @GetMapping
    public ResponseEntity<List<Pet>> getAvailablePets() {
        try {
            List<Pet> availablePets = petService.getAvailablePets();
            return ResponseEntity.ok(availablePets);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Collections.emptyList());
        }
    }
}

@RestController
@RequestMapping("/api/adopt")
class AdoptionController {
    @Autowired
    private AdoptionService adoptionService;
    @Autowired
    private PetService petService;
    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping
    public ResponseEntity<?> adoptPet(
            @RequestParam Long petId,
            @RequestParam String petName,
            @RequestHeader("Authorization") String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Απαιτείται σύνδεση");
        }

        String token = authHeader.substring(7);
        String username = jwtUtils.validateToken(token);

        if (username == null) {
            return ResponseEntity.status(401).body("Μη έγκυρο token");
        }

        try {
            AdoptionRequest request = adoptionService.createAdoptionRequest(username, petId, petName);

            Optional<Pet> petOptional = petService.getPetById(petId);
            if (petOptional.isPresent()) {
                Pet pet = petOptional.get();
                pet.setAvailable(false);
                petService.savePet(pet);
            }

            return ResponseEntity.ok(Map.of(
                    "message", "Η αίτηση υιοθεσίας υποβλήθηκε επιτυχώς",
                    "requestId", request.getId()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Σφάλμα κατά την υποβολή αίτησης: " + e.getMessage());
        }
    }
}

// ===== DATA INITIALIZATION =====
@Component
class DataLoader implements CommandLineRunner {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PetRepository petRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Δημιουργία admin user
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@petadoption.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole("ADMIN");
            userRepository.save(admin);
        }

        // Δημιουργία δοκιμαστικών κατοικίδιων (μόνο 2 όπως ζητήθηκε)
        if (petRepository.count() == 0) {
            Pet pet1 = new Pet();
            pet1.setName("Buddy");
            pet1.setType("Σκύλος");
            pet1.setBreed("Golden Retriever");
            pet1.setAge(2);
            pet1.setGender("Αρσενικό");
            pet1.setDescription("Φιλικός και παιχνιδιάρης σκύλος");
            pet1.setImageUrl("https://images.unsplash.com/photo-1543466835-00a7907e9de1?w=400&h=300&fit=crop");
            pet1.setAvailable(true);
            petRepository.save(pet1);

            Pet pet2 = new Pet();
            pet2.setName("Luna");
            pet2.setType("Γάτα");
            pet2.setBreed("Siamese");
            pet2.setAge(3);
            pet2.setGender("Θηλυκό");
            pet2.setDescription("Ήρεμη και τρυφερή γάτα");
            pet2.setImageUrl("https://images.unsplash.com/photo-1573148164257-8a2b173be464?w=400&h=300&fit=crop");
            pet2.setAvailable(true);
            petRepository.save(pet2);
        }
    }
}