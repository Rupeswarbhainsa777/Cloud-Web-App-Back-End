package com.code.CloudShare.repository;

import com.code.CloudShare.document.ProfileDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ProfileRepository extends MongoRepository<ProfileRepository,String> {

   Optional<ProfileDocument> findByEmail(String email);
}
