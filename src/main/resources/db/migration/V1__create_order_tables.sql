CREATE TABLE orders
(
    id         uuid        NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    status     varchar(20) NOT NULL CHECK (status IN ('NEW', 'PAID', 'SHIPPED', 'DELIVERED', 'CANCELLED')),
    created_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE order_items
(
    id          uuid           NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    order_id    uuid           NOT NULL REFERENCES orders (id) ON DELETE CASCADE,
    line_number integer        NOT NULL CHECK (line_number >= 1),
    name        varchar(255)   NOT NULL,
    quantity    integer        NOT NULL CHECK (quantity >= 1),
    unit_price  numeric(12, 2) NOT NULL CHECK (unit_price >= 0.01),

    UNIQUE (order_id, line_number)
);