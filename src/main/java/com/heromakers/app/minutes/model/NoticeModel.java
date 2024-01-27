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

@Table(name = "tb_notice")
@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@DynamicInsert
@DynamicUpdate
public class NoticeModel implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notice_id", nullable = false)
    private Integer noticeId;

    @Column(name = "notice_kind")
    private String noticeKind;

    @Column(name = "writer_id")
    private Integer writerId;

    @Column(name = "writer")
    private String writer;

    @Column(name = "title")
    private String title;

    @Column(name = "contents")
    private String contents;

    @Column(name = "read_count")
    @Builder.Default
    private Integer readCount = 0;

    @Column(name = "use_fg")
    @Builder.Default
    private Boolean useFlag = true;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    @Column(name = "created_at")
    private Instant createdAt;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    @Column(name = "updated_at")
    private Instant updatedAt;

    @Transient
    private String noticeKindTitle;

    @Transient
    @Builder.Default
    private FileModel mainImage = new FileModel();
}
