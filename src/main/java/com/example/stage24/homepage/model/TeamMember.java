package com.example.stage24.homepage.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "team_members")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "about_us_id")
    @JsonBackReference
    private AboutUs aboutUs;

    @Column(name = "member_name")
    private String name;

    @Column(name = "member_position")
    private String position;

    @Column(name = "member_description", columnDefinition = "LONGTEXT")
    private String bio;

    @Column(name = "display_order")
    private Integer displayOrder;

    @Column(name = "member_image")
    private String image;

}