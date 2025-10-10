-- liquibase formatted sql

-- changeset giuliodalbono:create-user
CREATE TABLE user (
    uid VARCHAR(255) PRIMARY KEY,
    version BIGINT NOT NULL DEFAULT 0,
    email VARCHAR(255) NOT NULL UNIQUE,
    username VARCHAR(255) NOT NULL,
    profile_picture LONGBLOB,
    creation_time DATETIME NOT NULL DEFAULT NOW(),
    last_update DATETIME NOT NULL DEFAULT NOW(),
    INDEX `user_idx_email` (email),
    INDEX `user_idx_username` (username),
    INDEX `user_idx_last_update` (last_update)
);
-- rollback DROP TABLE
-- rollback create-user

-- changeset giuliodalbono:create-skill
CREATE TABLE skill (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    version BIGINT NOT NULL DEFAULT 0,
    label VARCHAR(255) NOT NULL UNIQUE,
    metadata JSON,
    description VARCHAR(255),
    creation_time DATETIME NOT NULL DEFAULT NOW(),
    last_update DATETIME NOT NULL DEFAULT NOW(),
    INDEX `skill_idx_label` (label),
    INDEX `skill_idx_last_update` (last_update)
);
-- rollback DROP TABLE
-- rollback create-skill

-- changeset giuliodalbono:create-swap_proposal
CREATE TABLE swap_proposal (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    version BIGINT NOT NULL DEFAULT 0,
    date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    presentation_letter VARCHAR(255),
    status VARCHAR(255) NOT NULL,
    creation_time DATETIME NOT NULL DEFAULT NOW(),
    last_update DATETIME NOT NULL DEFAULT NOW(),
    skill_offered_id BIGINT NOT NULL,
    skill_requested_id BIGINT NOT NULL,
    request_user_uid VARCHAR(255) NOT NULL,
    offer_user_uid VARCHAR(255) NOT NULL,
    constraint fk_sp_skill_offered foreign key (skill_offered_id) references skill (id),
    constraint fk_sp_skill_requested foreign key (skill_requested_id) references skill (id),
    constraint fk_sp_request_user foreign key (request_user_uid) references user (uid),
    constraint fk_sp_offer_user foreign key (offer_user_uid) references user (uid),
    INDEX `swap_proposal_idx_date` (date),
    INDEX `swap_proposal_idx_status` (status),
    INDEX `swap_proposal_idx_last_update` (last_update)
);
-- rollback DROP TABLE
-- rollback create-swap_proposal

-- changeset giuliodalbono:create-feedback
CREATE TABLE feedback (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    version BIGINT NOT NULL DEFAULT 0,
    rating INT NOT NULL,
    review VARCHAR(255),
    creation_time DATETIME NOT NULL DEFAULT NOW(),
    last_update DATETIME NOT NULL DEFAULT NOW(),
    reviewer_uid VARCHAR(255) NOT NULL,
    reviewed_uid VARCHAR(255) NOT NULL,
    constraint fk_fb_reviewer_uid foreign key (reviewer_uid) references user (uid),
    constraint fk_fb_reviewed_uid foreign key (reviewed_uid) references user (uid),
    INDEX `feedback_idx_rating` (rating),
    INDEX `feedback_idx_last_update` (last_update)
);
-- rollback DROP TABLE
-- rollback create-feedback

-- changeset giuliodalbono:create-skill_offered
CREATE TABLE skill_offered (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    version BIGINT NOT NULL DEFAULT 0,
    user_uid VARCHAR(255) NOT NULL,
    skill_id BIGINT NOT NULL,
    creation_time DATETIME NOT NULL DEFAULT NOW(),
    last_update DATETIME NOT NULL DEFAULT NOW(),
    constraint fk_so_user foreign key (user_uid) references user (uid),
    constraint fk_so_skill foreign key (skill_id) references skill (id),
    constraint uk_so_user_skill unique (user_uid, skill_id),
    INDEX `skill_offered_idx_last_update` (last_update)
);
-- rollback DROP TABLE
-- rollback create-skill_offered

-- changeset giuliodalbono:create-skill_desired
CREATE TABLE skill_desired (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    version BIGINT NOT NULL DEFAULT 0,
    user_uid VARCHAR(255) NOT NULL,
    skill_id BIGINT NOT NULL,
    creation_time DATETIME NOT NULL DEFAULT NOW(),
    last_update DATETIME NOT NULL DEFAULT NOW(),
    constraint fk_sd_user foreign key (user_uid) references user (uid),
    constraint fk_sd_skill foreign key (skill_id) references skill (id),
    constraint uk_sd_user_skill unique (user_uid, skill_id),
    INDEX `skill_desired_idx_last_update` (last_update)
);
-- rollback DROP TABLE
-- rollback create-skill_desired