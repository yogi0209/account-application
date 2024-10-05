package com.yogendra.dao;

import com.yogendra.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountDao extends JpaRepository<Account, String> {

}

