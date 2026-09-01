package com.pamir.ppfarmsbackend.shared.seed;

import com.pamir.ppfarmsbackend.accounting.entity.Expense;
import com.pamir.ppfarmsbackend.accounting.entity.Income;
import com.pamir.ppfarmsbackend.accounting.enums.ExpenseCategory;
import com.pamir.ppfarmsbackend.accounting.enums.IncomeCategory;
import com.pamir.ppfarmsbackend.accounting.repository.ExpenseRepository;
import com.pamir.ppfarmsbackend.accounting.repository.IncomeRepository;
import com.pamir.ppfarmsbackend.billing.entity.Payment;
import com.pamir.ppfarmsbackend.billing.entity.Plan;
import com.pamir.ppfarmsbackend.billing.entity.Subscription;
import com.pamir.ppfarmsbackend.billing.repository.PaymentRepository;
import com.pamir.ppfarmsbackend.billing.repository.PlanRepository;
import com.pamir.ppfarmsbackend.billing.repository.SubscriptionRepository;
import com.pamir.ppfarmsbackend.feed.entity.FeedConsumptionLog;
import com.pamir.ppfarmsbackend.feed.entity.FeedInventory;
import com.pamir.ppfarmsbackend.feed.repository.FeedConsumptionLogRepository;
import com.pamir.ppfarmsbackend.feed.repository.FeedInventoryRepository;
import com.pamir.ppfarmsbackend.flock.entity.EggProductionLog;
import com.pamir.ppfarmsbackend.flock.entity.FlockBatch;
import com.pamir.ppfarmsbackend.flock.entity.FlockMortalityLog;
import com.pamir.ppfarmsbackend.flock.repository.EggProductionLogRepository;
import com.pamir.ppfarmsbackend.flock.repository.FlockBatchRepository;
import com.pamir.ppfarmsbackend.flock.repository.FlockMortalityLogRepository;
import com.pamir.ppfarmsbackend.health.entity.DewormingRecord;
import com.pamir.ppfarmsbackend.health.entity.HealthRecord;
import com.pamir.ppfarmsbackend.health.entity.MortalityRecord;
import com.pamir.ppfarmsbackend.health.entity.VaccinationRecord;
import com.pamir.ppfarmsbackend.health.repository.DewormingRepository;
import com.pamir.ppfarmsbackend.health.repository.HealthRepository;
import com.pamir.ppfarmsbackend.health.repository.MortalityRepository;
import com.pamir.ppfarmsbackend.health.repository.VaccinationRepository;
import com.pamir.ppfarmsbackend.herd.domain.AnimalGender;
import com.pamir.ppfarmsbackend.herd.domain.AnimalStatus;
import com.pamir.ppfarmsbackend.herd.entity.*;
import com.pamir.ppfarmsbackend.herd.repository.*;
import com.pamir.ppfarmsbackend.identity.entity.Organization;
import com.pamir.ppfarmsbackend.identity.entity.Role;
import com.pamir.ppfarmsbackend.identity.entity.User;
import com.pamir.ppfarmsbackend.identity.repository.OrganizationRepository;
import com.pamir.ppfarmsbackend.identity.repository.RoleRepository;
import com.pamir.ppfarmsbackend.identity.repository.UserRepository;
import com.pamir.ppfarmsbackend.production.entity.ProductionRecord;
import com.pamir.ppfarmsbackend.production.repository.ProductionRecordRepository;
import com.pamir.ppfarmsbackend.purchases.entity.Purchase;
import com.pamir.ppfarmsbackend.purchases.entity.PurchaseItem;
import com.pamir.ppfarmsbackend.purchases.repository.PurchaseItemRepository;
import com.pamir.ppfarmsbackend.purchases.repository.PurchaseRepository;
import com.pamir.ppfarmsbackend.reproduction.entity.*;
import com.pamir.ppfarmsbackend.reproduction.repository.*;
import com.pamir.ppfarmsbackend.sales.entity.Sale;
import com.pamir.ppfarmsbackend.sales.entity.SaleItem;
import com.pamir.ppfarmsbackend.sales.repository.SaleItemRepository;
import com.pamir.ppfarmsbackend.sales.repository.SaleRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.*;

