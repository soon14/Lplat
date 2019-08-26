package com.zengshi.paas.license_server.repository;

import javax.persistence.Table;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.zengshi.paas.license_server.entity.ProdLic;

@Repository
@Table(name = "product_licenses")
@Qualifier("prodLicRepo")
public interface ProdLicRepo extends JpaRepository<ProdLic, String>{

}
