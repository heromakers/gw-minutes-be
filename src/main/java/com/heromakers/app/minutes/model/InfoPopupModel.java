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

@Table(name = "tb_info_popup")
@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@DynamicInsert
@DynamicUpdate
public class InfoPopupModel implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seq", nullable = false)
    private Integer seq;

    @Column(name = "code")
    private String code;

    @Column(name = "kind")
    private String kind;

    @Column(name = "status")
    private String status;

    @Column(name = "title")
    private String title;

    @Column(name = "details")
    private String details;

    @Column(name = "options")
    private String options; // link, image, buttons

    @Column(name = "start_date")
    private String startDate;

    @Column(name = "end_date")
    private String endDate;

    @Column(name = "register_id")
    private Integer registerId;

    @Column(name = "show_fg")
    @Builder.Default
    private Boolean showFlag = true;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    @Column(name = "edited_at")
    private Instant editedAt;

    @Transient
    @Builder.Default
    private FileModel popupImage = new FileModel();

    @Transient
    private String registerName;
}
