package com.heromakers.app.minutes.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.io.Serializable;
import java.time.Instant;

@Table(name = "tb_file")
@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@DynamicInsert
@DynamicUpdate
public class FileModel implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id", nullable = false)
    private Integer fileId;

    @JsonIgnore
    @Column(name = "link_info")
    private String linkInfo;

    @JsonIgnore
    @Column(name = "link_key")
    private Integer linkKey;

    @Column(name = "real_name")
    private String realName;

    @Column(name = "file_url")
    private String fileUrl;

    @Column(name = "file_size")
    private Long fileSize;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    @Column(name = "saved_at")
    private Instant savedAt;

    @Transient
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String base64String;

    @Transient
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private byte[] bytes;
}