@Component
@Order(2)
@Profile({"demo-manual"})
@RequiredArgsConstructor
public class DemoDataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DemoDataSeeder.class);

    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PlanRepository planRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final PaymentRepository paymentRepository;
    private final SpeciesRepository speciesRepository;
    private final BreedRepository breedRepository;
    private final ShedPenRepository shedPenRepository;
    private final AnimalRepository animalRepository;
    private final WeightRepository weightRepository;
    private final HealthRepository healthRepository;
    private final VaccinationRepository vaccinationRepository;
    private final DewormingRepository dewormingRepository;
    private final MortalityRepository mortalityRepository;
    private final HeatCycleRepository heatCycleRepository;
    private final BreedingRepository breedingRepository;
    private final PregnancyRepository pregnancyRepository;
    private final BirthRecordRepository birthRecordRepository;
    private final BirthOffspringRepository birthOffspringRepository;
    private final FlockBatchRepository flockBatchRepository;
    private final FlockMortalityLogRepository flockMortalityLogRepository;
    private final EggProductionLogRepository eggProductionLogRepository;
    private final ProductionRecordRepository productionRecordRepository;
    private final FeedInventoryRepository feedInventoryRepository;
    private final FeedConsumptionLogRepository feedConsumptionLogRepository;
    private final SaleRepository saleRepository;
    private final SaleItemRepository saleItemRepository;
    private final PurchaseRepository purchaseRepository;
    private final PurchaseItemRepository purchaseItemRepository;
    private final IncomeRepository incomeRepository;
    private final ExpenseRepository expenseRepository;
    private final PasswordEncoder passwordEncoder;
    private final com.pamir.ppfarmsbackend.tasks.repository.TaskRepository taskRepository;
    private final com.pamir.ppfarmsbackend.crm.repository.SupplierRepository supplierRepository;
    private final com.pamir.ppfarmsbackend.crm.repository.CustomerRepository customerRepository;
    private final com.pamir.ppfarmsbackend.crops.repository.CropPlotRepository cropPlotRepository;
    private final com.pamir.ppfarmsbackend.crops.repository.CropHarvestLogRepository cropHarvestLogRepository;
    private final com.pamir.ppfarmsbackend.documents.repository.DocumentAttachmentRepository documentAttachmentRepository;

    @Override
    @Transactional
    public void run(String... args) {
        String demoEmail = "owner@greenvalleyfarms.com";
        if (organizationRepository.existsByEmail(demoEmail)) {
            log.info("[DEMO SEEDER] Demo farm data already exists. Skipping initialization.");
            return;
        }

        log.info("[DEMO SEEDER] Initializing full commercial farm demo dataset across all tables...");

        // 1. DEMO FARM ORGANIZATION
        Organization farm = Organization.builder()
                .name("Green Valley Commercial Dairy & Goat Farm")
                .email(demoEmail)
                .phone("+91 98765 43210")
                .address("Plot 42, Agro Industrial Corridor, Pune, Maharashtra 411038")
                .currency("INR")
                .status("ACTIVE")
                .build();
        farm = organizationRepository.save(farm);
        UUID orgId = farm.getId();

        // 2. USER ACCOUNTS ACROSS ALL ROLES
        Role adminRole = roleRepository.findByName("ADMIN").orElseThrow();
        Role vetRole = roleRepository.findByName("VET").orElseThrow();
        Role workerRole = roleRepository.findByName("WORKER").orElseThrow();
        String commonPassword = passwordEncoder.encode("Password123!");

        User owner = User.builder()
                .organizationId(orgId)
                .role(adminRole)
                .name("Rajesh Sharma")
                .email(demoEmail)
                .phone("+91 98765 43210")
                .passwordHash(commonPassword)
                .status("ACTIVE")
                .build();
        userRepository.save(owner);

        User vet = User.builder()
                .organizationId(orgId)
                .role(vetRole)
                .name("Dr. Priya Kulkarni (B.V.Sc)")
                .email("vet@greenvalleyfarms.com")
                .phone("+91 98765 43211")
                .passwordHash(commonPassword)
                .status("ACTIVE")
                .build();
        userRepository.save(vet);

        User worker = User.builder()
                .organizationId(orgId)
                .role(workerRole)
                .name("Ramesh Patil")
                .email("worker@greenvalleyfarms.com")
                .phone("+91 98765 43212")
                .passwordHash(commonPassword)
                .status("ACTIVE")
                .build();
        userRepository.save(worker);

        // 3. SAAS SUBSCRIPTION & PENDING PAYMENT PROOF
        Plan proPlan = planRepository.findAll().stream()
                .filter(p -> p.getPrice() != null && p.getPrice().compareTo(BigDecimal.valueOf(1000)) > 0)
                .findFirst()
                .orElse(planRepository.findAll().get(0));

        Subscription sub = Subscription.builder()
                .organizationId(orgId)
                .plan(proPlan)
                .status("ACTIVE")
                .startDate(LocalDate.now().minusDays(10))
                .endDate(LocalDate.now().plusDays(50))
                .autoRenew(true)
                .build();
        sub = subscriptionRepository.save(sub);

        Payment demoPayment = Payment.builder()
                .organizationId(orgId)
                .subscriptionId(sub.getId())
                .amount(proPlan.getPrice())
                .paymentMethod("UPI_QR")
                .transactionRef("UPI-UTR-98234120912")
                .receiptImageUrl("https://ivxbzisrqzlsdlokppke.supabase.co/storage/v1/object/public/payment-proofs/demo_receipt.webp")
                .status("APPROVED")
                .verifiedBy(owner.getId())
                .verifiedAt(OffsetDateTime.now().minusDays(10))
                .build();
        paymentRepository.save(demoPayment);

        // 4. SPECIES & BREEDS
        Species goatSpecies = getOrCreateSpecies("Goat", 150, 21, true, false);
        Species cowSpecies = getOrCreateSpecies("Cattle", 283, 21, true, false);
        Species sheepSpecies = getOrCreateSpecies("Sheep", 147, 17, true, true);
        Species poultrySpecies = getOrCreateSpecies("Poultry", 21, 0, false, false);

        Breed beetal = breedRepository.save(Breed.builder().organizationId(orgId).species(goatSpecies).name("Beetal").description("Dual purpose goat breed from Punjab").build());
        Breed sirohi = breedRepository.save(Breed.builder().organizationId(orgId).species(goatSpecies).name("Sirohi").description("Hardy stall-fed goat breed from Rajasthan").build());
        Breed barbari = breedRepository.save(Breed.builder().organizationId(orgId).species(goatSpecies).name("Barbari").description("Prolific dairy goat breed").build());
        Breed jamnapari = breedRepository.save(Breed.builder().organizationId(orgId).species(goatSpecies).name("Jamnapari").description("Large dual purpose goat breed").build());

        Breed gir = breedRepository.save(Breed.builder().organizationId(orgId).species(cowSpecies).name("Gir").description("Famous indigenous A2 dairy cattle breed").build());
        Breed hf = breedRepository.save(Breed.builder().organizationId(orgId).species(cowSpecies).name("Holstein Friesian").description("World high-yield dairy cattle breed").build());
        Breed sahiwal = breedRepository.save(Breed.builder().organizationId(orgId).species(cowSpecies).name("Sahiwal").description("High butterfat indigenous dairy cattle").build());
        Breed jersey = breedRepository.save(Breed.builder().organizationId(orgId).species(cowSpecies).name("Jersey").description("High butterfat, heat tolerant dairy breed").build());

        Breed nellore = breedRepository.save(Breed.builder().organizationId(orgId).species(sheepSpecies).name("Nellore").description("Tall Indian mutton sheep breed").build());
        Breed dorper = breedRepository.save(Breed.builder().organizationId(orgId).species(sheepSpecies).name("Dorper").description("Fast growing meat sheep breed").build());

        // 5. HOUSING SHEDS & PENS
        ShedPen shedMilking = shedPenRepository.save(ShedPen.builder().organizationId(orgId).name("Shed A - Cattle Milking Parlor").penType("MILKING_PARLOR").capacity(30).build());
        ShedPen shedGoatBarn = shedPenRepository.save(ShedPen.builder().organizationId(orgId).name("Shed B - Goat Stall-Fed Barn").penType("GOAT_BARN").capacity(40).build());
        ShedPen shedMaternity = shedPenRepository.save(ShedPen.builder().organizationId(orgId).name("Shed C - Maternity & Nursery Pen").penType("MATERNITY_PEN").capacity(20).build());
        ShedPen shedBullPen = shedPenRepository.save(ShedPen.builder().organizationId(orgId).name("Shed D - Breeding Bull & Buck Pen").penType("BUCK_PEN").capacity(15).build());
        ShedPen shedPoultry = shedPenRepository.save(ShedPen.builder().organizationId(orgId).name("Shed E - Flock & Layer House").penType("POULTRY_SHED").capacity(1000).build());

        // 6. LIVESTOCK SEEDING: 10 GOATS
        Animal sultanBuck = animalRepository.save(Animal.builder()
                .organizationId(orgId).species(goatSpecies).breed(beetal).shedPen(shedBullPen)
                .tagNumber("BTL-SIRE-01").name("Sultan (Champion Sire)").gender(AnimalGender.MALE).status(AnimalStatus.ACTIVE)
                .dateOfBirth(LocalDate.now().minusYears(2).minusMonths(6)).birthWeight(BigDecimal.valueOf(4.2))
                .photoUrl("https://images.unsplash.com/photo-1524024973431-2ad916746881?w=600")
                .build());

        Animal gangaDoe = animalRepository.save(Animal.builder()
                .organizationId(orgId).species(goatSpecies).breed(sirohi).shedPen(shedGoatBarn)
                .tagNumber("SRH-DOE-101").name("Ganga (Lactating & Pregnant)").gender(AnimalGender.FEMALE).status(AnimalStatus.ACTIVE)
                .dateOfBirth(LocalDate.now().minusYears(2)).birthWeight(BigDecimal.valueOf(3.5))
                .sire(sultanBuck).photoUrl("https://images.unsplash.com/photo-1560807707-8cc77767d783?w=600")
                .build());

        Animal yamunaDoe = animalRepository.save(Animal.builder()
                .organizationId(orgId).species(goatSpecies).breed(barbari).shedPen(shedGoatBarn)
                .tagNumber("BAR-DOE-102").name("Yamuna").gender(AnimalGender.FEMALE).status(AnimalStatus.ACTIVE)
                .dateOfBirth(LocalDate.now().minusYears(1).minusMonths(8)).birthWeight(BigDecimal.valueOf(3.0))
                .build());

        Animal kaveriDoe = animalRepository.save(Animal.builder()
                .organizationId(orgId).species(goatSpecies).breed(jamnapari).shedPen(shedGoatBarn)
                .tagNumber("JMN-DOE-103").name("Kaveri").gender(AnimalGender.FEMALE).status(AnimalStatus.ACTIVE)
                .dateOfBirth(LocalDate.now().minusYears(1).minusMonths(10)).birthWeight(BigDecimal.valueOf(3.6))
                .build());

        Animal narmadaDoe = animalRepository.save(Animal.builder()
                .organizationId(orgId).species(goatSpecies).breed(sirohi).shedPen(shedGoatBarn)
                .tagNumber("SRH-DOE-104").name("Narmada").gender(AnimalGender.FEMALE).status(AnimalStatus.ACTIVE)
                .dateOfBirth(LocalDate.now().minusYears(1).minusMonths(6)).birthWeight(BigDecimal.valueOf(3.2))
                .build());

        Animal saraswatiDoe = animalRepository.save(Animal.builder()
                .organizationId(orgId).species(goatSpecies).breed(beetal).shedPen(shedGoatBarn)
                .tagNumber("BTL-DOE-105").name("Saraswati").gender(AnimalGender.FEMALE).status(AnimalStatus.ACTIVE)
                .dateOfBirth(LocalDate.now().minusYears(1).minusMonths(4)).birthWeight(BigDecimal.valueOf(3.4))
                .build());

        Animal godavariDoe = animalRepository.save(Animal.builder()
                .organizationId(orgId).species(goatSpecies).breed(barbari).shedPen(shedGoatBarn)
                .tagNumber("BAR-DOE-106").name("Godavari").gender(AnimalGender.FEMALE).status(AnimalStatus.ACTIVE)
                .dateOfBirth(LocalDate.now().minusYears(1).minusMonths(2)).birthWeight(BigDecimal.valueOf(2.8))
                .build());

        Animal taptiDoe = animalRepository.save(Animal.builder()
                .organizationId(orgId).species(goatSpecies).breed(jamnapari).shedPen(shedGoatBarn)
                .tagNumber("JMN-DOE-107").name("Tapti").gender(AnimalGender.FEMALE).status(AnimalStatus.ACTIVE)
                .dateOfBirth(LocalDate.now().minusYears(1)).birthWeight(BigDecimal.valueOf(3.3))
                .build());

        Animal chotuKid = animalRepository.save(Animal.builder()
                .organizationId(orgId).species(goatSpecies).breed(sirohi).shedPen(shedMaternity)
                .tagNumber("SRH-KID-201").name("Chotu (Twin 1)").gender(AnimalGender.MALE).status(AnimalStatus.ACTIVE)
                .dateOfBirth(LocalDate.now().minusMonths(3)).birthWeight(BigDecimal.valueOf(2.8))
                .sire(sultanBuck).dam(gangaDoe)
                .build());

        Animal munniKid = animalRepository.save(Animal.builder()
                .organizationId(orgId).species(goatSpecies).breed(sirohi).shedPen(shedMaternity)
                .tagNumber("SRH-KID-202").name("Munni (Twin 2)").gender(AnimalGender.FEMALE).status(AnimalStatus.ACTIVE)
                .dateOfBirth(LocalDate.now().minusMonths(3)).birthWeight(BigDecimal.valueOf(2.6))
                .sire(sultanBuck).dam(gangaDoe)
                .build());

        // 7. LIVESTOCK SEEDING: 10 COWS / CATTLE
        Animal nandiBull = animalRepository.save(Animal.builder()
                .organizationId(orgId).species(cowSpecies).breed(gir).shedPen(shedBullPen)
                .tagNumber("GIR-BULL-01").name("Nandi (Vedic Gir Bull)").gender(AnimalGender.MALE).status(AnimalStatus.ACTIVE)
                .dateOfBirth(LocalDate.now().minusYears(3).minusMonths(2)).birthWeight(BigDecimal.valueOf(28.0))
                .photoUrl("https://images.unsplash.com/photo-1546445317-29f4545e9d53?w=600")
                .build());

        Animal kamadhenuCow = animalRepository.save(Animal.builder()
                .organizationId(orgId).species(cowSpecies).breed(hf).shedPen(shedMilking)
                .tagNumber("HF-COW-101").name("Kamadhenu (28L Elite)").gender(AnimalGender.FEMALE).status(AnimalStatus.ACTIVE)
                .dateOfBirth(LocalDate.now().minusYears(3)).birthWeight(BigDecimal.valueOf(32.0))
                .photoUrl("https://images.unsplash.com/photo-1570042225831-d98fa7577f1e?w=600")
                .build());

        Animal nandiniCow = animalRepository.save(Animal.builder()
                .organizationId(orgId).species(cowSpecies).breed(gir).shedPen(shedMilking)
                .tagNumber("GIR-COW-102").name("Nandini (Pure A2)").gender(AnimalGender.FEMALE).status(AnimalStatus.ACTIVE)
                .dateOfBirth(LocalDate.now().minusYears(2).minusMonths(8)).birthWeight(BigDecimal.valueOf(26.5))
                .sire(nandiBull)
                .photoUrl("https://images.unsplash.com/photo-1596733430284-f7437764b1a9?w=600")
                .build());

        Animal lakshmiCow = animalRepository.save(Animal.builder()
                .organizationId(orgId).species(cowSpecies).breed(sahiwal).shedPen(shedMilking)
                .tagNumber("SHW-COW-103").name("Lakshmi").gender(AnimalGender.FEMALE).status(AnimalStatus.ACTIVE)
                .dateOfBirth(LocalDate.now().minusYears(2).minusMonths(6)).birthWeight(BigDecimal.valueOf(25.0))
                .build());

        Animal daisyCow = animalRepository.save(Animal.builder()
                .organizationId(orgId).species(cowSpecies).breed(jersey).shedPen(shedMilking)
                .tagNumber("JRS-COW-104").name("Daisy (High-Fat)").gender(AnimalGender.FEMALE).status(AnimalStatus.ACTIVE)
                .dateOfBirth(LocalDate.now().minusYears(2).minusMonths(4)).birthWeight(BigDecimal.valueOf(24.0))
                .build());

        Animal radhaCow = animalRepository.save(Animal.builder()
                .organizationId(orgId).species(cowSpecies).breed(hf).shedPen(shedMilking)
                .tagNumber("HF-COW-105").name("Radha").gender(AnimalGender.FEMALE).status(AnimalStatus.ACTIVE)
                .dateOfBirth(LocalDate.now().minusYears(2).minusMonths(2)).birthWeight(BigDecimal.valueOf(30.0))
                .build());

        Animal gauriCow = animalRepository.save(Animal.builder()
                .organizationId(orgId).species(cowSpecies).breed(gir).shedPen(shedMilking)
                .tagNumber("GIR-COW-106").name("Gauri (Pregnant Heifer)").gender(AnimalGender.FEMALE).status(AnimalStatus.ACTIVE)
                .dateOfBirth(LocalDate.now().minusYears(1).minusMonths(11)).birthWeight(BigDecimal.valueOf(26.0))
                .build());

        Animal annapurnaCow = animalRepository.save(Animal.builder()
                .organizationId(orgId).species(cowSpecies).breed(sahiwal).shedPen(shedMilking)
                .tagNumber("SHW-COW-107").name("Annapurna").gender(AnimalGender.FEMALE).status(AnimalStatus.ACTIVE)
                .dateOfBirth(LocalDate.now().minusYears(1).minusMonths(9)).birthWeight(BigDecimal.valueOf(25.5))
                .build());

        Animal krishnaCalf = animalRepository.save(Animal.builder()
                .organizationId(orgId).species(cowSpecies).breed(gir).shedPen(shedMaternity)
                .tagNumber("GIR-CALF-201").name("Krishna (Bull Calf)").gender(AnimalGender.MALE).status(AnimalStatus.ACTIVE)
                .dateOfBirth(LocalDate.now().minusMonths(4)).birthWeight(BigDecimal.valueOf(27.0))
                .sire(nandiBull).dam(nandiniCow)
                .build());

        Animal bellaCalf = animalRepository.save(Animal.builder()
                .organizationId(orgId).species(cowSpecies).breed(hf).shedPen(shedMaternity)
                .tagNumber("HF-CALF-202").name("Bella (Heifer Calf)").gender(AnimalGender.FEMALE).status(AnimalStatus.ACTIVE)
                .dateOfBirth(LocalDate.now().minusMonths(2)).birthWeight(BigDecimal.valueOf(31.0))
                .dam(kamadhenuCow)
                .build());

        // 8. LIVESTOCK SEEDING: 2 SHEEP
        animalRepository.save(Animal.builder()
                .organizationId(orgId).species(sheepSpecies).breed(nellore).shedPen(shedGoatBarn)
                .tagNumber("NEL-RAM-01").name("Bhima (Nellore Stud)").gender(AnimalGender.MALE).status(AnimalStatus.ACTIVE)
                .dateOfBirth(LocalDate.now().minusYears(1).minusMonths(6)).birthWeight(BigDecimal.valueOf(4.0))
                .build());

        animalRepository.save(Animal.builder()
                .organizationId(orgId).species(sheepSpecies).breed(dorper).shedPen(shedGoatBarn)
                .tagNumber("DOR-EWE-01").name("Maya (Dorper Ewe)").gender(AnimalGender.FEMALE).status(AnimalStatus.ACTIVE)
                .dateOfBirth(LocalDate.now().minusYears(1).minusMonths(4)).birthWeight(BigDecimal.valueOf(3.8))
                .build());

        // 9. POULTRY & FLOCK BATCH (500 BIRDS)
        FlockBatch flockBatch = flockBatchRepository.save(FlockBatch.builder()
                .organizationId(orgId)
                .batchName("Batch 2026-FL01 (Broiler & Layer Hybrid)")
                .species(poultrySpecies)
                .shedPen(shedPoultry)
                .initialQuantity(500)
                .currentQuantity(488)
                .arrivalDate(LocalDate.now().minusDays(45))
                .initialAgeWeeks(1)
                .purpose("EGGS")
                .status("ACTIVE")
                .purchaseCost(BigDecimal.valueOf(32500.0))
                .notes("Commercial layer flock for daily organic brown egg production.")
                .build());

        // Flock Mortality Logs
        flockMortalityLogRepository.save(FlockMortalityLog.builder()
                .organizationId(orgId).flockBatch(flockBatch).deadCount(4)
                .logDate(LocalDate.now().minusDays(12)).causeOfDeath("Heat Stress / Dehydration")
                .notes("Added electrolyte supplement to nipple drinking lines.").build());
        flockMortalityLogRepository.save(FlockMortalityLog.builder()
                .organizationId(orgId).flockBatch(flockBatch).deadCount(2)
                .logDate(LocalDate.now().minusDays(3)).causeOfDeath("Natural Mortality")
                .notes("Normal daily inspection.").build());

        // 7 Days of Egg Production Logs
        for (int i = 6; i >= 0; i--) {
            LocalDate eggDate = LocalDate.now().minusDays(i);
            int eggs = 380 + (i * 3) % 15;
            eggProductionLogRepository.save(EggProductionLog.builder()
                    .organizationId(orgId).flockBatch(flockBatch).collectionDate(eggDate)
                    .totalEggs(eggs).brokenEggs(3 + (i % 2))
                    .traysCount(eggs / 30).notes("Morning collection: Grade A clean eggs")
                    .build());
        }

        // 10. WEIGHT RECORDS (GROWTH & ADG MONITORING)
        weightRepository.save(WeightRecord.builder().organizationId(orgId).animalId(sultanBuck.getId()).weightKg(BigDecimal.valueOf(74.5)).measuredAt(LocalDate.now().minusDays(7)).notes("Prime sire body condition").build());
        weightRepository.save(WeightRecord.builder().organizationId(orgId).animalId(gangaDoe.getId()).weightKg(BigDecimal.valueOf(45.0)).measuredAt(LocalDate.now().minusDays(7)).notes("Good gestational weight").build());
        weightRepository.save(WeightRecord.builder().organizationId(orgId).animalId(nandiBull.getId()).weightKg(BigDecimal.valueOf(620.0)).measuredAt(LocalDate.now().minusDays(15)).notes("Vedic bull weight").build());
        weightRepository.save(WeightRecord.builder().organizationId(orgId).animalId(kamadhenuCow.getId()).weightKg(BigDecimal.valueOf(540.0)).measuredAt(LocalDate.now().minusDays(15)).notes("Peak lactation body score 3.5").build());
        weightRepository.save(WeightRecord.builder().organizationId(orgId).animalId(krishnaCalf.getId()).weightKg(BigDecimal.valueOf(78.0)).measuredAt(LocalDate.now().minusDays(5)).notes("Excellent ADG: 450g/day").build());

        // 11. 7 DAYS OF MILK PRODUCTION (MORNING & EVENING SESSIONS)
        for (int i = 6; i >= 0; i--) {
            LocalDate d = LocalDate.now().minusDays(i);
            // High-Yield HF Cow (Kamadhenu: ~26L/day)
            productionRecordRepository.save(ProductionRecord.builder()
                    .organizationId(orgId).animalId(kamadhenuCow.getId()).shedPenId(shedMilking.getId())
                    .productionType("MILK_MORNING").quantity(BigDecimal.valueOf(14.2 + (i % 3) * 0.3)).unit("LITERS")
                    .fatPercentage(BigDecimal.valueOf(3.8)).snfPercentage(BigDecimal.valueOf(8.5)).recordedDate(d).notes("Machine milking")
                    .build());
            productionRecordRepository.save(ProductionRecord.builder()
                    .organizationId(orgId).animalId(kamadhenuCow.getId()).shedPenId(shedMilking.getId())
                    .productionType("MILK_EVENING").quantity(BigDecimal.valueOf(12.0 + (i % 2) * 0.2)).unit("LITERS")
                    .fatPercentage(BigDecimal.valueOf(4.0)).snfPercentage(BigDecimal.valueOf(8.6)).recordedDate(d).notes("Machine milking")
                    .build());

            // Pure Gir Cow (Nandini: ~14L/day A2)
            productionRecordRepository.save(ProductionRecord.builder()
                    .organizationId(orgId).animalId(nandiniCow.getId()).shedPenId(shedMilking.getId())
                    .productionType("MILK_MORNING").quantity(BigDecimal.valueOf(7.5 + (i % 2) * 0.2)).unit("LITERS")
                    .fatPercentage(BigDecimal.valueOf(4.6)).snfPercentage(BigDecimal.valueOf(8.9)).recordedDate(d).notes("Pure A2 Desi milk")
                    .build());
            productionRecordRepository.save(ProductionRecord.builder()
                    .organizationId(orgId).animalId(nandiniCow.getId()).shedPenId(shedMilking.getId())
                    .productionType("MILK_EVENING").quantity(BigDecimal.valueOf(6.8 + (i % 3) * 0.1)).unit("LITERS")
                    .fatPercentage(BigDecimal.valueOf(4.8)).snfPercentage(BigDecimal.valueOf(9.0)).recordedDate(d).notes("Pure A2 Desi milk")
                    .build());

            // Sirohi Doe (Ganga: ~3.2L/day goat milk)
            productionRecordRepository.save(ProductionRecord.builder()
                    .organizationId(orgId).animalId(gangaDoe.getId()).shedPenId(shedGoatBarn.getId())
                    .productionType("MILK_MORNING").quantity(BigDecimal.valueOf(1.8 + (i % 2) * 0.1)).unit("LITERS")
                    .fatPercentage(BigDecimal.valueOf(4.2)).snfPercentage(BigDecimal.valueOf(8.8)).recordedDate(d).notes("Manual milking")
                    .build());
        }

        // 12. HEALTH, VACCINATION, DEWORMING & MORTALITY LOGS
        healthRepository.save(HealthRecord.builder()
                .organizationId(orgId).animalId(kamadhenuCow.getId())
                .symptoms("Mild swelling in left hind quarter, clot in initial milk stream")
                .treatment("Intramammary antibiotic infusion (Cephapirin) & anti-inflammatory meloxicam")
                .healthStatus("UNDER_TREATMENT").vetName("Dr. Priya Kulkarni")
                .treatmentDate(LocalDate.now().minusDays(2)).followUpDate(LocalDate.now().plusDays(2))
                .milkWithdrawalUntilDate(LocalDate.now().plusDays(4))
                .slaughterWithdrawalUntilDate(LocalDate.now().plusDays(14))
                .build());

        vaccinationRepository.save(VaccinationRecord.builder()
                .organizationId(orgId).animalId(nandiniCow.getId())
                .vaccineName("FMD (Foot and Mouth Disease) Oil Adjuvant Vaccine")
                .batchNumber("FMD-2026-V4").dosage("2 ml Deep I.M.")
                .status("COMPLETED").administeredAt(LocalDate.now().minusMonths(1))
                .nextDueDate(LocalDate.now().plusMonths(5))
                .build());

        vaccinationRepository.save(VaccinationRecord.builder()
                .organizationId(orgId).animalId(sultanBuck.getId())
                .vaccineName("PPR (Peste des Petits Ruminants) Vaccine")
                .batchNumber("PPR-9921").dosage("1 ml S.C.")
                .status("COMPLETED").administeredAt(LocalDate.now().minusMonths(2))
                .nextDueDate(LocalDate.now().plusMonths(10))
                .build());

        dewormingRepository.save(DewormingRecord.builder()
                .organizationId(orgId).animalId(gangaDoe.getId())
                .drugName("Albendazole 2.5% Oral Suspension").drugType("BENZIMIDAZOLE").dosage("15 ml oral drench")
                .administeredAt(LocalDate.now().minusMonths(1)).nextDueDate(LocalDate.now().plusMonths(2))
                .build());

        mortalityRepository.save(MortalityRecord.builder()
                .organizationId(orgId).animalId(taptiDoe.getId())
                .deathDate(LocalDate.now().minusMonths(3))
                .causeOfDeath("Old Age & Chronic Enteritis")
                .necropsyNotes("Severe dehydration and intestinal inflammation; no contagious disease detected.")
                .disposalMethod("DEEP_BURIAL_WITH_LIME")
                .build());

        // 13. REPRODUCTION: HEAT CYCLES, BREEDING & PREGNANCIES
        heatCycleRepository.save(HeatCycle.builder()
                .organizationId(orgId).animalId(godavariDoe.getId())
                .observedAt(LocalDate.now().minusDays(5))
                .expectedNextHeat(LocalDate.now().plusDays(16))
                .status("OBSERVED")
                .build());

        // Current Active Pregnancies
        BreedingRecord cowBreeding = breedingRepository.save(BreedingRecord.builder()
                .organizationId(orgId).damId(nandiniCow.getId()).sireId(nandiBull.getId())
                .breedingType("NATURAL").bredAt(LocalDate.now().minusDays(180)).outcome("PREGNANT")
                .build());

        pregnancyRepository.save(Pregnancy.builder()
                .organizationId(orgId).breedingRecordId(cowBreeding.getId()).animalId(nandiniCow.getId())
                .confirmationDate(LocalDate.now().minusDays(120)).expectedDueDate(LocalDate.now().plusDays(103))
                .status("CONFIRMED")
                .build());

        BreedingRecord goatBreeding = breedingRepository.save(BreedingRecord.builder()
                .organizationId(orgId).damId(gangaDoe.getId()).sireId(sultanBuck.getId())
                .breedingType("NATURAL").bredAt(LocalDate.now().minusDays(110)).outcome("PREGNANT")
                .build());

        pregnancyRepository.save(Pregnancy.builder()
                .organizationId(orgId).breedingRecordId(goatBreeding.getId()).animalId(gangaDoe.getId())
                .confirmationDate(LocalDate.now().minusDays(70)).expectedDueDate(LocalDate.now().plusDays(40))
                .status("CONFIRMED")
                .build());

        // Past Delivered Pregnancy, Calving Delivery Log & Offspring Connection
        BreedingRecord pastCowBreeding = breedingRepository.save(BreedingRecord.builder()
                .organizationId(orgId).damId(nandiniCow.getId()).sireId(nandiBull.getId())
                .breedingType("NATURAL").bredAt(LocalDate.now().minusMonths(13)).outcome("PREGNANT")
                .build());

        Pregnancy pastCowPregnancy = pregnancyRepository.save(Pregnancy.builder()
                .organizationId(orgId).breedingRecordId(pastCowBreeding.getId()).animalId(nandiniCow.getId())
                .confirmationDate(LocalDate.now().minusMonths(11)).expectedDueDate(LocalDate.now().minusMonths(4))
                .actualDeliveryDate(LocalDate.now().minusMonths(4))
                .status("DELIVERED")
                .build());

        BirthRecord birthRec = birthRecordRepository.save(BirthRecord.builder()
                .organizationId(orgId).pregnancyId(pastCowPregnancy.getId()).damId(nandiniCow.getId()).sireId(nandiBull.getId())
                .totalBorn(1).aliveCount(1).stillbornCount(0).birthDate(LocalDate.now().minusMonths(4))
                .deliveryNotes("Smooth natural calving, healthy active bull calf.")
                .build());

        birthOffspringRepository.save(BirthOffspring.builder()
                .birthRecordId(birthRec.getId()).animalId(krishnaCalf.getId())
                .gender("MALE").birthWeight(BigDecimal.valueOf(27.0)).birthStatus("ALIVE")
                .build());

        // 14. FEED INVENTORY & CONSUMPTION
        FeedInventory dairyConcentrate = feedInventoryRepository.save(FeedInventory.builder()
                .organizationId(orgId).feedName("High-Yield Dairy Cattle Pellets (22% Protein)")
                .feedCategory("CONCENTRATE").quantityKg(BigDecimal.valueOf(1250.0))
                .minThresholdKg(BigDecimal.valueOf(300.0)).costPerKg(BigDecimal.valueOf(29.50))
                .build());

        FeedInventory cornSilage = feedInventoryRepository.save(FeedInventory.builder()
                .organizationId(orgId).feedName("Sweet Corn Silage Bales")
                .feedCategory("SILAGE").quantityKg(BigDecimal.valueOf(2400.0))
                .minThresholdKg(BigDecimal.valueOf(500.0)).costPerKg(BigDecimal.valueOf(7.80))
                .build());

        FeedInventory greenMaize = feedInventoryRepository.save(FeedInventory.builder()
                .organizationId(orgId).feedName("Fresh Green Hybrid Fodder")
                .feedCategory("GREEN_FODDER").quantityKg(BigDecimal.valueOf(1800.0))
                .minThresholdKg(BigDecimal.valueOf(400.0)).costPerKg(BigDecimal.valueOf(4.50))
                .build());

        feedConsumptionLogRepository.save(FeedConsumptionLog.builder()
                .organizationId(orgId).feedInventoryId(dairyConcentrate.getId()).shedPenId(shedMilking.getId())
                .quantityConsumedKg(BigDecimal.valueOf(85.0)).totalCost(BigDecimal.valueOf(2507.50))
                .consumedDate(LocalDate.now().minusDays(1))
                .build());

        // 15. SALES, PURCHASES & FINANCIAL LEDGER
        Sale milkSale = saleRepository.save(Sale.builder()
                .organizationId(orgId).invoiceNumber("INV-2026-S001")
                .buyerName("Mother Dairy Milk Procurement Cooperative")
                .buyerContact("+91 94220 11223").totalAmount(BigDecimal.valueOf(48200.0))
                .paymentStatus("PAID").saleDate(LocalDate.now().minusDays(2))
                .notes("Weekly bulk A2 Cow & Goat milk supply")
                .build());

        saleItemRepository.save(SaleItem.builder()
                .sale(milkSale).itemType("MILK")
                .description("Fresh A2 Gir Cow Milk (620 Liters @ Rs 65/L)")
                .quantity(BigDecimal.valueOf(620.0)).unitPrice(BigDecimal.valueOf(65.0))
                .totalPrice(BigDecimal.valueOf(40300.0))
                .build());

        saleItemRepository.save(SaleItem.builder()
                .sale(milkSale).itemType("MILK")
                .description("Raw Goat Milk (90 Liters @ Rs 85/L)")
                .quantity(BigDecimal.valueOf(90.0)).unitPrice(BigDecimal.valueOf(85.0))
                .totalPrice(BigDecimal.valueOf(7650.0))
                .build());

        Purchase fodderPurchase = purchaseRepository.save(Purchase.builder()
                .organizationId(orgId).invoiceNumber("PO-2026-V889")
                .vendorName("Baramati Agro & Animal Nutrition Ltd")
                .vendorContact("+91 98900 44556").totalAmount(BigDecimal.valueOf(22500.0))
                .paymentStatus("PAID").purchaseDate(LocalDate.now().minusDays(6))
                .notes("Bulk monthly concentrated feed delivery")
                .build());

        purchaseItemRepository.save(PurchaseItem.builder()
                .purchaseId(fodderPurchase.getId()).itemCategory("FEED")
                .itemName("Dairy Cattle Concentrate Pellets (750 kg)")
                .quantity(BigDecimal.valueOf(750.0)).unitPrice(BigDecimal.valueOf(30.0))
                .subtotalPrice(BigDecimal.valueOf(22500.0))
                .build());

        // Income & Expense ledger entries for dashboards & analytics
        incomeRepository.save(Income.builder()
                .organizationId(orgId).category(IncomeCategory.MILK_SALE)
                .amount(BigDecimal.valueOf(48200.0)).incomeDate(LocalDate.now().minusDays(2))
                .sourceName("Mother Dairy Procurement").referenceNumber("NEFT-9912048")
                .description("Weekly bulk milk supply revenue")
                .build());

        incomeRepository.save(Income.builder()
                .organizationId(orgId).category(IncomeCategory.LIVESTOCK_SALE)
                .amount(BigDecimal.valueOf(35000.0)).incomeDate(LocalDate.now().minusDays(14))
                .sourceName("Kisan Agro Farm").referenceNumber("UPI-TXN-881239")
                .description("Sale of 1 Purebred Beetal breeding buck")
                .build());

        expenseRepository.save(Expense.builder()
                .organizationId(orgId).category(ExpenseCategory.FEED)
                .amount(BigDecimal.valueOf(22500.0)).expenseDate(LocalDate.now().minusDays(6))
                .vendorName("Baramati Agro").receiptNumber("INV-8891").paymentMethod("BANK_TRANSFER")
                .description("Monthly concentrate pellets stock refill")
                .build());

        // 16. CRM: SUPPLIERS & CUSTOMERS
        supplierRepository.save(com.pamir.ppfarmsbackend.crm.entity.Supplier.builder()
                .organizationId(orgId).name("Baramati Agro Animal Nutrition")
                .contactPerson("Anand Shinde").phone("+91 98900 44556").email("sales@baramatiagro.com")
                .address("Industrial Estate, Baramati, Pune 413102").taxId("27AAACB1234F1Z5")
                .paymentTerms("NET 30").openingBalance(BigDecimal.ZERO).currentBalance(BigDecimal.ZERO)
                .rating(5).notes("Preferred wholesale supplier for dairy and goat concentrate pellets").isActive(true).build());

        supplierRepository.save(com.pamir.ppfarmsbackend.crm.entity.Supplier.builder()
                .organizationId(orgId).name("Pune Vet Pharma & Vaccines")
                .contactPerson("Dr. Suresh Joshi").phone("+91 94220 99881").email("info@punevetpharma.com")
                .address("Shivaji Nagar, Pune 411005").taxId("27BBBCJ5678K1Z2")
                .paymentTerms("COD").openingBalance(BigDecimal.ZERO).currentBalance(BigDecimal.ZERO)
                .rating(5).notes("Certified distributor for FMD, PPR, and Goat Pox vaccines").isActive(true).build());

        customerRepository.save(com.pamir.ppfarmsbackend.crm.entity.Customer.builder()
                .organizationId(orgId).name("Mother Dairy Milk Procurement Cooperative")
                .phone("+91 94220 11223").email("procurement@motherdairy.org")
                .address("Bulk Dairy Chilling Plant, Pune").customerType("COOPERATIVE")
                .preferredSpecies("Cattle").creditLimit(BigDecimal.valueOf(100000.0)).outstandingBalance(BigDecimal.ZERO)
                .notes("Daily morning/evening bulk milk tanker pickup").isActive(true).build());

        customerRepository.save(com.pamir.ppfarmsbackend.crm.entity.Customer.builder()
                .organizationId(orgId).name("Kisan Livestock & Breeding Traders")
                .phone("+91 98221 33445").email("kisanlivestock@gmail.com")
                .address("Agro Mandi Yard, Satara").customerType("WHOLESALER")
                .preferredSpecies("Goat").creditLimit(BigDecimal.valueOf(50000.0)).outstandingBalance(BigDecimal.ZERO)
                .notes("Buyer for purebred Beetal and Sirohi breeding bucks and kids").isActive(true).build());

        // 17. FODDER & CROP CULTIVATION PLOTS
        com.pamir.ppfarmsbackend.crops.entity.CropPlot napierPlot = cropPlotRepository.save(com.pamir.ppfarmsbackend.crops.entity.CropPlot.builder()
                .organizationId(orgId).plotName("Plot 1 - Super Napier Green Fodder")
                .areaAcres(BigDecimal.valueOf(3.5)).cropName("NAPIER").variety("Super Napier Hybrid CO-4")
                .sowingDate(LocalDate.now().minusMonths(4)).expectedHarvestDate(LocalDate.now().plusDays(10))
                .status("GROWING").irrigationType("DRIP").productionCost(BigDecimal.valueOf(12000.0))
                .notes("High-protein perennial forage crop with 45-day cutting cycles.").build());

        com.pamir.ppfarmsbackend.crops.entity.CropPlot maizePlot = cropPlotRepository.save(com.pamir.ppfarmsbackend.crops.entity.CropPlot.builder()
                .organizationId(orgId).plotName("Plot 2 - African Tall Maize (Silage)")
                .areaAcres(BigDecimal.valueOf(5.0)).cropName("MAIZE").variety("African Tall Silage Hybrid")
                .sowingDate(LocalDate.now().minusMonths(3)).expectedHarvestDate(LocalDate.now().minusDays(5))
                .status("HARVESTED").irrigationType("SPRINKLER").productionCost(BigDecimal.valueOf(25000.0))
                .notes("Harvested for high-energy silage bunk packing.").build());

        cropHarvestLogRepository.save(com.pamir.ppfarmsbackend.crops.entity.CropHarvestLog.builder()
                .organizationId(orgId).cropPlot(maizePlot).harvestDate(LocalDate.now().minusDays(5))
                .yieldKg(BigDecimal.valueOf(18500.0)).destinationFeed(cornSilage)
                .storageLocation("Silage Bunk #1").notes("Excellent dry matter yield with 11% starch content.").build());

        // 18. DAILY TASKS ASSIGNED TO WORKERS & VET
        taskRepository.save(com.pamir.ppfarmsbackend.tasks.entity.Task.builder()
                .organizationId(orgId).title("Feed Afternoon Stall-Fed Rations to Shed B Goats")
                .description("Dispense 40kg Super Napier and 15kg concentrate pellets into feed troughs.")
                .assignedTo(worker).shedPen(shedGoatBarn).taskType("FEEDING")
                .priority(com.pamir.ppfarmsbackend.tasks.domain.TaskPriority.HIGH)
                .status(com.pamir.ppfarmsbackend.tasks.domain.TaskStatus.PENDING)
                .dueDate(LocalDate.now()).notes("Check automatic nipple drinkers as well.").build());

        taskRepository.save(com.pamir.ppfarmsbackend.tasks.entity.Task.builder()
                .organizationId(orgId).title("Post-Treatment Mastitis Check for Kamadhenu (HF-COW-101)")
                .description("Inspect left hind quarter and test strip cup milk before parlor entry.")
                .assignedTo(vet).animal(kamadhenuCow).taskType("CHECKUP")
                .priority(com.pamir.ppfarmsbackend.tasks.domain.TaskPriority.URGENT)
                .status(com.pamir.ppfarmsbackend.tasks.domain.TaskStatus.PENDING)
                .dueDate(LocalDate.now().plusDays(1)).notes("Under drug withdrawal until safety clearance date.").build());

        taskRepository.save(com.pamir.ppfarmsbackend.tasks.entity.Task.builder()
                .organizationId(orgId).title("Weigh Growing Kids in Nursery Pen (SRH-KID-201 & 202)")
                .description("Monthly scale weigh-in to calculate Average Daily Gain (ADG).")
                .assignedTo(worker).shedPen(shedMaternity).taskType("WEIGHING")
                .priority(com.pamir.ppfarmsbackend.tasks.domain.TaskPriority.MEDIUM)
                .status(com.pamir.ppfarmsbackend.tasks.domain.TaskStatus.COMPLETED)
                .dueDate(LocalDate.now().minusDays(2))
                .completedAt(OffsetDateTime.now().minusDays(2))
                .notes("Weights recorded in system. Both kids showing >120g/day gain.").build());

        log.info("[DEMO SEEDER] Commercial farm demo dataset successfully seeded! (Login: owner@greenvalleyfarms.com / Password123!)");
    }

    private Species getOrCreateSpecies(String name, int gestationDays, int heatCycleDays, boolean supportsMilking, boolean supportsShearing) {
        return speciesRepository.findAll().stream()
                .filter(s -> name.equalsIgnoreCase(s.getName()))
                .findFirst()
                .orElseGet(() -> speciesRepository.save(Species.builder()
                        .name(name)
                        .gestationDays(gestationDays)
                        .heatCycleDays(heatCycleDays)
                        .supportsMilking(supportsMilking)
                        .supportsShearing(supportsShearing)
                        .build()));
    }
}
