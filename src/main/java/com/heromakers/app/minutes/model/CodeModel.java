package com.heromakers.app.minutes.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.io.Serializable;
import java.time.Instant;

@Table(name = "tb_code")
@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@DynamicInsert
@DynamicUpdate
public class CodeModel implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "code_id", nullable = false)
    private Integer codeId;

    @Column(name = "p_code")
    private String parentCode;

    @Column(name = "code")
    private String code;

    @Column(name = "code_label")
    private String codeLabel;

    @Column(name = "memo")
    private String memo;

    @Column(name = "str_val")
    private String stringValue;

    @Column(name = "num_val")
    private Float numberValue;

    @Column(name = "use_fg")
    @Builder.Default
    private Boolean useFlag = true;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    @Column(name = "edited_at")
    private Instant editedAt;

    @Transient
    private String parentCodeLabel;

    @Transient
    private int rowSeq;
}