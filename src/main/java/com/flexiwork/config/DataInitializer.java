package com.flexiwork.config;

import com.flexiwork.enums.*;
import com.flexiwork.model.*;
import com.flexiwork.repository.*;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Component
public class DataInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final CompanyUserRepository companyUserRepository;
    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;
    private final QRVerificationRepository qrVerificationRepository;
    private final CommissionPaymentRepository commissionPaymentRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, CompanyRepository companyRepository,
                           CompanyUserRepository companyUserRepository, JobRepository jobRepository,
                           ApplicationRepository applicationRepository,
                           QRVerificationRepository qrVerificationRepository,
                           CommissionPaymentRepository commissionPaymentRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.companyUserRepository = companyUserRepository;
        this.jobRepository = jobRepository;
        this.applicationRepository = applicationRepository;
        this.qrVerificationRepository = qrVerificationRepository;
        this.commissionPaymentRepository = commissionPaymentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        // Auto-seeding disabled — add your own test data manually
        log.info("Auto-seed disabled. Skipping.");
        return;
        /*
        if (userRepository.count() > 0) {
            log.info("Seed data already exists, skipping.");
            return;
        }
        log.info("Seeding FlexiWork database...");

        String pwd = passwordEncoder.encode("password123");

        // --- Companies ---
        Company c1 = makeCompany("ABC Garments Ltd", "BR001234", "0112345678",
            "info@abcgarments.lk", "No 12, Industrial Zone, Katunayake", pwd);
        Company c2 = makeCompany("Sri Lanka Foods Pvt Ltd", "BR005678", "0113456789",
            "hr@slfoods.lk", "No 45, Peliyagoda", pwd);
        Company c3 = makeCompany("Colombo Marketing Co", "BR009012", "0114567890",
            "admin@colombomarketing.lk", "No 78, Maradana, Colombo 10", pwd);
        Company c4 = makeCompany("Galle Restaurants Group", "BR003456", "0912345678",
            "ops@gallerestaurants.lk", "No 33, Galle Road, Galle", pwd);
        Company c5 = makeCompany("Kurunegala Exports Ltd", "BR007890", "0372345678",
            "contact@kurunegalaexports.lk", "No 5, Kurunegala Town", pwd);

        // --- Company Users ---
        saveCompanyUser(c1, "Saman Perera", "it@abcgarments.lk", pwd, CompanyRole.IT_ADMIN);
        saveCompanyUser(c1, "Nimal Silva", "gm@abcgarments.lk", pwd, CompanyRole.GM);
        saveCompanyUser(c2, "Kamala Gunasekara", "hr@slfoods.lk", pwd, CompanyRole.HR_MANAGER);
        saveCompanyUser(c3, "Roshan Fernando", "factory@colombomarketing.lk", pwd, CompanyRole.FACTORY_MANAGER);
        saveCompanyUser(c4, "Pradeep Jayawardana", "finance@gallerestaurants.lk", pwd, CompanyRole.FINANCE);

        // --- Workers ---
        // firstName, lastName, NIC, phone, email, gender, address, district
        String[][] wData = {
            {"Kamal","Perera","199001234567","0771234567","kamal@gmail.com","MALE","No 10, Gampaha","Gampaha"},
            {"Nimali","Silva","199512345678","0772345678","nimali@gmail.com","FEMALE","No 22, Negombo","Gampaha"},
            {"Rajan","Kumar","198903456789","0773456789","rajan@gmail.com","MALE","No 5, Colombo 6","Colombo"},
            {"Priya","Fernando","200104567890","0774567890","priya@gmail.com","FEMALE","No 8, Kalutara","Kalutara"},
            {"Dinesh","Wickrama","199705678901","0775678901","dinesh@gmail.com","MALE","No 15, Hambantota","Hambantota"},
            {"Chamila","Bandara","199806789012","0776789012","chamila@gmail.com","FEMALE","No 3, Kurunegala","Kurunegala"},
            {"Suresh","Nair","199007890123","0777890123","suresh@gmail.com","MALE","No 20, Colombo 6","Colombo"},
            {"Ayesha","Hameed","200208901234","0778901234","ayesha@gmail.com","FEMALE","No 12, Gampaha","Gampaha"},
            {"Ruwan","Jayasekara","199109012345","0779012345","ruwan@gmail.com","MALE","No 7, Galle","Galle"},
            {"Thilini","Madhavi","199910123456","0770123456","thilini@gmail.com","FEMALE","No 9, Colombo 3","Colombo"},
            {"Amara","Gunawardena","199211234567","0761234567","amara@gmail.com","MALE","No 4, Kalutara","Kalutara"},
            {"Sanduni","Rathnayake","200312345678","0762345678","sanduni@gmail.com","FEMALE","No 11, Kurunegala","Kurunegala"},
            {"Lahiru","Pathirana","199513456789","0763456789","lahiru@gmail.com","MALE","No 6, Hambantota","Hambantota"},
            {"Nadeesha","Kumari","199714567890","0764567890","nadeesha@gmail.com","FEMALE","No 18, Galle","Galle"},
            {"Chathura","Seneviratne","199015678901","0765678901","chathura@gmail.com","MALE","No 2, Gampaha","Gampaha"},
            {"Malsha","Wijesinghe","200116789012","0766789012","malsha@gmail.com","FEMALE","No 25, Colombo 10","Colombo"},
            {"Isuru","Dissanayake","199317890123","0767890123","isuru@gmail.com","MALE","No 14, Kalutara","Kalutara"},
            {"Hiruni","Samarasinghe","199618901234","0768901234","hiruni@gmail.com","FEMALE","No 30, Galle","Galle"},
            {"Nuwan","Rajapaksha","199119012345","0769012345","nuwan@gmail.com","MALE","No 1, Hambantota","Hambantota"},
            {"Sachini","Herath","200020123456","0760123456","sachini@gmail.com","FEMALE","No 16, Kurunegala","Kurunegala"}
        };

        User[] workers = new User[20];
        for (int i = 0; i < 20; i++) {
            User u = new User();
            u.setFirstName(wData[i][0]);
            u.setLastName(wData[i][1]);
            u.setNic(wData[i][2]);
            u.setPhone(wData[i][3]);
            u.setEmail(wData[i][4]);
            u.setGender(Gender.valueOf(wData[i][5]));
            u.setAddress(wData[i][6]);
            u.setDistrict(wData[i][7]);
            u.setPassword(pwd);
            u.setIsDeleted(false);
            workers[i] = userRepository.save(u);
        }

        // --- Jobs ---
        LocalDate today = LocalDate.now();
        Job j1 = makeJob(c1,"Garment Factory Worker","Sewing machine operator needed",1800,8,17,10,3,
            "Katunayake Industrial Zone","Gampaha",7.1697,79.8888,Gender.FEMALE,18,40,JobCategory.FACTORY,true,today.plusDays(2));
        Job j2 = makeJob(c1,"Quality Control Inspector","Inspect finished garments",2200,7,15,5,2,
            "Katunayake Industrial Zone","Gampaha",7.1697,79.8888,Gender.ANY,21,45,JobCategory.FACTORY,true,today.plusDays(3));
        Job j3 = makeJob(c2,"Food Processing Worker","Pack and label food products",1600,6,14,15,5,
            "Peliyagoda Industrial Area","Colombo",6.9601,79.8847,Gender.ANY,18,50,JobCategory.FACTORY,true,today.plusDays(1));
        Job j4 = makeJob(c2,"Restaurant Kitchen Helper","Assist chefs in preparation",1400,10,22,8,1,
            "Colombo 3","Colombo",6.8921,79.8520,Gender.MALE,20,35,JobCategory.RESTAURANT,true,today.plusDays(2));
        Job j5 = makeJob(c3,"Marketing Promoter","Distribute flyers in supermarkets",1500,9,17,20,8,
            "Colombo City","Colombo",6.9271,79.8612,Gender.ANY,18,30,JobCategory.MARKETING,true,today.plusDays(4));
        Job j6 = makeJob(c3,"Brand Ambassador","Represent brand at trade events",2500,8,18,6,2,
            "BMICH, Colombo 7","Colombo",6.9106,79.8644,Gender.FEMALE,20,35,JobCategory.MARKETING,true,today.plusDays(5));
        Job j7 = makeJob(c4,"Restaurant Waiter","Serve customers at Galle branch",1300,11,23,12,4,
            "Galle City","Galle",6.0353,80.2170,Gender.ANY,18,40,JobCategory.RESTAURANT,true,today.plusDays(1));
        Job j8 = makeJob(c4,"Kitchen Cleaner","Clean kitchen equipment after service",1200,22,6,4,0,
            "Galle City","Galle",6.0353,80.2170,Gender.MALE,20,45,JobCategory.RESTAURANT,true,today.plusDays(3));
        Job j9 = makeJob(c5,"Warehouse Loader","Load and unload goods at warehouse",1700,5,13,8,2,
            "Kurunegala Export Zone","Kurunegala",7.4818,80.3609,Gender.MALE,20,45,JobCategory.FACTORY,true,today.plusDays(2));
        Job j10 = makeJob(c5,"Packing Line Worker","Pack goods for export",1500,7,15,10,3,
            "Kurunegala Industrial Area","Kurunegala",7.4818,80.3609,Gender.ANY,18,50,JobCategory.FACTORY,true,today.plusDays(4));
        Job j11 = makeJob(c1,"Fabric Cutter","Cut fabric patterns for garments",1900,8,16,6,1,
            "Katunayake","Gampaha",7.1697,79.8888,Gender.ANY,20,45,JobCategory.FACTORY,true,today.plusDays(3));
        Job j12 = makeJob(c4,"Event Server","Serve at private events and catering",1600,14,22,10,0,
            "Hambantota Hotel Zone","Hambantota",6.1241,81.1185,Gender.ANY,18,40,JobCategory.RESTAURANT,true,today.plusDays(5));
        Job j13 = makeJob(c3,"Sales Promoter","Promote products at supermarkets in Kalutara",1500,9,17,15,5,
            "Kalutara Town","Kalutara",6.5854,79.9607,Gender.ANY,18,35,JobCategory.MARKETING,true,today.plusDays(2));

        // --- Applications ---
        saveApplication(workers[0], j1, ApplicationStatus.APPROVED);
        saveApplication(workers[1], j1, ApplicationStatus.APPROVED);
        saveApplication(workers[2], j3, ApplicationStatus.APPROVED);
        saveApplication(workers[3], j5, ApplicationStatus.APPROVED);
        saveApplication(workers[4], j12, ApplicationStatus.APPROVED);
        saveApplication(workers[5], j9, ApplicationStatus.APPROVED);
        saveApplication(workers[8], j7, ApplicationStatus.APPROVED);
        saveApplication(workers[9], j5, ApplicationStatus.APPROVED);

        // --- QR Verifications ---
        saveQR(workers[0], j1, "token-kamal-j1", ScanType.CHECK_IN,
            LocalDateTime.now().minusHours(5), null, true);
        saveQR(workers[8], j7, "token-ruwan-j7", ScanType.CHECK_OUT,
            LocalDateTime.now().minusHours(10), LocalDateTime.now().minusHours(2), true);

        // --- Commission Payments ---
        saveCommission(c1, j1, 1800.00, 180.00, PaymentStatus.PENDING, null);
        saveCommission(c4, j7, 1300.00, 130.00, PaymentStatus.PAID, LocalDateTime.now().minusDays(1));

        log.info("Database seeded: 5 companies, 20 workers, 13 jobs, 8 applications.");
        */
    }

    private Company makeCompany(String name, String brNumber, String phone, String email,
                                 String address, String pwd) {
        Company c = new Company();
        c.setName(name); c.setBrNumber(brNumber); c.setPhone(phone);
        c.setEmail(email); c.setAddress(address); c.setPassword(pwd); c.setIsDeleted(false);
        return companyRepository.save(c);
    }

    private void saveCompanyUser(Company company, String name, String email, String pwd, CompanyRole role) {
        CompanyUser cu = new CompanyUser();
        cu.setCompany(company); cu.setName(name); cu.setEmail(email);
        cu.setPassword(pwd); cu.setRole(role);
        companyUserRepository.save(cu);
    }

    private Job makeJob(Company company, String title, String desc, double wage,
                        int startH, int endH, int required, int approved,
                        String location, String district, double lat, double lng,
                        Gender gender, int minAge, int maxAge, JobCategory cat,
                        boolean active, LocalDate date) {
        Job j = new Job();
        j.setCompany(company); j.setTitle(title); j.setDescription(desc);
        j.setDailyWage(BigDecimal.valueOf(wage));
        j.setShiftStartTime(LocalTime.of(startH, 0));
        j.setShiftEndTime(LocalTime.of(endH % 24, 0));
        j.setRequiredWorkers(required); j.setApprovedWorkers(approved);
        j.setFactoryLocation(location); j.setDistrict(district);
        j.setLatitude(lat); j.setLongitude(lng);
        j.setGender(gender); j.setMinAge(minAge); j.setMaxAge(maxAge);
        j.setCategory(cat); j.setIsActive(active); j.setIsDeleted(false);
        j.setShiftDate(date);
        return jobRepository.save(j);
    }

    private void saveApplication(User user, Job job, ApplicationStatus status) {
        Application a = new Application();
        a.setUser(user); a.setJob(job); a.setStatus(status);
        applicationRepository.save(a);
    }

    private void saveQR(User user, Job job, String token, ScanType scanType,
                        LocalDateTime checkIn, LocalDateTime checkOut, boolean verified) {
        QRVerification qr = new QRVerification();
        qr.setUser(user); qr.setJob(job); qr.setQrToken(token);
        qr.setScanType(scanType); qr.setCheckInTime(checkIn);
        qr.setCheckOutTime(checkOut); qr.setIsVerified(verified);
        qrVerificationRepository.save(qr);
    }

    private void saveCommission(Company company, Job job, double wage, double commission,
                                PaymentStatus status, LocalDateTime paidAt) {
        CommissionPayment cp = new CommissionPayment();
        cp.setCompany(company); cp.setJob(job);
        cp.setWorkerWage(BigDecimal.valueOf(wage));
        cp.setCommissionAmount(BigDecimal.valueOf(commission));
        cp.setStatus(status); cp.setPaidAt(paidAt);
        commissionPaymentRepository.save(cp);
    }
}
