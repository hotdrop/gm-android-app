CREATE TABLE m_goods_category (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    view_order INTEGER
);
CREATE TABLE t_goods (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    category_id INTEGER NOT NULL,
    amount INTEGER NOT NULL DEFAULT 5,
    stock_num String,
    last_stock_date INTEGER,
    last_stock_price String,
    update_date INTEGER
);
