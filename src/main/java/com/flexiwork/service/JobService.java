package com.flexiwork.service;

import com.flexiwork.dto.JobPostRequest;
import com.flexiwork.enums.ApplicationStatus;
import com.flexiwork.dto.PageResponse;
import com.flexiwork.enums.JobCategory;
import com.flexiwork.exception.ResourceNotFoundException;
import com.flexiwork.model.Company;
import com.flexiwork.model.Job;
import com.flexiwork.repository.JobRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class JobService {

    private static final Logger log = LoggerFactory.getLogger(JobService.class);

    private final JobRepository jobRepository;
    private final CompanyService companyService;
    private final com.flexiwork.repository.ApplicationRepository applicationRepository;
    private final SmsService smsService;

    public JobService(JobRepository jobRepository, CompanyService companyService,
                      com.flexiwork.repository.ApplicationRepository applicationRepository,
                      SmsService smsService) {
        this.jobRepository = jobRepository;
        this.companyService = companyService;
        this.applicationRepository = applicationRepository;
        this.smsService = smsService;
    }

    @CacheEvict(value = "jobs", allEntries = true)
    public Job postJob(JobPostRequest request, Long companyId) {
        Company company = companyService.findById(companyId);
        Job job = Job.builder()
                .company(company)
                .title(request.getTitle())
                .description(request.getDescription())
                .dailyWage(request.getDailyWage())
                .shiftStartTime(request.getShiftStartTime())
                .shiftEndTime(request.getShiftEndTime())
                .requiredWorkers(request.getRequiredWorkers())
                .factoryLocation(request.getFactoryLocation())
                .district(request.getDistrict())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .gender(request.getGender() != null ? request.getGender() : com.flexiwork.enums.Gender.ANY)
                .minAge(request.getMinAge())
                .maxAge(request.getMaxAge())
                .category(request.getCategory())
                .shiftDate(request.getShiftDate())
                .isActive(true)
                .isDeleted(false)
                .approvedWorkers(0)
                .build();
        return jobRepository.save(job);
    }

    public PageResponse<Job> searchJobs(String district, Double minWage, Double maxWage,
                                         JobCategory category, LocalDate dateFrom, LocalDate dateTo,
                                         String keyword, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);
        Page<Job> result = jobRepository.searchJobs(
            district, minWage, maxWage, category, dateFrom, dateTo, keyword, pageable);
        return PageResponse.<Job>builder()
                .content(result.getContent())
                .page(result.getNumber())
                .size(result.getSize())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .build();
    }

    public Job getJobById(Long jobId) {
        return jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job", jobId));
    }

    @CacheEvict(value = "jobs", allEntries = true)
    public Job updateJob(Long jobId, JobPostRequest request) {
        Job job = getJobById(jobId);
        job.setTitle(request.getTitle());
        job.setDescription(request.getDescription());
        job.setDailyWage(request.getDailyWage());
        job.setShiftStartTime(request.getShiftStartTime());
        job.setShiftEndTime(request.getShiftEndTime());
        job.setRequiredWorkers(request.getRequiredWorkers());
        job.setFactoryLocation(request.getFactoryLocation());
        job.setDistrict(request.getDistrict());
        job.setLatitude(request.getLatitude());
        job.setLongitude(request.getLongitude());
        if (request.getGender() != null) job.setGender(request.getGender());
        job.setMinAge(request.getMinAge());
        job.setMaxAge(request.getMaxAge());
        job.setCategory(request.getCategory());
        job.setShiftDate(request.getShiftDate());
        return jobRepository.save(job);
    }

    @CacheEvict(value = "jobs", allEntries = true)
    public void softDeleteJob(Long jobId) {
        Job job = getJobById(jobId);
        job.setIsDeleted(true);
        job.setDeletedAt(LocalDateTime.now());
        job.setIsActive(false);
        jobRepository.save(job);

        // Notify all approved workers via WhatsApp
        applicationRepository.findByJobJobId(jobId).forEach(app -> {
            if (app.getStatus() == com.flexiwork.enums.ApplicationStatus.APPROVED) {
                smsService.sendJobCancelledNotification(app.getUser(), job);
            }
        });
    }

    public List<Job> getJobsByCompany(Long companyId) {
        return jobRepository.findByCompanyCompanyIdAndIsDeletedFalse(companyId);
    }

    @Scheduled(fixedRate = 60000)
    @CacheEvict(value = "jobs", allEntries = true)
    public void expireJobs() {
        LocalDate today = LocalDate.now();
        LocalTime cutoff = LocalTime.now().plusHours(1);

        List<Job> expiredByTime = jobRepository.findActiveJobsBeforeTime(today, cutoff);
        for (Job job : expiredByTime) {
            job.setIsActive(false);
            jobRepository.save(job);
            log.info("Job {} expired due to shift time", job.getJobId());
        }

        List<Job> fullyBooked = jobRepository.findFullyBookedJobs();
        for (Job job : fullyBooked) {
            job.setIsActive(false);
            jobRepository.save(job);
            log.info("Job {} expired due to full booking", job.getJobId());
        }
    }

    public List<Job> findAll() {
        return jobRepository.findAll();
    }
}
