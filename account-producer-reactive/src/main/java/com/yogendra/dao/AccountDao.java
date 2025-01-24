package com.yogendra.dao;

import com.yogendra.entity.Account;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface AccountDao extends ReactiveCrudRepository<Account, String> {

}
