package com.zeeyeh.multicurrency.manager;

import cn.hutool.core.util.IdUtil;
import cn.hutool.db.Db;
import cn.hutool.db.DbUtil;
import cn.hutool.db.Entity;
import cn.hutool.db.ds.simple.SimpleDataSource;
import com.zeeyeh.multicurrency.MultiCurrency;
import com.zeeyeh.multicurrency.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.rmi.AlreadyBoundException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Map;

public class DatasourceManagerProvider {
    private Db db;

    public DatasourceManagerProvider() {
        ConfigurationSection section = MultiCurrency.getInstance().getConfigManager().getConfig("config").getConfigurationSection("datasource");
        if (section == null) {
            MessageUtil.sendMessage(Bukkit.getConsoleSender(), "&4数据库连接失败，请检查配置连接然后重启服务端以加载该插件");
            Bukkit.getPluginManager().disablePlugin(MultiCurrency.getInstance());
            return;
        }
        String host = section.getString("host");
        int port = section.getInt("port");
        String username = section.getString("username");
        String password = section.getString("password");
        String database = section.getString("database");
        String connectUrl = "jdbc:mysql://" + host + ":" + port + "/" + database + "?timezone=Asia/Shanghai&useSSL=false";
        String driver = "com.mysql.cj.jdbc.Driver";
        try (SimpleDataSource simpleDataSource = new SimpleDataSource(connectUrl, username, password, driver)) {
            db = DbUtil.use(simpleDataSource);
        }
        initTables();
    }

    private void initTables() {
        try {
            db.execute("CREATE TABLE IF NOT EXISTS multi_currency.currency (\n" +
                    "\tid INT auto_increment NOT NULL,\n" +
                    "\tname varchar(30) NOT NULL,\n" +
                    "\tcid varchar(50) NOT NULL,\n" +
                    "\tCONSTRAINT currency_pk PRIMARY KEY (id)\n" +
                    ")\n" +
                    "ENGINE=MyISAM\n" +
                    "DEFAULT CHARSET=utf8\n" +
                    "COLLATE=utf8_general_ci;\n");
            db.execute("CREATE TABLE IF NOT EXISTS multi_currency.player_currency (\n" +
                    "\tid INT auto_increment NOT NULL,\n" +
                    "\tcid varchar(50) NULL,\n" +
                    "\tplayerName varchar(50) NOT NULL,\n" +
                    "\tcount int NULL,\n" +
                    "\tCONSTRAINT player_currency_pk PRIMARY KEY (id)\n" +
                    ")\n" +
                    "ENGINE=MyISAM\n" +
                    "DEFAULT CHARSET=utf8\n" +
                    "COLLATE=utf8_general_ci;");
        } catch (SQLException e) {
            MessageUtil.sendMessage(Bukkit.getConsoleSender(), "&4数据库表初始化失败,请联系管理员修复");
            Bukkit.getPluginManager().disablePlugin(MultiCurrency.getInstance());
            e.printStackTrace();
        }
    }

    public boolean createCurrencyType(String name) {
        long id = IdUtil.getSnowflakeNextId();
        try {
            int inserted = db.insert(Entity.create("currency")
                    .set("cid", id)
                    .set("name", name));
            if (inserted > 0) {
                return true;
            }
            throw new AlreadyBoundException("货币已存在");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean removeCurrencyType(String name) {
        try {
            int delled = db.del(Entity.create("currency")
                    .set("name", name));
            Long cid = getCurrencyCid(name);
            int delled1 = db.del(Entity.create("player_currency")
                    .set("cid", cid));
            if (delled > 0 && delled1 > 0) {
                return true;
            }
            throw new AlreadyBoundException("货币已存在");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean removeCurrencyType(long id) {
        try {
            int delled = db.del(Entity.create("currency")
                    .set("cid", id));
            int delled1 = db.del(Entity.create("player_currency")
                    .set("cid", id));
            if (delled > 0 && delled1 > 0) {
                return true;
            }
            throw new AlreadyBoundException("货币已存在");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean clearCurrencyType() {
        try {
            db.execute("TRUNCATE TABLE currency");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getCurrencyName(long cid) {
        try {
            List<Entity> entities = db.find(Entity.create("currency")
                    .set("cid", cid));
            return entities.get(0).getStr("name");
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Entity> listCurrencyType() {
        try {
            return db.findAll(Entity.create("currency"));
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<Long> listCurrencyTypeCid() {
        List<Entity> entities = listCurrencyType();
        List<Long> cids = new ArrayList<>();
        for (Entity entity : entities) {
            cids.add(entity.getLong("cid"));
        }
        return cids;
    }

    public Long getCurrencyCid(String name) {
        try {
            List<Entity> entities = db.find(Entity.create("currency").set("name", name));
            if (entities.size() == 0) {
                return null;
            }
            return entities.get(0).getLong("cid");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean giveCurrency(String playerName, long cid, int count) {
        try {
            List<Entity> entities = db.find(Entity.create("player_currency")
                    .set("playerName", playerName)
                    .set("cid", cid));
            if (entities.size() == 0) {
                int inserted = db.insert(Entity.create("player_currency")
                        .set("cid", cid)
                        .set("playerName", playerName)
                        .set("count", 0));
                if (inserted == 0) {
                    return false;
                }
            }
            entities = db.find(Entity.create("player_currency"));
            Integer hasCount = entities.get(0).getInt("count");
            int targetCount = hasCount + count;
            int updated = db.update(Entity.create("player_currency")
                            .set("count", targetCount),
                    Entity.create("player_currency")
                            .set("playerName", playerName)
                            .set("cid", cid));
            return updated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean takeCurrency(String playerName, long cid, int count, CommandSender sender) {
        try {
            List<Entity> entities = db.find(Entity.create("player_currency")
                    .set("playerName", playerName)
                    .set("cid", cid));
            if (entities.size() == 0) {
                return false;
            }
            Integer hasCount = entities.get(0).getInt("count");
            if (count > hasCount) {
                MessageUtil.sendMessage(sender, "&4库存不足");
                return false;
            }
            int targetCount = hasCount - count;
            int updated = db.update(Entity.create("player_currency")
                    .set("count", targetCount), Entity.create("player_currency")
                    .set("playerName", playerName)
                    .set("cid", cid));
            return updated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean setCurrency(String playerName, long cid, int count) {
        try {
            int updated = db.update(Entity.create("player_currency")
                    .set("count", count), Entity.create("player_currency")
                    .set("playerName", playerName)
                    .set("cid", cid));
            if (updated > 0) {
                return true;
            }
            throw new AlreadyBoundException("货币更新失败");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Entity> getCurrency(String playerName) {
        try {
            List<Entity> entities = db.findAll(Entity.create("player_currency")
                    .set("playerName", playerName));
            if (entities.size() == 0) {
                List<Long> cids = listCurrencyTypeCid();
                for (Long cid : cids) {
                    setCurrency(playerName, cid, 0);
                }
            }
            entities = db.findAll(Entity.create("player_currency")
                    .set("playerName", playerName));
            return entities;
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
