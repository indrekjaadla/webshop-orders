INSERT INTO orders(id, status, created_at)
VALUES ('8786448f-277b-4812-84ff-07a9bd3a2366', 'DELIVERED', '2026-09-08 09:14:00+00'),
       ('e12e95ad-ca33-4f7e-8660-08e3a6f9b8e5', 'DELIVERED', '2026-09-12 16:42:00+00'),
       ('047e3406-6625-44a8-ac0d-45d128a5fe2a', 'CANCELLED', '2026-09-17 11:05:00+00'),
       ('5ffcfe78-2478-4845-b82d-10edac2b398e', 'SHIPPED', '2026-09-22 13:37:00+00'),
       ('de425e09-af1b-4fc4-b802-8fd468074a82', 'PAID', '2026-09-27 08:20:00+00'),
       ('9e357c56-00d9-40b5-911f-f541b249ae94', 'NEW', '2026-09-29 19:48:00+00'),
       ('4bb55d97-e621-42cd-bb62-0d46e0b04127', 'NEW', '2026-09-29 19:48:00+00');

INSERT INTO order_items (order_id, line_number, name, quantity, unit_price)
VALUES ('8786448f-277b-4812-84ff-07a9bd3a2366', 1, 'Lenovo ThinkPad T14 Gen 5',     1, 1299.99),
       ('8786448f-277b-4812-84ff-07a9bd3a2366', 2, 'Logitech MX Master 3S',         1,   99.99),
       ('8786448f-277b-4812-84ff-07a9bd3a2366', 3, 'USB-C dokkimisjaam',            1,  189.00),

       ('e12e95ad-ca33-4f7e-8660-08e3a6f9b8e5', 1, 'Dell UltraSharp 27 monitor',    2,  529.00),

       ('047e3406-6625-44a8-ac0d-45d128a5fe2a', 1, 'Apple MacBook Air 13 M4',       1, 1249.00),
       ('047e3406-6625-44a8-ac0d-45d128a5fe2a', 2, 'Sülearvuti kott',               1,   39.90),

       ('5ffcfe78-2478-4845-b82d-10edac2b398e', 1, 'Sony mürasummutavad klapid',    1, 449.00),
       ('5ffcfe78-2478-4845-b82d-10edac2b398e', 2, 'HDMI kaabel 2 m',               3,   12.49),
       ('5ffcfe78-2478-4845-b82d-10edac2b398e', 3, 'Mänguri klaviatuur',            1,  139.00),
       ('5ffcfe78-2478-4845-b82d-10edac2b398e', 4, 'Hiirematt',                     2,   14.90),

       ('de425e09-af1b-4fc4-b802-8fd468074a82', 1, 'Samsung Galaxy Tab S10',        1,  899.00),
       ('de425e09-af1b-4fc4-b802-8fd468074a82', 2, 'Ekraanikaitse',                 2,   19.99),

       ('9e357c56-00d9-40b5-911f-f541b249ae94', 1, 'Raspberry Pi 5 8 GB',           1,   89.90),
       ('9e357c56-00d9-40b5-911f-f541b249ae94', 2, 'microSD kaart 64 GB',           2,   11.50),
       ('9e357c56-00d9-40b5-911f-f541b249ae94', 3, 'microSD kaart 64 GB',           1,   11.50),

       ('4bb55d97-e621-42cd-bb62-0d46e0b04127', 1, 'Kontoritool',                   1,  249.00);
