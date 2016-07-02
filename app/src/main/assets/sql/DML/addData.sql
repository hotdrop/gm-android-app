INSERT INTO m_goods_category(name, view_order) VALUES ("ジン", 1);
INSERT INTO m_goods_category(name, view_order) VALUES ("ウォッカ", 2);
INSERT INTO m_goods_category(name, view_order) VALUES ("スコッチ", 3);
INSERT INTO m_goods_category(name, view_order) VALUES ("バーボン", 4);
INSERT INTO t_goods(name, category_id, amount, stock_num, last_stock_date, last_stock_price, update_date)
 VALUES ("商品その１", 1, 5, "4", 1462201000000, "2000" , datetime('now', 'localtime'));
INSERT INTO t_goods(name, category_id, amount, stock_num, last_stock_date, last_stock_price, update_date)
 VALUES ("商品その２", 2, 4, "5", 1462201100000, "2000" , datetime('now', 'localtime'));
INSERT INTO t_goods(name, category_id, amount, stock_num, last_stock_date, last_stock_price, update_date)
 VALUES ("商品その３", 1, 3, "6", 1462201200000, "2000" , datetime('now', 'localtime'));
INSERT INTO t_goods(name, category_id, amount, stock_num, last_stock_date, last_stock_price, update_date)
 VALUES ("商品その４", 2, 2, "7", 1462201300000, "2000" , datetime('now', 'localtime'));
INSERT INTO t_goods(name, category_id, amount, stock_num, last_stock_date, last_stock_price, update_date)
 VALUES ("商品その５", 2, 1, "1", 1462201300000, "2000" , datetime('now', 'localtime'));
