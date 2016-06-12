CREATE TABLE m_goods_category (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL
);
CREATE TABLE t_goods (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    category_id INTEGER NOT NULL,
    amount INTEGER NOT NULL DEFAULT 100,
    stock_num INTEGER,
    last_stocking_date INTEGER,
    last_stocking_price INTEGER,
    last_update_date INTEGER
);
