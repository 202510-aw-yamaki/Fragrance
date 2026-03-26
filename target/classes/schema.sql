CREATE TABLE IF NOT EXISTS visit_types (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  code VARCHAR(32) NOT NULL UNIQUE,
  name VARCHAR(100) NOT NULL,
  description VARCHAR(255),
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS reservation_slots (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  slot_date DATE NOT NULL,
  slot_time TIME NOT NULL,
  status VARCHAR(32) NOT NULL DEFAULT 'open',
  instructor_name VARCHAR(100),
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT uq_reservation_slots_date_time UNIQUE (slot_date, slot_time)
);

CREATE TABLE IF NOT EXISTS reservations (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  reservation_code VARCHAR(32) NOT NULL UNIQUE,
  reservation_slot_id BIGINT NOT NULL,
  visit_type_id BIGINT NOT NULL,
  visit_type_label VARCHAR(100) NOT NULL,
  guest_count INT NOT NULL,
  staff_memo TEXT,
  summary_headline VARCHAR(255),
  questionnaire_result_code VARCHAR(32),
  slot_label VARCHAR(255),
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_reservations_slot FOREIGN KEY (reservation_slot_id) REFERENCES reservation_slots (id),
  CONSTRAINT fk_reservations_visit_type FOREIGN KEY (visit_type_id) REFERENCES visit_types (id)
);

CREATE TABLE IF NOT EXISTS questionnaire_results (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  result_code VARCHAR(32) NOT NULL UNIQUE,
  route_code VARCHAR(32),
  step1_answers_json JSON,
  step2_answers_json JSON,
  graph_axes_json JSON,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);