package com.alibou.stockmanage.auths.models;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name="profiles")
public class ProfileImage {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String fileName;

    private String fileType;

    @Lob
    @Column(nullable = false)
    private byte[]images;//type byte[] mais non Blob car le profile n'est pas "volumineux" <= 1GO

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_details_id", nullable = false)
    private UserDetails userDetails;
}
