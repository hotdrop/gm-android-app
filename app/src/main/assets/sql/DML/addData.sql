INSERT INTO m_goods_category(name, view_order) VALUES ("ジン", 1);
INSERT INTO m_goods_category(name, view_order) VALUES ("ウォッカ", 2);
INSERT INTO t_goods(name, category_id, amount, stock_num, last_stock_date, last_stock_price, last_update_date)
 VALUES ("商品その１", 1, 100, 4, 1462201000000, 2000, datetime('now', 'localtime'));
INSERT INTO t_goods(name, category_id, amount, stock_num, last_stock_date, last_stock_price, last_update_date)
 VALUES ("商品その２", 2, 75, 5, 1462201100000, 5000, datetime('now', 'localtime'));
INSERT INTO t_goods(name, category_id, amount, stock_num, last_stock_date, last_stock_price, last_update_date)
 VALUES ("商品その３", 1, 50, 6, 1462201200000, 12000, datetime('now', 'localtime'));
INSERT INTO t_goods(name, category_id, amount, stock_num, last_stock_date, last_stock_price, last_update_date)
 VALUES ("商品その４", 2, 25, 7, 1462201300000, 4500, datetime('now', 'localtime'));
INSERT INTO t_goods(name, category_id, amount, stock_num, last_stock_date, last_stock_price, last_update_date)
 VALUES ("商品その５", 2, 0, 1, 1462201300000, 7500, datetime('now', 'localtime'));
