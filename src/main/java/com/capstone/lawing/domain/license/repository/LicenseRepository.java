package com.capstone.lawing.domain.license.repository;

import com.capstone.lawing.domain.license.License;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LicenseRepository extends JpaRepository<License,Long> {
    Optional<License> findBySerialNumber(String serialNum);
}
