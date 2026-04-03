INSERT INTO visit_types (code, name, description, created_at, updated_at)
VALUES
  ('workshop', '初回ワークショップ', '初回来店の香りづくり体験', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('followup', '再来店相談', '再来店時の調整・相談向け', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
  ('gift', 'ギフト相談', 'ギフト用途の香り相談', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON DUPLICATE KEY UPDATE
  name = VALUES(name),
  description = VALUES(description),
  updated_at = CURRENT_TIMESTAMP;

INSERT INTO reservation_slots (
  slot_date,
  slot_time,
  status,
  instructor_name,
  created_at,
  updated_at
)
WITH RECURSIVE seq AS (
  SELECT 1 AS day_offset
  UNION ALL
  SELECT day_offset + 1
  FROM seq
  WHERE day_offset < 14
),
slot_patterns AS (
  SELECT '10:30:00' AS slot_time, 'recommended' AS status, '原口' AS instructor_name
  UNION ALL
  SELECT '13:00:00', 'open', '清水'
  UNION ALL
  SELECT '15:30:00', 'open', '大塚'
)
SELECT
  DATE_ADD(CURDATE(), INTERVAL seq.day_offset DAY),
  CAST(slot_patterns.slot_time AS TIME),
  slot_patterns.status,
  slot_patterns.instructor_name,
  CURRENT_TIMESTAMP,
  CURRENT_TIMESTAMP
FROM seq
CROSS JOIN slot_patterns
LEFT JOIN reservation_slots existing
  ON existing.slot_date = DATE_ADD(CURDATE(), INTERVAL seq.day_offset DAY)
 AND existing.slot_time = CAST(slot_patterns.slot_time AS TIME)
WHERE existing.id IS NULL;

INSERT INTO questionnaire_results (
  result_code,
  route_code,
  step1_answers_json,
  step2_answers_json,
  graph_axes_json,
  created_at,
  updated_at
)
WITH RECURSIVE sample_seq AS (
  SELECT 1 AS seq_no
  UNION ALL
  SELECT seq_no + 1
  FROM sample_seq
  WHERE seq_no < 27
)
SELECT
  CONCAT('SAMPLE-RESULT-', LPAD(seq_no, 3, '0')),
  ELT(1 + FLOOR(RAND(seq_no * 101) * 4), 'A1', 'B2', 'C3', 'D4'),
  JSON_OBJECT(
    'purpose', ELT(1 + FLOOR(RAND(seq_no * 103) * 6), 'refresh', 'gift', 'focus', 'selfcare', 'switch', 'special'),
    'mood', ELT(1 + FLOOR(RAND(seq_no * 107) * 6), 'clean', 'bright', 'calm', 'warm', 'fresh', 'elegant'),
    'strength', ELT(1 + FLOOR(RAND(seq_no * 109) * 3), 'soft', 'medium', 'rich')
  ),
  JSON_OBJECT(
    'note', ELT(1 + FLOOR(RAND(seq_no * 113) * 6), 'citrus', 'floral', 'woody', 'tea', 'musk', 'herbal'),
    'scene', ELT(1 + FLOOR(RAND(seq_no * 127) * 6), 'morning', 'holiday', 'work', 'night', 'travel', 'gift')
  ),
  JSON_OBJECT(
    'sweet', 1 + FLOOR(RAND(seq_no * 131) * 5),
    'fresh', 1 + FLOOR(RAND(seq_no * 137) * 5),
    'calm', 1 + FLOOR(RAND(seq_no * 139) * 5),
    'deep', 1 + FLOOR(RAND(seq_no * 149) * 5)
  ),
  DATE_SUB(CURRENT_TIMESTAMP, INTERVAL (12 + FLOOR(RAND(seq_no * 151) * 96)) HOUR),
  DATE_SUB(CURRENT_TIMESTAMP, INTERVAL (12 + FLOOR(RAND(seq_no * 151) * 96)) HOUR)
FROM sample_seq
LEFT JOIN questionnaire_results existing
  ON existing.result_code = CONCAT('SAMPLE-RESULT-', LPAD(sample_seq.seq_no, 3, '0'))
WHERE existing.id IS NULL;

INSERT INTO reservations (
  reservation_code,
  reservation_slot_id,
  visit_type_id,
  visit_type_label,
  guest_count,
  staff_memo,
  summary_headline,
  questionnaire_result_code,
  slot_label,
  created_at,
  updated_at
)
WITH numbered_slots AS (
  SELECT
    rs.id,
    rs.slot_date,
    rs.slot_time,
    rs.instructor_name,
    ROW_NUMBER() OVER (ORDER BY rs.slot_date, rs.slot_time, rs.id) AS seq_no,
    ROW_NUMBER() OVER (
      PARTITION BY COALESCE(rs.instructor_name, '担当未定')
      ORDER BY RAND(rs.id * 163 + DAYOFMONTH(rs.slot_date))
    ) AS instructor_seq
  FROM reservation_slots rs
  WHERE rs.slot_date >= DATE_ADD(CURDATE(), INTERVAL 1 DAY)
),
sample_slots AS (
  SELECT *
  FROM numbered_slots
  WHERE instructor_seq <= 9
),
visit_type_map AS (
  SELECT code, id, name
  FROM visit_types
)
SELECT
  CONCAT('SAMPLE-', DATE_FORMAT(CURDATE(), '%y%m'), '-', LPAD(sample_slots.seq_no, 3, '0')),
  sample_slots.id,
  CASE ((sample_slots.instructor_seq - 1) % 3)
    WHEN 0 THEN (SELECT id FROM visit_type_map WHERE code = 'workshop')
    WHEN 1 THEN (SELECT id FROM visit_type_map WHERE code = 'followup')
    ELSE (SELECT id FROM visit_type_map WHERE code = 'gift')
  END,
  CASE ((sample_slots.instructor_seq - 1) % 3)
    WHEN 0 THEN '初回ワークショップ'
    WHEN 1 THEN '再来店相談'
    ELSE 'ギフト相談'
  END,
  1 + FLOOR(RAND(sample_slots.seq_no * 173) * 4),
  ELT(1 + FLOOR(RAND(sample_slots.seq_no * 179) * 8),
    '香りの方向性を比較しながら案内希望',
    '前回の香りを基準に再調整したい',
    '贈り先の好みをヒアリング予定',
    '柑橘寄りとウッディ寄りで迷っている',
    '当日の所要時間は短めを希望',
    'ペアで香りの違いを比べたい',
    '仕事向けと休日向けで使い分けたい',
    '贈答用候補を2案ほど見たい'
  ),
  ELT(1 + FLOOR(RAND(sample_slots.seq_no * 181) * 8),
    '初回ヒアリング予定',
    '再来店フォローアップ',
    'ギフト提案候補あり',
    '比較提案メイン',
    '短時間調整希望',
    '季節提案を優先',
    '香りの持続時間を相談',
    '複数候補を試香予定'
  ),
  CASE
    WHEN FLOOR(RAND(sample_slots.seq_no * 191) * 5) = 0 THEN NULL
    ELSE CONCAT('SAMPLE-RESULT-', LPAD(1 + FLOOR(RAND(sample_slots.seq_no * 193) * 27), 3, '0'))
  END,
  CONCAT(DATE_FORMAT(sample_slots.slot_date, '%Y/%m/%d'), ' ', DATE_FORMAT(sample_slots.slot_time, '%H:%i'), ' ', COALESCE(sample_slots.instructor_name, '担当未定')),
  DATE_SUB(CURRENT_TIMESTAMP, INTERVAL (8 + FLOOR(RAND(sample_slots.seq_no * 197) * 120)) HOUR),
  DATE_SUB(CURRENT_TIMESTAMP, INTERVAL (8 + FLOOR(RAND(sample_slots.seq_no * 197) * 120)) HOUR)
FROM sample_slots
LEFT JOIN reservations existing
  ON existing.reservation_code = CONCAT('SAMPLE-', DATE_FORMAT(CURDATE(), '%y%m'), '-', LPAD(sample_slots.seq_no, 3, '0'))
WHERE existing.id IS NULL;
