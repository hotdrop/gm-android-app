INSERT INTO m_goods_category(name, view_order, register_date) VALUES ("ジン", 1, datetime('now', 'localtime'));
INSERT INTO m_goods_category(name, view_order, register_date) VALUES ("ウォッカ", 2, datetime('now', 'localtime'));
INSERT INTO m_goods_category(name, view_order, register_date) VALUES ("スコッチ", 3, datetime('now', 'localtime'));
INSERT INTO m_goods_category(name, view_order, register_date) VALUES ("バーボン", 4, datetime('now', 'localtime'));
INSERT INTO t_goods(name, category_id, amount, stock_num, last_update_amount_date, last_stock_date, update_date)
      VALUES ("商品その１", 1, 5, "4", 1462201000000, 1462201000000 , datetime('now', 'localtime'));
INSERT INTO t_goods(name, category_id, amount, stock_num, last_update_amount_date, last_stock_date, update_date)
      VALUES ("商品その２", 2, 4, "5", 1462201100000, 1462201100000 , datetime('now', 'localtime'));
INSERT INTO t_goods(name, category_id, amount, stock_num, last_update_amount_date, last_stock_date, update_date)
      VALUES ("商品その３", 1, 3, "6", 1462201200000, 1462201200000 , datetime('now', 'localtime'));
INSERT INTO t_goods(name, category_id, amount, stock_num, last_update_amount_date, last_stock_date, update_date)
      VALUES ("商品その４", 2, 2, "7", 1462201300000, 1462201300000 , datetime('now', 'localtime'));
INSERT INTO t_goods(name, category_id, amount, stock_num, last_update_amount_date, last_stock_date, update_date)
      VALUES ("商品その５", 2, 1, "1", 1462201300000, 1462201300000 , datetime('now', 'localtime'));
