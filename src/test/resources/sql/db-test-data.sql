TRUNCATE TABLE approval_registry, document_history, documents RESTART IDENTITY CASCADE;

INSERT INTO documents (id, uid, author, title, status, created_at, updated_at) VALUES
(1, 'DOC-0001', 'ivan',  'Черновик Ивана',          'DRAFT',     now() - interval '5 days', now() - interval '5 days'),
(2, 'DOC-0002', 'ivan',  'Документ Ивана (subm.)',  'SUBMITTED', now() - interval '4 days', now() - interval '3 days'),
(3, 'DOC-0003', 'petr',  'Документ Пети (appr.)',   'APPROVED',  now() - interval '3 days', now() - interval '1 days'),
(4, 'DOC-0004', 'maria', 'Документ Маши (appr.)',   'APPROVED',  now() - interval '2 days', now() - interval '1 days'),
(5, 'DOC-0005', 'maria', 'Документ Маши (subm.)',   'SUBMITTED', now() - interval '1 days', now()),
(6, 'DOC-0006', 'alex',  'Черновик Алекса',         'DRAFT',     now(),                     now());

INSERT INTO document_history (id, document_id, performed_by, created_at, action, comment) VALUES
(1, 2, 'ivan',     now() - interval '4 days', 'SUBMIT',  'Initial submit for DOC-0002'),
(2, 3, 'petr',     now() - interval '3 days', 'SUBMIT',  'Send DOC-0003 for approval'),
(3, 3, 'manager1', now() - interval '2 days', 'APPROVE', 'Approve DOC-0003'),
(4, 4, 'maria',    now() - interval '2 days', 'SUBMIT',  'Submit DOC-0004'),
(5, 4, 'manager2', now() - interval '1 days', 'APPROVE', 'Approve DOC-0004'),
(6, 5, 'maria',    now() - interval '1 days', 'SUBMIT',  'Submit DOC-0005');

INSERT INTO approval_registry (id, document_id, approver, created_at) VALUES
(1, 3, 'manager1', now() - interval '2 days'),
(2, 4, 'manager2', now() - interval '1 days');

-- поправляем последовательности, чтобы новые id не конфликтовали с уже заданными
SELECT setval('documents_seq',
              COALESCE((SELECT MAX(id) FROM documents), 1),
              true);

SELECT setval('document_history_seq',
              COALESCE((SELECT MAX(id) FROM document_history), 1),
              true);

SELECT setval('approval_registry_seq',
              COALESCE((SELECT MAX(id) FROM approval_registry), 1),
              true);

/*POST http://localhost:8080/api/documents/batch/submit
Content-Type: application/json

{
  "documentIds": [1, 2, 3, 100],
  "performedBy": "ivan",
  "comment": "Batch submit test"
}*/

/*POST http://localhost:8080/api/documents/batch/approve
Content-Type: application/json

{
  "documentIds": [1, 2, 3, 4, 6],
  "approver": "manager_batch",
  "comment": "Batch approve test"
}*/

-- GET http://localhost:8080/api/documents/search?status=SUBMITTED
-- GET http://localhost:8080/api/documents/search?author=ivan
-- GET http://localhost:8080/api/documents/search?status=SUBMITTED&author=ivan
-- GET http://localhost:8080/api/documents/search?createdFrom=2026-02-23T00:00:00Z
-- GET http://localhost:8080/api/documents/search?createdFrom=2026-02-20T00:00:00Z&createdTo=2026-02-25T23:59:59Z