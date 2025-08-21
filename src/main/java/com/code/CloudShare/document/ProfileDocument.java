package com.code.CloudShare.document;


import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor  // Required by Spring Data MongoDB
@AllArgsConstructor // For convenience
@Document(collection = "profiles") // ✅ Corrected "collation" -> "collection"
public class ProfileDocument {

    @Id
    private String id;

    private String clerkId;

    @Indexed(unique = true)
    private String email;

    private String firstName;
    private String lastName;

    private Integer credits;
    private String photoUrl;

    @CreatedDate
    private Instant createdAt;
}
