CREATE TABLE m_goods_category (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    view_order INTEGER NOT NULL
);

CREATE TABLE t_goods (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    category_id INTEGER NOT NULL,
    amount INTEGER NOT NULL DEFAULT 5,
    stock_num INTEGER,
    note TEXT,
    checked INTEGER DEFAULT 0,
    checked_confirm_date INTEGER,
    register_date INTEGER,
    update_date INTEGER
);

CREATE TABLE t_check_version (
    version INTEGER PRIMARY KEY AUTOINCREMENT,
    checked_date INTEGER
);

CREATE TABLE t_history (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    version INTEGER NOT NULL,
    goods_id INTEGER NOT NULL,
    goods_name TEXT NOT NULL,
    category_id INTEGER NOT NULL,
    category_name TEXT NOT NULL,
    amount INTEGER NOT NULL,
    stock_num INTEGER,
    register_date INTEGER,
    update_date INTEGER
);
