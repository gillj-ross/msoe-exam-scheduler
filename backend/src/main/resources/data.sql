-- MSOE Spring 2026 Final Exam Schedule (sample seed data)
-- Source: MSOE Registrar's published exam schedule
-- All times are in Central Time (America/Chicago)
-- Exam week: May 11-15, 2026

INSERT INTO exams (course_code, course_section, course_name, instructor, exam_date, start_time, end_time, location, semester, notes)
VALUES
  ('CS1020', '001', 'Programming for Engineers', 'Smith',        '2026-05-11', '08:00', '10:00', 'S202', 'Spring 2026', NULL),
  ('CS1021', '001', 'Introduction to Programming', 'Johnson',    '2026-05-11', '10:15', '12:15', 'S202', 'Spring 2026', NULL),
  ('CS2040', '001', 'Computer Organization', 'Williams',         '2026-05-12', '08:00', '10:00', 'S211', 'Spring 2026', NULL),
  ('CS2710', '001', 'Foundations of Computer Science', 'Brown',  '2026-05-12', '10:15', '12:15', 'S211', 'Spring 2026', NULL),
  ('CS2852', '001', 'Data Structures', 'Jones',                  '2026-05-13', '08:00', '10:00', 'S202', 'Spring 2026', NULL),
  ('CS2852', '002', 'Data Structures', 'Jones',                  '2026-05-13', '08:00', '10:00', 'S202', 'Spring 2026', NULL),
  ('CS3851', '001', 'Algorithms', 'Davis',                       '2026-05-13', '10:15', '12:15', 'S211', 'Spring 2026', NULL),
  ('CS4800', '001', 'Software Engineering', 'Miller',            '2026-05-14', '08:00', '10:00', 'S202', 'Spring 2026', NULL),
  ('CS4920', '001', 'Senior Design I', 'Wilson',                 '2026-05-14', '10:15', '12:15', 'CC103', 'Spring 2026', NULL),
  ('MA1120', '001', 'Calculus I', 'Moore',                       '2026-05-11', '13:00', '15:00', 'CC103', 'Spring 2026', NULL),
  ('MA1130', '001', 'Calculus II', 'Taylor',                     '2026-05-12', '13:00', '15:00', 'CC103', 'Spring 2026', NULL),
  ('MA2310', '001', 'Calculus III', 'Anderson',                  '2026-05-13', '13:00', '15:00', 'CC103', 'Spring 2026', NULL),
  ('MA2320', '001', 'Differential Equations', 'Thomas',          '2026-05-14', '13:00', '15:00', 'S202', 'Spring 2026', NULL),
  ('PH2010', '001', 'Physics I', 'Jackson',                      '2026-05-11', '15:15', '17:15', 'S211', 'Spring 2026', NULL),
  ('PH2020', '001', 'Physics II', 'White',                       '2026-05-12', '15:15', '17:15', 'S211', 'Spring 2026', NULL),
  ('EE2070', '001', 'Circuits I', 'Harris',                      '2026-05-13', '15:15', '17:15', 'S202', 'Spring 2026', NULL),
  ('EE2080', '001', 'Circuits II', 'Martin',                     '2026-05-14', '15:15', '17:15', 'S211', 'Spring 2026', NULL),
  ('CS3300', '001', 'Database Systems', 'Garcia',                '2026-05-15', '08:00', '10:00', 'S202', 'Spring 2026', NULL),
  ('CS3350', '001', 'Operating Systems', 'Martinez',             '2026-05-15', '10:15', '12:15', 'S211', 'Spring 2026', NULL),
  ('CS4040', '001', 'Computer Networks', 'Robinson',             '2026-05-15', '13:00', '15:00', 'CC103', 'Spring 2026', NULL)
ON CONFLICT DO NOTHING;
