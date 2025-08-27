package com.code.CloudShare.repository;

import com.code.CloudShare.document.UserCredits;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserCreditsRepository extends MongoRepository<UserCredits,String> {

}
