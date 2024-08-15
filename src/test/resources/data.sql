INSERT INTO nudge (id, user_id, title, description, due)
VALUES (1, 'd30d4015-9985-41d5-a201-b9575db5d239', 'Title', 'Description', '2025-01-01'),
       (2, 'd30d4015-9985-41d5-a201-b9575db5d239', 'Title', 'Description', '2026-01-01');

INSERT INTO trigger (id, period, span, communication)
VALUES (1, 0, 1, 0),
       (2, 0, 1, 0);

INSERT INTO nudge_triggers (nudge_id, triggers_id)
VALUES (1, 1),
       (2, 2);