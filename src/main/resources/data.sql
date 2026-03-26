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
  SELECT '10:30:00' AS slot_time, 'recommended' AS status, 'Haraguchi' AS instructor_name
  UNION ALL
  SELECT '13:00:00', 'open', 'Shimizu'
  UNION ALL
  SELECT '15:30:00', 'open', 'Otsuka'
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