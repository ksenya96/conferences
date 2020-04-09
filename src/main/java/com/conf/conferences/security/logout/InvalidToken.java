package com.conf.conferences.security.logout;

import lombok.Data;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "invalid_token", schema = "confs")
@Data
public class InvalidToken implements Serializable {

    private static final long serialVersionUID = 441960217825674505L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_id")
    private long tokenId;

    private String token;

    @Basic
    @Column(name = "expiration_time", columnDefinition = "TIMESTAMP WITHOUT TIME ZONE")
    private LocalDateTime expirationTime;


}
