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

@Table(name = "tb_talk")
@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@DynamicInsert
@DynamicUpdate
public class TalkModel implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "talk_id", nullable = false)
    private Integer talkId;

    @Column(name = "talk_kind")
    private String talkKind;

    @Column(name = "writer_id")
    private Integer writerId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(
            name = "writer_id",
            referencedColumnName = "account_id",
            insertable = false,
            updatable = false,
            foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT)
    )
    private AccountModel writer;

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
    private String talkKindTitle;

    @Transient
    private String writerName;

    @Transient
    @Builder.Default
    private FileModel mainImage = new FileModel();
}