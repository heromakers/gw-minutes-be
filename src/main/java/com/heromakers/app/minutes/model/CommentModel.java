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

@Table(name = "tb_comment")
@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@DynamicInsert
@DynamicUpdate
public class CommentModel implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id", nullable = false)
    private Integer commentId;

    @Column(name = "parent_kind")
    private String parentKind;

    @Column(name = "parent_id")
    private Integer parentId;

    @Column(name = "preceding_id")
    private Integer precedingId;

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

    @Column(name = "contents")
    private String contents;

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
    private String writerName;

    @Transient
    private String parentKindTitle;
}
