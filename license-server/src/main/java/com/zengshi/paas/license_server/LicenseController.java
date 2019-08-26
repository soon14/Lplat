package com.zengshi.paas.license_server;

import java.sql.Timestamp;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zengshi.paas.license_server.entity.LicLog;
import com.zengshi.paas.license_server.entity.ProdLic;
import com.zengshi.paas.license_server.repository.LicLogRepo;
import com.zengshi.paas.license_server.repository.ProdLicRepo;

@RestController
public class LicenseController {

	private final static Logger logger = LoggerFactory.getLogger(LicenseController.class);

	@Autowired
	Environment env;

	@Autowired
	protected LicLogRepo logRepo;

	@Autowired
	protected ProdLicRepo prodLicRepo;
	
	@RequestMapping("/")
	public String index() {
		return "Greetings from Spring Boot!";
	}

	@GetMapping("/{product_id}")
	public int license(@PathVariable String product_id, @RequestParam(value = "salt") String s,
			HttpServletRequest request, HttpServletResponse response) {
		String salt = env.getProperty("salt");
		String msg = "";
		int state = 1;
		if (s == null || !s.equals(salt)) {
			logger.error("salt " + s + "is not match ");
			msg += " salt " + s + "is not match ";
		}
		
		LicLog log = new LicLog();
		log.setProduct_id(product_id);
		log.setSalt(s);
		log.setFrom_ip(request.getRemoteAddr());
		log.setDatetime(new Timestamp(System.currentTimeMillis()));
		
		Optional<ProdLic> lics = prodLicRepo.findById(product_id);
		if (lics == null || !lics.isPresent() ) {
			logger.error("no license for product_id " + product_id);
			msg += "no license for product_id " + product_id;
		}
		else {
			ProdLic lic = lics.get();
			state = lic.getState();
			log.setResponse_state(state);
		}
		
		log.setMsg(msg);

		logRepo.save(log);

		
		return state;
	}
}
