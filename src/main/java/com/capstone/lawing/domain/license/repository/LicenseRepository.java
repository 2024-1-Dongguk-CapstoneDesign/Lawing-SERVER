package com.capstone.lawing.domain.license.repository;

import com.capstone.lawing.domain.license.License;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LicenseRepository extends JpaRepository<License,Long> {

}
