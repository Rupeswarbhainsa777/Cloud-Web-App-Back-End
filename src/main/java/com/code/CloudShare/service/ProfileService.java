package com.code.CloudShare.service;

import com.code.CloudShare.document.ProfileDocument;
import com.code.CloudShare.dto.ProfileDto;
import com.code.CloudShare.repository.ProfileRepository;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private   ProfileRepository profileRepository;
    public ProfileDto createProfile(ProfileDto profileDto){
    ProfileDocument profile =    ProfileDocument.builder()
                .clerkId(profileDto.getClerkId())
                .email(profileDto.getEmail())
                .firstName(profileDto.getFirstName())
                .lastName(profileDto.getLastName())
                .photoUrl(profileDto.getPhotoUrl())
                .credits(5)
                .createdAt(Instant.now())
                .build();


              profile =  profileRepository.save(profile);

           return    ProfileDto.builder()
                      .id(profile.getId())
                      .clerkId(profile.getClerkId())
                      .email(profile.getEmail())
                      .firstName(profile.getFirstName())
                      .lastName(profile.getLastName())
                      .photoUrl(profile.getPhotoUrl())
                      .credits(profile.getCredits())
                      .createdAt(profile.getCreatedAt())
                      .build();
    }

}
