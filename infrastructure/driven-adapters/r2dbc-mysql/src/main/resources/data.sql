-- Sample seed data. Runs after schema.sql when spring.sql.init.mode=always.
-- Each block is guarded so it only inserts when the table is empty, which
-- keeps this script idempotent (safe to run on every startup).
-- Ids are explicit so foreign keys line up and AUTO_INCREMENT advances.

INSERT INTO franchise (id, name)
SELECT * FROM (
    SELECT 1 AS id, 'Coffee Co' AS name
    UNION ALL SELECT 2, 'Book Haven'
) AS seed
WHERE NOT EXISTS (SELECT 1 FROM franchise);

INSERT INTO branch (id, name, franchise_id)
SELECT * FROM (
    SELECT 1 AS id, 'Downtown' AS name, 1 AS franchise_id
    UNION ALL SELECT 2, 'Airport', 1
    UNION ALL SELECT 3, 'Mall', 2
) AS seed
WHERE NOT EXISTS (SELECT 1 FROM branch);

INSERT INTO product (id, name, stock, branch_id)
SELECT * FROM (
    SELECT 1 AS id, 'Latte' AS name, 50 AS stock, 1 AS branch_id
    UNION ALL SELECT 2, 'Espresso', 80, 1
    UNION ALL SELECT 3, 'Cappuccino', 30, 1
    UNION ALL SELECT 4, 'Americano', 20, 2
    UNION ALL SELECT 5, 'Mocha', 65, 2
    UNION ALL SELECT 6, 'Notebook', 15, 3
    UNION ALL SELECT 7, 'Pen', 200, 3
    UNION ALL SELECT 8, 'Novel', 40, 3
) AS seed
WHERE NOT EXISTS (SELECT 1 FROM product);
